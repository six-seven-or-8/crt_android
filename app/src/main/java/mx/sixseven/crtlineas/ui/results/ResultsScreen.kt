package mx.sixseven.crtlineas.ui.results

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import mx.sixseven.crtlineas.data.Companies
import mx.sixseven.crtlineas.model.*
import mx.sixseven.crtlineas.ui.theme.CRTColors
import mx.sixseven.crtlineas.util.Phrases

// ══════════════════════════════════════════════════════════
// ResultsScreen.kt — CRT Líneas Android
//
// Muestra resultados agrupados:
// - Con números registrados 🟢
// - Sin números registrados ⚪
// - Portales con error 🔴
// - Portales con error conocido 🟡
//
// Al terminar la consulta: frase de donación + botón Ko-fi
// Botón "Ver otra frase" con lógica de keepReading a los 3+
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

@Composable
fun ResultsScreen(
    results:      Map<String, QueryResult>,
    isDone:       Boolean,
    onClearAll:   () -> Unit,
) {
    val context = LocalContext.current

    // Frase de donación
    var donationPhrase by remember { mutableStateOf(Phrases.getDonation()) }
    var phraseClicks   by remember { mutableIntStateOf(0) }
    var phraseKey      by remember { mutableIntStateOf(0) }

    // Agrupar resultados
    val withPhones  = results.values.filter { it.found }
    val noPhones    = results.values.filter { !it.found && it.status == ResultStatus.OK }
    val withErrors  = results.values.filter { it.status == ResultStatus.ERROR }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        // ── Header con conteo ──────────────────────────────
        ResultsHeader(
            found  = withPhones.size,
            ok     = noPhones.size,
            errors = withErrors.size,
            isDone = isDone,
        )

        // ── Frase de donación (solo cuando terminó) ────────
        if (isDone && results.isNotEmpty()) {
            DonationCard(
                phrase    = donationPhrase,
                phraseKey = phraseKey,
                onNewPhrase = {
                    phraseClicks++
                    donationPhrase = if (phraseClicks >= 3) {
                        Phrases.getKeepReading()
                    } else {
                        Phrases.getDonation()
                    }
                    phraseKey++
                },
                onKofi = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/sixseven8"))
                    context.startActivity(intent)
                },
            )
        }

        // ── Sección: Con números ───────────────────────────
        if (withPhones.isNotEmpty()) {
            ResultSection(
                title  = "Con números registrados",
                count  = withPhones.size,
                color  = CRTColors.Verde700,
                emoji  = "⚠️",
            ) {
                withPhones.forEach { result ->
                    ResultCard(result)
                }
            }
        }

        // ── Sección: Sin números ───────────────────────────
        if (noPhones.isNotEmpty()) {
            ResultSection(
                title  = "Sin números registrados",
                count  = noPhones.size,
                color  = CRTColors.Azul800,
                emoji  = "✅",
            ) {
                noPhones.forEach { result ->
                    ResultCard(result)
                }
            }
        }

        // ── Sección: Con error en tiempo real ──────────────
        if (withErrors.isNotEmpty()) {
            ResultSection(
                title  = "Portales con error",
                count  = withErrors.size,
                color  = CRTColors.Rojo600,
                emoji  = "❌",
            ) {
                withErrors.forEach { result ->
                    ResultCard(result)
                }
            }
        }

        // ── Sección: Errores conocidos ─────────────────────
        if (Companies.errorCompanies.isNotEmpty()) {
            KnownErrorsSection()
        }

        // ── Vacío ──────────────────────────────────────────
        if (results.isEmpty()) {
            EmptyState()
        }

        // ── Botón limpiar ──────────────────────────────────
        if (results.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick  = onClearAll,
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = CRTColors.Rojo600,
                ),
                border = BorderStroke(1.dp, CRTColors.Rojo600),
                shape  = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Borrar resultados")
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Subcomponentes ─────────────────────────────────────────

@Composable
private fun ResultsHeader(found: Int, ok: Int, errors: Int, isDone: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip("✅ $ok sin número",   CRTColors.Azul800,   Modifier.weight(1f))
        StatChip("⚠️ $found con línea", CRTColors.Verde700,  Modifier.weight(1f))
        StatChip("❌ $errors error",    CRTColors.Rojo600,   Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(10.dp),
        color    = color.copy(alpha = 0.12f),
    ) {
        Text(
            text      = label,
            style     = MaterialTheme.typography.labelSmall.copy(
                color      = color,
                fontWeight = FontWeight.SemiBold,
            ),
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
        )
    }
}

// Palabras clave que indican que la frase habla de donar
private fun phraseInvitesDonation(phrase: String): Boolean {
    val keywords = listOf("donar", "donación", "donaciones", "ko-fi", "kofi",
        "ko fi", "contribui", "apoya", "apoyo", "contribución",
        "personitas", "café", "presupuesto", "finanzas", "salarios")
    val lower = phrase.lowercase()
    return keywords.any { lower.contains(it) }
}

