package mx.sixseven.crtlineas.ui.webview

import android.annotation.SuppressLint
import android.webkit.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.*
import mx.sixseven.crtlineas.model.*
import mx.sixseven.crtlineas.ui.theme.CRTColors
import org.json.JSONObject

// ══════════════════════════════════════════════════════════
// SilentWebView.kt — Consulta WebView invisible para el usuario
// Muestra una pantalla de carga mientras el WebView trabaja
// en segundo plano. Ideal para portales Angular como VTL.
// ══════════════════════════════════════════════════════════

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SilentWebView(
    company:  Company,
    userData: UserData,
    onDone:   (companyId: String, phones: List<String>) -> Unit,
    onError:  (companyId: String, msg: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var pollJob by remember { mutableStateOf<Job?>(null) }
    var isDone by remember { mutableStateOf(false) }

    // Animación de rotación para el ícono de carga
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "rotation"
    )

    key(company.id) {
        Box(modifier = Modifier.fillMaxSize()) {

            // WebView oculto — trabaja en background
            AndroidView(
                modifier = Modifier.size(1.dp), // prácticamente invisible
                factory  = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled    = true
                            domStorageEnabled    = true
                            useWideViewPort      = true
                            loadWithOverviewMode = true
                            userAgentString      =
                                "Mozilla/5.0 (Linux; Android 14; Pixel 8) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/124.0.0.0 Mobile Safari/537.36"
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String) {
                                scope.launch {
                                    // Angular necesita tiempo extra para renderizar
                                    delay(3500)
                                    val script = ContentScript.getScript(company.id, userData)
                                    if (script != null) {
                                        view.evaluateJavascript(script) { }
                                    }
                                    // Sondear resultados
                                    pollJob?.cancel()
                                    pollJob = launch {
                                        pollSilent(view, company) { phones ->
                                            if (!isDone) {
                                                isDone = true
                                                onDone(company.id, phones)
                                            }
                                        }
                                    }
                                }
                            }
                            override fun onReceivedError(
                                view: WebView,
                                request: WebResourceRequest,
                                error: WebResourceError,
                            ) {
                                if (request.isForMainFrame && !isDone) {
                                    isDone = true
                                    pollJob?.cancel()
                                    onError(company.id, "Error ${error.errorCode}")
                                }
                            }
                        }
                        loadUrl(company.url)
                    }
                },
                update = { }
            )

            // Pantalla de carga fake — UX psicológico clásico
            FakeProgressScreen(companyName = company.name)
        }
    }
}

private suspend fun pollSilent(
    webView:  WebView,
    company:  Company,
    onFound:  (List<String>) -> Unit,
) {
    val extractor = ContentScript.getResultExtractor(company.id)
    // Para VTL buscar el mensaje de "sin líneas" o la lista de líneas
    val vtlChecker = """
        (function() {
          var body = document.body ? (document.body.innerText || '') : '';
          // Mensajes de "sin líneas" del portal VTL
          var noLines = body.includes('No hay información') ||
                        body.includes('no cuenta con líneas') ||
                        body.includes('no encontró') ||
                        body.includes('No se encontr') ||
                        body.includes('error al capturar') ||
                        body.includes('Búsqueda fallida') ||
                        body.includes('Sin líneas') ||
                        body.includes('no tiene líneas') ||
                        body.includes('No subscriptions');
          // Mensajes de éxito con líneas
          var phones = [];
          var matches = body.match(/\b\d{10}\b/g) || [];
          matches.forEach(function(m) {
            if (phones.indexOf(m) < 0) phones.push(m);
          });
          // También verificar si ya se mostró la pantalla de resultados
          // (el select #personType ya no está visible = consulta enviada)
          var formGone = !document.querySelector('#personType') ||
                         document.querySelector('#personType') === null;
          var hasResults = document.querySelector('[class*="result"], [class*="subscription"], [class*="lista"]') !== null;
          var done = noLines || phones.length > 0 || (formGone && hasResults);
          return JSON.stringify({ done: done, phones: phones, noLines: noLines });
        })();
    """.trimIndent()

    // Timeout máximo de 20 segundos — si no hay respuesta, asumir sin líneas
    repeat(80) {  // máximo 60 segundos (80 × 750ms)
        delay(750)
        var finished = false
        withContext(kotlinx.coroutines.Dispatchers.Main) {
            webView.evaluateJavascript(vtlChecker) { result ->
                runCatching {
                    val clean = result?.trim('"')
                        ?.replace("\\\"", "\"")
                        ?.replace("\\n", "") ?: return@evaluateJavascript
                    val obj = JSONObject(clean)
                    if (obj.optBoolean("done", false)) {
                        val phones = mutableListOf<String>()
                        val arr = obj.optJSONArray("phones")
                        if (arr != null) {
                            for (i in 0 until arr.length()) {
                                val p = arr.optString(i)
                                if (p.isNotBlank()) phones.add(p)
                            }
                        }
                        onFound(phones)
                        finished = true
                    }
                }
            }
        }
        if (finished) return
    }
    // Timeout — asumir sin líneas y continuar
    onFound(emptyList())
}

