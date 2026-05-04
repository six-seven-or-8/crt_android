package mx.sixseven.crtlineas.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.sixseven.crtlineas.ui.theme.CRTColors
import mx.sixseven.crtlineas.util.Phrases

// ══════════════════════════════════════════════════════════
// SplashScreen.kt — CRT Líneas Android
//
// Pantalla de bienvenida con:
// - Frase aleatoria del pool de 60
// - Botón "Ver otra frase"
// - Después de 3 clicks: frases del pool especial de 60
// - Botón principal para entrar a la app
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

@Composable
fun SplashScreen(onEnter: () -> Unit) {
    var currentPhrase  by remember { mutableStateOf(Phrases.getWelcome()) }
    var clickCount     by remember { mutableIntStateOf(0) }
    var isKeepReading  by remember { mutableStateOf(false) }

    // Animación de fade al cambiar frase
    var phraseKey by remember { mutableIntStateOf(0) }

    // Gradiente de fondo: azul profundo hacia azul oscuro
    val gradient = Brush.verticalGradient(
        colors = listOf(CRTColors.Azul900, CRTColors.Azul800.copy(alpha = 0.9f))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {

            // ── Logo / Badge ───────────────────────────────
            Spacer(Modifier.height(16.dp))

            Surface(
                shape  = RoundedCornerShape(16.dp),
                color  = CRTColors.Naranja600,
                shadowElevation = 8.dp,
            ) {
                Text(
                    text  = "6-7",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight   = FontWeight.Black,
                        fontSize     = 42.sp,
                        color        = CRTColors.Blanco,
                        letterSpacing = (-1).sp,
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }

            // ── Título ─────────────────────────────────────
            Text(
                text      = "Consulta Líneas CRT",
                style     = MaterialTheme.typography.headlineLarge.copy(
                    color      = CRTColors.Blanco,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
            )

            Text(
                text      = "por Six-Seven · México 🇲🇽",
                style     = MaterialTheme.typography.bodyMedium.copy(
                    color = CRTColors.Blanco.copy(alpha = 0.65f),
                ),
                textAlign = TextAlign.Center,
            )

            // ── Frase aleatoria ────────────────────────────
            AnimatedContent(
                targetState = phraseKey,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
                },
                label = "phrase_anim",
            ) {
                Surface(
                    shape  = RoundedCornerShape(16.dp),
                    color  = CRTColors.Blanco.copy(alpha = 0.08f),
                ) {
                    Text(
                        text      = currentPhrase,
                        style     = MaterialTheme.typography.bodyLarge.copy(
                            color      = CRTColors.Blanco.copy(alpha = 0.9f),
                            lineHeight = 24.sp,
                        ),
                        textAlign = TextAlign.Start,
                        modifier  = Modifier.padding(20.dp),
                    )
                }
            }

            // ── Botón "Ver otra frase" ─────────────────────
            TextButton(
                onClick = {
                    clickCount++
                    isKeepReading = clickCount >= 3
                    currentPhrase = if (isKeepReading) {
                        Phrases.getKeepReading()
                    } else {
                        Phrases.getWelcome()
                    }
                    phraseKey++
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = CRTColors.Naranja600,
                ),
            ) {
                Text(
                    text  = if (isKeepReading) "Ver otra más... 👀" else "Ver otra frase",
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Botón principal ────────────────────────────
            Button(
                onClick = onEnter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CRTColors.Naranja600,
                    contentColor   = CRTColors.Blanco,
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text  = "Comenzar consulta",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            // ── Nota de privacidad ─────────────────────────
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text  = "🔒 ",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text      = "Datos cifrados · Expiran en 24h · Sin servidores propios",
                    style     = MaterialTheme.typography.bodySmall.copy(
                        color = CRTColors.Blanco.copy(alpha = 0.5f),
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
