package mx.sixseven.crtlineas.ui.progress

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mx.sixseven.crtlineas.QueryPhase
import mx.sixseven.crtlineas.QueryUiState
import mx.sixseven.crtlineas.model.*
import mx.sixseven.crtlineas.ui.theme.CRTColors
import mx.sixseven.crtlineas.ui.webview.PortalWebView

// ══════════════════════════════════════════════════════════
// ProgressScreen.kt — CRT Líneas Android
//
// Muestra el progreso de la consulta:
// - Fase 1: Cards de APIs directas que aparecen al instante
// - Fase 2: WebView a pantalla completa para cada portal
//   (uno a la vez, con header que muestra el progreso)
//
// En la extensión se abren 5 pestañas simultáneas.
// En Android: 1 WebView a la vez, pantalla completa.
// El usuario resuelve el CAPTCHA, la app detecta el resultado
// y pasa al siguiente portal automáticamente.
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

@Composable
fun ProgressScreen(
    uiState:       QueryUiState,
    userData:      UserData,
    onWebviewDone: (String, List<String>) -> Unit,
    onWebviewError:(String, String) -> Unit,
) {
    val currentWebview = uiState.currentWebview
    val webviewTotal   = uiState.webviewQueue.size
    val webviewDone    = uiState.webviewDoneIds.size

    // Si hay un WebView activo, mostrarlo a pantalla completa
    if (currentWebview != null && uiState.phase == QueryPhase.RUNNING_WEBVIEW) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Barra de progreso WebView
            WebviewProgressBar(
                current = webviewDone + 1,
                total   = webviewTotal,
                name    = currentWebview.name,
            )
            // WebView
            PortalWebView(
                company  = currentWebview,
                userData = userData,
                onDone   = onWebviewDone,
                onError  = onWebviewError,
            )
        }
        return
    }

    // Vista de resumen con cards de API
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        // ── Header ─────────────────────────────────────────
        ProgressHeader(phase = uiState.phase, webviewDone = webviewDone, webviewTotal = webviewTotal)

        // ── Instrucciones ──────────────────────────────────
        if (uiState.phase == QueryPhase.RUNNING_WEBVIEW || uiState.phase == QueryPhase.RUNNING_APIS) {
            InstructionCard(phase = uiState.phase)
        }

        // ── Cards de resultados de API ─────────────────────
        if (uiState.results.isNotEmpty()) {
            Text(
                text  = "APIs directas (sin CAPTCHA)",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            uiState.results.values
                .filter { it.viaApi }
                .forEach { result -> ApiResultCard(result) }
        }

        // ── Portales WebView pendientes ────────────────────
        val pendingWebview = uiState.webviewQueue.filter { it.id !in uiState.webviewDoneIds }
        if (pendingWebview.isNotEmpty() && uiState.phase != QueryPhase.DONE) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Portales pendientes (${pendingWebview.size})",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            pendingWebview.forEach { company ->
                PendingPortalCard(company)
            }
        }

        // ── Portales WebView completados ───────────────────
        val doneWebview = uiState.webviewQueue.filter { it.id in uiState.webviewDoneIds }
        if (doneWebview.isNotEmpty()) {
            Text(
                text  = "Portales completados (${doneWebview.size})",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            doneWebview.forEach { company ->
                val result = uiState.results[company.id]
                if (result != null) ApiResultCard(result) else PendingPortalCard(company)
            }
        }

        // ── Mensaje de "listo" ─────────────────────────────
        if (uiState.phase == QueryPhase.DONE) {
            DoneCard(total = uiState.results.size)
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Subcomponentes ─────────────────────────────────────────

@Composable
private fun ProgressHeader(phase: QueryPhase, webviewDone: Int, webviewTotal: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (phase != QueryPhase.DONE && phase != QueryPhase.IDLE) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    color       = CRTColors.Naranja600,
                    strokeWidth = 2.dp,
                )
            } else if (phase == QueryPhase.DONE) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CRTColors.Verde700)
            }
            Text(
                text  = when (phase) {
                    QueryPhase.IDLE            -> "Sin consulta activa"
                    QueryPhase.RUNNING_APIS    -> "Consultando APIs directas..."
                    QueryPhase.RUNNING_WEBVIEW -> "Portales con formulario ($webviewDone/$webviewTotal)"
                    QueryPhase.DONE            -> "Consulta completada ✓"
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        if (webviewTotal > 0 && phase != QueryPhase.DONE) {
            LinearProgressIndicator(
                progress      = { webviewDone.toFloat() / webviewTotal.toFloat() },
                modifier      = Modifier.fillMaxWidth(),
                color         = CRTColors.Naranja600,
                trackColor    = CRTColors.Naranja600.copy(alpha = 0.2f),
            )
        }
    }
}

