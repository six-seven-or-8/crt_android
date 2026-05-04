package mx.sixseven.crtlineas.ui.manual

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.sixseven.crtlineas.data.Companies
import mx.sixseven.crtlineas.model.ManualCompany
import mx.sixseven.crtlineas.ui.theme.CRTColors

// ══════════════════════════════════════════════════════════
// ManualScreen.kt — CRT Líneas Android
//
// Portales que requieren datos adicionales que solo el usuario
// conoce: contraseña de cuenta, correo electrónico, INE, etc.
//
// Estos portales son opcionales. Si no tienes cuenta ahí,
// no puedes verificar si hay líneas. El sistema es así.
// No lo diseñamos nosotros. Solo lo documentamos.
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

// Mapa de claves i18n a textos en español (simplificado para la app)
// En la versión completa esto vendría del sistema de i18n
private val noteTexts = mapOf(
    "man.note.webcomp"     to "El portal usa componentes web propietarios que impiden el pegado automático de datos. Ingresa tu CURP manualmente. Incluye slider CAPTCHA.",
    "man.note.phone_abib"  to "Solicita un número de teléfono ABIB. Solo usuarios con una línea ABIB pueden verificar.",
    "man.note.phone_abib_bien" to "Solicita un número de teléfono ABIB Bienestar. Solo usuarios con una línea ABIB Bienestar pueden verificar.",
    "man.note.user_pass"   to "Requiere usuario y contraseña de cuenta propia en este portal.",
    "man.note.email_curp"  to "Requiere correo electrónico además del CURP para identificarte.",
    "man.note.id_oficial"  to "Requiere identificación oficial vigente (INE, pasaporte u otro documento). Incluye slider CAPTCHA.",
    "man.note.biometrico"  to "Error 403 al acceder. Puede requerir verificación biométrica (selfie + INE).",
    "man.note.email_vinc"  to "Solicita el correo electrónico que usaste al vincular la línea. Si alguien la registró con un correo desconocido, no podrás verificarla.",
)

private val credTexts = mapOf(
    "man.cred.curp_slider"        to "CURP + ajustar barra deslizante",
    "man.cred.phone_abib"         to "Número de teléfono ABIB",
    "man.cred.phone_abib_bien"    to "Número de teléfono ABIB Bienestar",
    "man.cred.user_pass_bestel"   to "Usuario y contraseña de cuenta Bestel",
    "man.cred.curp_email"         to "CURP + correo electrónico",
    "man.cred.user_pass_izzi"     to "Usuario y contraseña de cuenta Izzi",
    "man.cred.id_oficial"         to "Identificación oficial vigente",
    "man.cred.user_pass_redphone" to "Usuario y contraseña de cuenta Redphone Koonol",
    "man.cred.user_pass_sky"      to "Usuario y contraseña de cuenta Sky",
    "man.cred.biometrico"         to "Verificación biométrica (selfie + INE)",
    "man.cred.email_vinc"         to "Correo electrónico de vinculación",
    "man.cred.user_pass_yumovil"  to "Usuario y contraseña de cuenta Yu Movil",
)

@Composable
fun ManualScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        // ── Header ─────────────────────────────────────────
        Text(
            text  = "Portales manuales",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
        )

        // ── Hint ───────────────────────────────────────────
        Surface(
            shape  = RoundedCornerShape(12.dp),
            color  = CRTColors.Naranja600.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, CRTColors.Naranja600.copy(alpha = 0.25f)),
        ) {
            Text(
                text = "Estos portales requieren datos adicionales por lo que, si no tienes una cuenta " +
                       "con esas compañías, no es posible revisar si hay líneas registradas con tus datos " +
                       "o si no puedes brindar los datos de tu identificación oficial. Por lo anterior, " +
                       "estas consultas son opcionales y deben realizarse de forma manual.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier.padding(14.dp),
            )
        }

        // ── Lista de portales manuales ─────────────────────
        Companies.manualCompanies.forEach { company ->
            ManualCompanyCard(
                company = company,
                onOpen  = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(company.url))
                    context.startActivity(intent)
                },
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ManualCompanyCard(
    company: ManualCompany,
    onOpen:  () -> Unit,
) {
    val note = noteTexts[company.noteKey] ?: company.noteKey
    val cred = credTexts[company.credKey] ?: company.credKey

    Surface(
        shape           = RoundedCornerShape(14.dp),
        color           = MaterialTheme.colorScheme.surface,
        border          = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 1.dp,
        onClick         = onOpen,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Nombre del portal
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text  = company.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color      = CRTColors.Azul800,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector       = Icons.Default.OpenInNew,
                    contentDescription = "Abrir portal",
                    tint              = CRTColors.Azul800,
                    modifier          = Modifier.size(18.dp),
                )
            }

            // Descripción
            Text(
                text  = note,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )

            // Credenciales requeridas
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CRTColors.Azul800.copy(alpha = 0.08f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text  = "Necesitas:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color      = CRTColors.Azul800,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text  = cred,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CRTColors.Azul800,
                        ),
                    )
                }
            }

            // URL
            Text(
                text  = company.url,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
