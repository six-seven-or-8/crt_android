package mx.sixseven.crtlineas.ui.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.sixseven.crtlineas.ui.theme.CRTColors

// ══════════════════════════════════════════════════════════
// AboutScreen.kt — CRT Líneas Android
//
// Información de la app: versión, licencia, privacidad,
// donaciones (Ko-fi + crypto), compartir.
//
// Igual que la pestaña Acerca de la extensión pero con
// botones táctiles y acceso directo a Ko-fi.
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        // ── Logo + título ──────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape  = RoundedCornerShape(20.dp),
                color  = CRTColors.Naranja600,
                shadowElevation = 4.dp,
            ) {
                Text(
                    text  = "LS",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color      = CRTColors.Blanco,
                        fontSize   = 48.sp,
                    ),
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                )
            }
            Text(
                text  = "LineShield",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
            Text(
                text  = "Protege tu identidad telefónica",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }

        // ── Info table ─────────────────────────────────────
        AboutInfoCard()

        // ── Ko-fi ──────────────────────────────────────────
        DonationSection(context)

        // ── Crypto ─────────────────────────────────────────
        CryptoSection(context)

        // ── Compartir ──────────────────────────────────────
        ShareSection(context)

        // ── Nota Altán ─────────────────────────────────────
        AltanNote()

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AboutInfoCard() {
    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            listOf(
                "Versión"       to "1.0.0",
                "Autor"         to "Six-Seven",
                "Licencia"      to "MIT — código abierto",
                "Privacidad"    to "Sin servidores externos. Todo local. Expira en 24h.",
            ).forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text  = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color      = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    Text(
                        text  = value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier.weight(1f, fill = false),
                        textAlign = TextAlign.End,
                    )
                }
                if (label != "Privacidad") {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun DonationSection(context: Context) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Donaciones voluntarias ☕", "Apoya el desarrollo de la app")

        Button(
            onClick  = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/sixseven8"))
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CRTColors.Naranja600,
                contentColor   = CRTColors.Blanco,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text  = "☕ Apoyar en Ko-fi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        Text(
            text  = "ko-fi.com/sixseven8",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CryptoSection(context: Context) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Criptomonedas", "Vía Bitso · Envía solo la cripto correspondiente a cada red")

        listOf(
            Triple("XRP · Ripple",         "rLSn6Z3T8uCxbcd1oxwfGQN1Fdn5CyGujK",                                                                                    "Destination Tag: 11550963"),
            Triple("ADA · Cardano",        "addr1q9q48dvqwgfvrw6dhwhydushnd4qnfdsqxr2gxe93wkwk09sve5ted8m0wl99677rdgqrdhslk0g2l7skx2nrklpgdeqnhsyyr",             "Memo: No requerido"),
            Triple("XLM · Stellar",        "GA22MHPWUODDYFSQMQ3I6BJAHEJCDLEPOIYG5RP47LLIO3YV3KPSIVXV",                                                              "Memo ID: 11550963"),
        ).forEach { (label, address, tag) ->
            CryptoCard(label = label, address = address, tag = tag, context = context)
        }
    }
}

@Composable
private fun CryptoCard(label: String, address: String, tag: String, context: Context) {
    var copied by remember { mutableStateOf(false) }

    Surface(
        shape  = RoundedCornerShape(12.dp),
        color  = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color      = CRTColors.Azul800,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text  = address,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    ),
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("address", address))
                        copied = true
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copiar",
                        tint = if (copied) CRTColors.Verde700 else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Text(
                text  = tag,
                style = MaterialTheme.typography.bodySmall.copy(
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Composable
private fun ShareSection(context: Context) {
    val shareText = "Descubre si alguien registró líneas telefónicas a tu nombre sin permiso. " +
                    "App gratuita que revisa todos los portales del CRT en México. " +
                    "Protege tu identidad: https://github.com/six-seven/crt-lineas-android"
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Compartir", "Ayuda a otros a proteger su identidad")
        OutlinedButton(
            onClick  = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir CRT Líneas"))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CRTColors.Azul800),
            border = BorderStroke(1.dp, CRTColors.Azul800),
            shape  = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Compartir la app", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun AltanNote() {
    Surface(
        shape  = RoundedCornerShape(12.dp),
        color  = CRTColors.Azul800.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, CRTColors.Azul800.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text  = "🔑 Dato técnico: Altán Redes",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color      = CRTColors.Azul800,
                ),
            )
            Text(
                text  = "rnu.altanredes.com/consulta cubre aproximadamente 67 operadoras OMV " +
                        "de la red Altán en una sola consulta. Confirmado por análisis del tráfico " +
                        "de red: el servidor devuelve resultados de todas las OMVs asociadas sin " +
                        "necesidad de especificar cuál. Una consulta, 67 empresas.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text  = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}