@Composable
private fun DonationCard(
    phrase:       String,
    phraseKey:    Int,
    onNewPhrase:  () -> Unit,
    onKofi:       () -> Unit,
) {
    val showKofi = phraseInvitesDonation(phrase)

    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = CRTColors.Naranja600.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, CRTColors.Naranja600.copy(alpha = 0.3f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AnimatedContent(
                targetState  = phraseKey,
                transitionSpec = {
                    fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                },
                label = "donation_phrase",
            ) {
                Text(
                    text  = phrase,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color      = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp,
                    ),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Ko-fi — arriba, solo visible si la frase invita a donar
                if (showKofi) {
                    Button(
                        onClick  = onKofi,
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = CRTColors.Naranja600,
                            contentColor   = CRTColors.Blanco,
                        ),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text("☕ Donar en Ko-fi", style = MaterialTheme.typography.labelLarge)
                    }
                }

                // Botón de frases — siempre visible abajo
                OutlinedButton(
                    onClick  = onNewPhrase,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = CRTColors.Naranja600,
                    ),
                    border = BorderStroke(1.dp, CRTColors.Naranja600.copy(alpha = 0.5f)),
                    shape  = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text  = "¿Qué más dicen las personitas? 👀",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSection(
    title:   String,
    count:   Int,
    color:   androidx.compose.ui.graphics.Color,
    emoji:   String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(emoji, style = MaterialTheme.typography.titleMedium)
            Text(
                text  = "$title ($count)",
                style = MaterialTheme.typography.titleMedium.copy(
                    color      = color,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        content()
    }
}

@Composable
private fun ResultCard(result: QueryResult) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Surface(
        shape  = RoundedCornerShape(12.dp),
        color  = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text  = result.companyName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                if (result.viaApi) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CRTColors.Azul800.copy(alpha = 0.12f),
                    ) {
                        Text(
                            text     = "API",
                            style    = MaterialTheme.typography.labelSmall.copy(
                                color      = CRTColors.Azul800,
                                fontWeight = FontWeight.Bold,
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            if (result.phones.isNotEmpty()) {
                result.phones.forEach { phone ->
                    Text(
                        text  = "📱 $phone",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color      = CRTColors.Verde700,
                        ),
                    )
                }
            }

            if (result.status == ResultStatus.ERROR) {
                Text(
                    text  = result.errorMsg,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CRTColors.Rojo600,
                    ),
                )
            }

            // URL para consulta manual — siempre visible si existe
            if (result.url.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text  = result.url
                            .removePrefix("https://")
                            .removePrefix("http://")
                            .trimEnd('/'),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                    TextButton(
                        onClick = {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse(result.url)
                            )
                            context.startActivity(intent)
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 8.dp, vertical = 2.dp
                        ),
                    ) {
                        Text(
                            text  = "Ver en web",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CRTColors.Azul800,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KnownErrorsSection() {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape   = RoundedCornerShape(12.dp),
            color   = CRTColors.Naranja600.copy(alpha = 0.08f),
            border  = BorderStroke(1.dp, CRTColors.Naranja600.copy(alpha = 0.25f)),
            onClick = { expanded = !expanded },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text  = "🟡 Portales con error conocido (${Companies.errorCompanies.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color      = CRTColors.Naranja600,
                        ),
                    )
                    Text(
                        text  = "Desde 28/04/2026 · Monitoreados constantemente",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CRTColors.Naranja600,
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Companies.errorCompanies.forEach { co ->
                    ErrorCompanyCard(co)
                }
            }
        }
    }
}

private val errorMsgMap = mapOf(
    "err.proximamente" to "El portal muestra \"Próximamente\". El servicio no está habilitado aún.",
    "err.403"          to "Error 403. El portal bloquea el acceso externo.",
    "err.522"          to "Error 522 de Cloudflare. El servidor del portal está caído.",
    "err.403_vtl"      to "Error 403. VinculaTuLinea bloquea el acceso directo a esta ruta.",
    "err.timeout"      to "Timeout de conexión. El portal no responde.",
)

@Composable
private fun ErrorCompanyCard(co: mx.sixseven.crtlineas.model.ErrorCompany) {
    val context   = androidx.compose.ui.platform.LocalContext.current
    val errorText = errorMsgMap[co.errorMsgKey] ?: co.errorMsgKey
    Surface(
        shape  = RoundedCornerShape(10.dp),
        color  = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        onClick = {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(co.url))
            context.startActivity(intent)
        },
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text  = co.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text  = "Error conocido desde: ${co.knownSince}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CRTColors.Naranja600, fontWeight = FontWeight.Medium,
                ),
            )
            Text(
                text  = errorText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            Text(
                text  = co.url,
                style = MaterialTheme.typography.labelSmall.copy(color = CRTColors.Azul800),
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("🔍", style = MaterialTheme.typography.displayLarge)
            Text(
                text      = "Aún no hay resultados.\nVe a Inicio y comienza.",
                style     = MaterialTheme.typography.bodyLarge.copy(
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}