@Composable
private fun WebviewProgressBar(current: Int, total: Int, name: String) {
    Surface(
        color = CRTColors.Azul900,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text  = "Portal $current de $total",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = CRTColors.Blanco.copy(alpha = 0.7f),
                ),
            )
            LinearProgressIndicator(
                progress   = { current.toFloat() / total.toFloat() },
                modifier   = Modifier.fillMaxWidth(),
                color      = CRTColors.Naranja600,
                trackColor = CRTColors.Blanco.copy(alpha = 0.2f),
            )
        }
    }
}

@Composable
private fun InstructionCard(phase: QueryPhase) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CRTColors.Azul800.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, CRTColors.Azul800.copy(alpha = 0.2f)),
    ) {
        Text(
            text  = when (phase) {
                QueryPhase.RUNNING_APIS    ->
                    "🚀 Consultando APIs directas en paralelo. Sin captchas. Resultado en segundos."
                QueryPhase.RUNNING_WEBVIEW ->
                    "📋 Se abrirá cada portal dentro de la app con tus datos ya rellenados.\n" +
                    "Resuelve el CAPTCHA si aparece y da clic en Buscar.\n" +
                    "La app detecta el resultado y pasa al siguiente automáticamente."
                else -> ""
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
            modifier = Modifier.padding(14.dp),
        )
    }
}

@Composable
private fun ApiResultCard(result: QueryResult) {
    val color = when {
        result.status == ResultStatus.ERROR -> CRTColors.Rojo600
        result.found                        -> CRTColors.Verde700
        else                                -> CRTColors.Azul800
    }
    Surface(
        shape  = RoundedCornerShape(10.dp),
        color  = color.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = result.companyName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                if (result.found) {
                    Text(
                        text  = "${result.phones.size} línea(s): ${result.phones.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall.copy(color = CRTColors.Verde700),
                    )
                } else if (result.status == ResultStatus.ERROR) {
                    Text(
                        text  = result.errorMsg,
                        style = MaterialTheme.typography.bodySmall.copy(color = CRTColors.Rojo600),
                    )
                } else {
                    Text(
                        text  = "Sin números registrados",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = color.copy(alpha = 0.15f),
            ) {
                Text(
                    text     = if (result.viaApi) "API" else "WEB",
                    style    = MaterialTheme.typography.labelSmall.copy(
                        color = color, fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun PendingPortalCard(company: mx.sixseven.crtlineas.model.Company) {
    Surface(
        shape  = RoundedCornerShape(10.dp),
        color  = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = company.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = "Pendiente",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun DoneCard(total: Int) {
    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = CRTColors.Verde700.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, CRTColors.Verde700.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("✅", style = MaterialTheme.typography.displayLarge)
            Text(
                text      = "Consulta completada",
                style     = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = CRTColors.Verde700,
                ),
                textAlign = TextAlign.Center,
            )
            Text(
                text      = "$total portales consultados.\nVe a la pestaña Resultados para ver el detalle.",
                style     = MaterialTheme.typography.bodyMedium.copy(
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}
