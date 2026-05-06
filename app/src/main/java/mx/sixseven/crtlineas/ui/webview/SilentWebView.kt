package mx.sixseven.crtlineas.ui.webview

import android.annotation.SuppressLint
import android.webkit.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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

            // Pantalla de carga visible para el usuario
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CRTColors.Azul900),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Ícono de escudo girando
                Text(
                    text  = "🛡️",
                    fontSize = 56.sp,
                    modifier = Modifier.rotate(rotation),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text  = "Consultando ${company.name}…",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = CRTColors.Blanco,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = "Esto puede tardar unos segundos",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CRTColors.Blanco.copy(alpha = 0.5f),
                    ),
                )
                Spacer(modifier = Modifier.height(32.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(200.dp)
                        .height(3.dp),
                    color            = CRTColors.Naranja600,
                    trackColor       = CRTColors.Blanco.copy(alpha = 0.1f),
                )
            }
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
          var noLines = body.includes('No hay información') ||
                        body.includes('no cuenta con líneas') ||
                        body.includes('no encontró') ||
                        body.includes('No se encontr') ||
                        body.includes('error al capturar') ||
                        body.includes('Búsqueda fallida');
          var phones = [];
          var matches = body.match(/\b\d{10}\b/g) || [];
          matches.forEach(function(m) {
            if (phones.indexOf(m) < 0) phones.push(m);
          });
          var done = noLines || phones.length > 0;
          return JSON.stringify({ done: done, phones: phones, noLines: noLines });
        })();
    """.trimIndent()

    // Timeout máximo de 30 segundos
    repeat(40) {
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
    // Timeout — asumir sin líneas
    onFound(emptyList())
}
