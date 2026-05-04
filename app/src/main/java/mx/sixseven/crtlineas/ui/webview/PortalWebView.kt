package mx.sixseven.crtlineas.ui.webview

import android.annotation.SuppressLint
import android.webkit.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.*
import mx.sixseven.crtlineas.model.*
import mx.sixseven.crtlineas.ui.theme.CRTColors
import org.json.JSONObject

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PortalWebView(
    company:  Company,
    userData: UserData,
    onDone:   (companyId: String, phones: List<String>) -> Unit,
    onError:  (companyId: String, msg: String) -> Unit,
) {
    var isLoading      by remember { mutableStateOf(true) }
    var isResultFound  by remember { mutableStateOf(false) }
    var detectedPhones by remember { mutableStateOf<List<String>>(emptyList()) }
    val scope          = rememberCoroutineScope()
    var pollJob        by remember { mutableStateOf<Job?>(null) }

    // KEY crítico: fuerza recrear el WebView completo cuando cambia el portal
    key(company.id) {
        Column(modifier = Modifier.fillMaxSize()) {

            WebViewHeader(
                companyName = company.name,
                isLoading   = isLoading,
                isFound     = isResultFound,
                phones      = detectedPhones,
                onDone      = {
                    pollJob?.cancel()
                    onDone(company.id, detectedPhones)
                },
            )

            AndroidView(
                modifier = Modifier.weight(1f),
                factory  = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled        = true
                            domStorageEnabled        = true
                            useWideViewPort          = true
                            loadWithOverviewMode     = true
                            mixedContentMode         = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                            userAgentString          =
                                "Mozilla/5.0 (Linux; Android 14; Pixel 8) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/124.0.0.0 Mobile Safari/537.36"
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String) {
                                isLoading = false

                                // Inyectar JS con delay para esperar que el DOM esté listo
                                scope.launch {
                                    delay(1200)
                                    val script = ContentScript.getScript(company.id, userData)
                                    if (script != null) {
                                        view.evaluateJavascript(script) { }
                                    }

                                    // Iniciar sondeo de resultados
                                    pollJob?.cancel()
                                    pollJob = launch {
                                        pollForResults(view, company.id) { phones ->
                                            detectedPhones = phones
                                            isResultFound  = true
                                        }
                                    }
                                }
                            }

                            override fun onReceivedError(
                                view: WebView,
                                request: WebResourceRequest,
                                error: WebResourceError,
                            ) {
                                if (request.isForMainFrame) {
                                    isLoading = false
                                    pollJob?.cancel()
                                    onError(company.id, "Error ${error.errorCode}")
                                }
                            }
                        }

                        loadUrl(company.url)
                    }
                },
                // update NO hace nada — el key() de arriba maneja la recreación
                update = { }
            )
        }
    }
}

private suspend fun pollForResults(
    webView:   WebView,
    companyId: String,
    onFound:   (List<String>) -> Unit,
) {
    val extractor = ContentScript.getResultExtractor(companyId)
    repeat(25) {
        delay(1500L)
        var done = false
        withContext(Dispatchers.Main) {
            webView.evaluateJavascript(extractor) { result ->
                val parsed = runCatching {
                    val clean = result?.trim('"')?.replace("\\\"", "\"")
                        ?.replace("\\n", "")?.replace("\\\\", "\\") ?: return@evaluateJavascript
                    JSONObject(clean)
                }.getOrNull() ?: return@evaluateJavascript

                if (parsed.optBoolean("done", false)) {
                    val phones = mutableListOf<String>()
                    val arr = parsed.optJSONArray("phones")
                    if (arr != null) {
                        for (i in 0 until arr.length()) {
                            val p = arr.optString(i)
                            if (p.isNotBlank()) phones.add(p)
                        }
                    }
                    onFound(phones)
                    done = true
                }
            }
        }
        if (done) return
    }
}

@Composable
private fun WebViewHeader(
    companyName: String,
    isLoading:   Boolean,
    isFound:     Boolean,
    phones:      List<String>,
    onDone:      () -> Unit,
) {
    Surface(
        color           = CRTColors.Azul800,
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = companyName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color      = CRTColors.Blanco,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text  = when {
                            isLoading                    -> "Cargando…"
                            isFound && phones.isEmpty()  -> "✓ Sin números registrados"
                            isFound                      -> "⚠ ${phones.size} número(s) encontrado(s)"
                            else                         -> "Completa el CAPTCHA y busca"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CRTColors.Blanco.copy(alpha = 0.8f),
                        ),
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = CRTColors.Naranja600,
                        strokeWidth = 2.dp,
                    )
                }
            }

            if (!isLoading) {
                Button(
                    onClick  = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFound) CRTColors.Verde700 else CRTColors.Naranja600,
                        contentColor   = CRTColors.Blanco,
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text  = if (isFound) "✓ Listo — Siguiente portal"
                                else         "Marcar como terminado y continuar",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
