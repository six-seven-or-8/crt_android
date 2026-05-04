package mx.sixseven.crtlineas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mx.sixseven.crtlineas.data.*
import mx.sixseven.crtlineas.model.*

// ══════════════════════════════════════════════════════════
// MainViewModel.kt — CRT Líneas Android
//
// Orquesta la consulta completa:
// 1. APIs directas en PARALELO (Weex, MoBig x2, YoMobile, IENTC, Sorcel)
// 2. WebViews en SECUENCIAL (1 a la vez en móvil, no 5 como la extensión)
//
// El estado es reactivo via StateFlow.
// SecureStorage persiste resultados entre rotaciones de pantalla.
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

data class QueryUiState(
    val phase:          QueryPhase = QueryPhase.IDLE,
    val results:        Map<String, QueryResult> = emptyMap(),
    val currentWebview: Company? = null,
    val webviewQueue:   List<Company> = emptyList(),
    val webviewDoneIds: Set<String> = emptySet(),
    val isDone:         Boolean = false,
    val error:          String? = null,
)

enum class QueryPhase {
    IDLE,           // Sin consulta activa
    RUNNING_APIS,   // APIs directas en paralelo
    RUNNING_WEBVIEW,// WebViews secuenciales
    DONE,           // Todo terminado
}

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val storage    = SecureStorage(app)
    private val apiService = ApiService()

    private val _state = MutableStateFlow(QueryUiState(
        results = storage.loadResults(),
        isDone  = !storage.checkSession().exists && storage.loadResults().isNotEmpty(),
    ))
    val state: StateFlow<QueryUiState> = _state.asStateFlow()

    // ── Iniciar consulta completa ──────────────────────────
    fun startQuery(userData: UserData) {
        viewModelScope.launch {
            storage.saveUserData(userData)
            storage.startSession()

            val apiList     = Companies.filterApiForUser(userData.personType, userData.citizenship)
            val webviewList = Companies.filterWebviewForUser(userData.personType, userData.citizenship)

            _state.update { it.copy(
                phase        = QueryPhase.RUNNING_APIS,
                results      = emptyMap(),
                webviewQueue = webviewList,
                isDone       = false,
            )}

            // ── Fase 1: APIs directas en paralelo ──────────
            runApiPhase(apiList, userData)

            // ── Fase 2: WebViews secuenciales ─────────────
            if (webviewList.isNotEmpty()) {
                _state.update { it.copy(phase = QueryPhase.RUNNING_WEBVIEW) }
                // El primer WebView se activa automáticamente — el siguiente
                // se dispara cuando la pantalla de progreso confirma que terminó
                startNextWebview()
            } else {
                finishQuery()
            }
        }
    }

    private suspend fun runApiPhase(companies: List<Company>, userData: UserData) {
        val jobs = companies.map { company ->
            viewModelScope.async(Dispatchers.IO) {
                val result = apiService.query(company.id, userData)
                val named  = result.copy(companyName = company.name)
                storage.saveResult(named)
                _state.update { current ->
                    current.copy(results = current.results + (named.companyId to named))
                }
            }
        }
        jobs.awaitAll()
    }

    // ── Control de WebViews ────────────────────────────────
    // El WebView activo se incrusta en ProgressScreen.
    // Cuando el usuario termina (o la app detecta el resultado),
    // llama a webviewDone() para pasar al siguiente.

    fun startNextWebview() {
        val current = _state.value
        val pending = current.webviewQueue.filter { it.id !in current.webviewDoneIds }
        if (pending.isEmpty()) {
            finishQuery()
            return
        }
        val next = pending.first()
        storage.setSessionIndex(current.webviewDoneIds.size)
        _state.update { it.copy(currentWebview = next) }
    }

    fun webviewDone(companyId: String, phones: List<String>) {
        val current = _state.value
        val company = current.webviewQueue.find { it.id == companyId }

        val result = QueryResult(
            companyId   = companyId,
            companyName = company?.name ?: companyId,
            phones      = phones,
            found       = phones.isNotEmpty(),
            url         = company?.url ?: "",
            status      = ResultStatus.OK,
            viaApi      = false,
        )
        storage.saveResult(result)

        val newDone = current.webviewDoneIds + companyId
        _state.update { it.copy(
            results       = it.results + (companyId to result),
            webviewDoneIds = newDone,
            currentWebview = null,
        )}

        val allWebviewIds = current.webviewQueue.map { it.id }.toSet()
        if (newDone.containsAll(allWebviewIds)) {
            finishQuery()
        } else {
            startNextWebview()
        }
    }

    fun webviewError(companyId: String, errorMsg: String) {
        val current = _state.value
        val company = current.webviewQueue.find { it.id == companyId }
        val result  = QueryResult(
            companyId   = companyId,
            companyName = company?.name ?: companyId,
            status      = ResultStatus.ERROR,
            errorMsg    = errorMsg,
            url         = company?.url ?: "",
            viaApi      = false,
        )
        storage.saveResult(result)
        val newDone = current.webviewDoneIds + companyId
        _state.update { it.copy(
            results        = it.results + (companyId to result),
            webviewDoneIds = newDone,
            currentWebview = null,
        )}
        val allWebviewIds = current.webviewQueue.map { it.id }.toSet()
        if (newDone.containsAll(allWebviewIds)) finishQuery() else startNextWebview()
    }

    private fun finishQuery() {
        storage.doneSession()
        _state.update { it.copy(
            phase          = QueryPhase.DONE,
            isDone         = true,
            currentWebview = null,
        )}
    }

    // ── Reanudar sesión ────────────────────────────────────
    fun resumeSession(userData: UserData) {
        val session = storage.checkSession()
        if (!session.exists) return

        val webviewList = Companies.filterWebviewForUser(userData.personType, userData.citizenship)
        val doneIds     = storage.loadResults()
            .filter { !it.value.viaApi }
            .keys.toSet()

        _state.update { it.copy(
            phase          = QueryPhase.RUNNING_WEBVIEW,
            results        = storage.loadResults(),
            webviewQueue   = webviewList,
            webviewDoneIds = doneIds,
        )}
        startNextWebview()
    }

    // ── Limpiar todo ───────────────────────────────────────
    fun clearAll() {
        storage.clearAll()
        _state.update { QueryUiState() }
    }

    // ── Datos persistidos ──────────────────────────────────
    fun loadUserData()    = storage.loadUserData()
    fun ttlRemainingMs()  = storage.ttlRemainingMs()
    fun checkSession()    = storage.checkSession()
}