// ══════════════════════════════════════════════════════════
// FakeProgressScreen — UX psicológico clásico
// Barra de progreso que avanza sola + texto tipo "matrix"
// para que el usuario sienta que la consulta está viva
// ══════════════════════════════════════════════════════════
@Composable
private fun FakeProgressScreen(companyName: String) {
    // rememberSaveable mantiene el estado cuando el usuario cambia de pestaña
    var fakeProgress by rememberSaveable { mutableStateOf(0f) }
    var statusText   by rememberSaveable { mutableStateOf("Iniciando conexión segura…") }
    var hexText      by remember { mutableStateOf("") }

    val statusMessages = listOf(
        "Iniciando conexión segura…",
        "Autenticando con el portal…",
        "Enviando solicitud al CRT…",
        "Esperando respuesta del servidor…",
        "Procesando datos de vinculación…",
        "Verificando registros…",
        "Casi listo…",
    )

    // Avanzar progreso
    LaunchedEffect(Unit) {
        var msgIdx = 0
        // Fase 1: avanzar rápido hasta 85%
        while (fakeProgress < 0.85f) {
            delay(120)
            fakeProgress = (fakeProgress + (0.008f + (Math.random() * 0.012f).toFloat()))
                .coerceAtMost(0.85f)
            // Cambiar mensaje cada ~15%
            val newIdx = ((fakeProgress / 0.85f) * (statusMessages.size - 1)).toInt()
                .coerceAtMost(statusMessages.size - 1)
            if (newIdx != msgIdx) {
                msgIdx = newIdx
                statusText = statusMessages[msgIdx]
            }
        }
        // Fase 2: pulsar lentamente entre 85-92% esperando respuesta real
        while (true) {
            delay(800)
            fakeProgress = (0.85f + (Math.random() * 0.07f).toFloat()).coerceAtMost(0.92f)
        }
    }

    // Texto hex tipo "matrix" que cambia constantemente
    LaunchedEffect(Unit) {
        val chars = "0123456789ABCDEF"
        while (true) {
            delay(80)
            hexText = (1..32).map { chars.random() }.chunked(4).joinToString(" ")
        }
    }

    // Animación de rotación del escudo
    val infiniteTransition = rememberInfiniteTransition(label = "shield")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    // Animación suave de la barra
    val animatedProgress by animateFloatAsState(
        targetValue    = fakeProgress,
        animationSpec  = tween(300, easing = FastOutSlowInEasing),
        label          = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CRTColors.Azul900)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Escudo girando
        Text(
            text     = "🛡️",
            fontSize = 52.sp,
            modifier = Modifier.rotate(rotation),
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text  = "Consultando $companyName",
            style = MaterialTheme.typography.titleMedium.copy(
                color      = CRTColors.Blanco,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text  = statusText,
            style = MaterialTheme.typography.bodySmall.copy(
                color = CRTColors.Naranja600,
            ),
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Barra de progreso fake
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    CRTColors.Blanco.copy(alpha = 0.08f),
                    androidx.compose.foundation.shape.RoundedCornerShape(3.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(CRTColors.Azul800, CRTColors.Naranja600)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(3.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CRTColors.Blanco.copy(alpha = 0.4f),
                ),
            )
            Text(
                text  = "CRT • vinculatulinea.com",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CRTColors.Blanco.copy(alpha = 0.4f),
                ),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Texto hex tipo matrix — da sensación de actividad
        Text(
            text  = hexText,
            style = MaterialTheme.typography.labelSmall.copy(
                color      = CRTColors.Blanco.copy(alpha = 0.6f),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize   = 10.sp,
                letterSpacing = 2.sp,
            ),
        )

        Spacer(modifier = Modifier.height(48.dp))


    }
}
