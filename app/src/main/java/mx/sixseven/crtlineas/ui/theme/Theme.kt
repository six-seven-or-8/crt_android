package mx.sixseven.crtlineas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Paleta de colores ──────────────────────────────────────
// Misma identidad visual que la extensión de Chrome/Firefox
object CRTColors {
    val Azul900    = Color(0xFF0F1E4A)
    val Azul800    = Color(0xFF1E3A8A)  // Primary
    val Azul600    = Color(0xFF2563EB)
    val Azul100    = Color(0xFFEFF6FF)

    val Naranja600 = Color(0xFFF97316)  // Accent
    val Naranja100 = Color(0xFFFFF7ED)

    val Verde700   = Color(0xFF15803D)
    val Verde100   = Color(0xFFF0FDF4)

    val Rojo600    = Color(0xFFDC2626)
    val Rojo100    = Color(0xFFFEF2F2)

    val Gris50     = Color(0xFFF8FAFC)
    val Gris100    = Color(0xFFF1F5F9)
    val Gris200    = Color(0xFFE2E8F0)
    val Gris400    = Color(0xFF94A3B8)
    val Gris600    = Color(0xFF475569)
    val Gris900    = Color(0xFF0F172A)

    val Blanco     = Color(0xFFFFFFFF)
    val Negro      = Color(0xFF000000)
}

// ── Esquema de colores — Light ─────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = CRTColors.Azul800,
    onPrimary        = CRTColors.Blanco,
    primaryContainer = CRTColors.Azul100,
    onPrimaryContainer = CRTColors.Azul900,

    secondary        = CRTColors.Naranja600,
    onSecondary      = CRTColors.Blanco,
    secondaryContainer = CRTColors.Naranja100,
    onSecondaryContainer = Color(0xFF7C2D12),

    tertiary         = CRTColors.Verde700,
    onTertiary       = CRTColors.Blanco,

    error            = CRTColors.Rojo600,
    onError          = CRTColors.Blanco,
    errorContainer   = CRTColors.Rojo100,

    background       = CRTColors.Gris50,
    onBackground     = CRTColors.Gris900,
    surface          = CRTColors.Blanco,
    onSurface        = CRTColors.Gris900,
    surfaceVariant   = CRTColors.Gris100,
    onSurfaceVariant = CRTColors.Gris600,
    outline          = CRTColors.Gris200,
)

// ── Esquema de colores — Dark ──────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = CRTColors.Azul600,
    onPrimary        = CRTColors.Blanco,
    primaryContainer = CRTColors.Azul900,
    onPrimaryContainer = CRTColors.Azul100,

    secondary        = CRTColors.Naranja600,
    onSecondary      = CRTColors.Blanco,

    background       = Color(0xFF0A0F1E),
    onBackground     = CRTColors.Gris100,
    surface          = Color(0xFF111827),
    onSurface        = CRTColors.Gris100,
    surfaceVariant   = Color(0xFF1E293B),
    onSurfaceVariant = CRTColors.Gris400,
    outline          = Color(0xFF334155),
)

// ── Tipografía ─────────────────────────────────────────────
// Se usan fuentes del sistema por compatibilidad en release,
// pero la jerarquía está bien definida para personalización futura
val CRTTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize   = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.3).sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 28.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 24.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 22.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 15.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp,
        lineHeight = 19.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp,
    ),
)

// ── Tema principal ─────────────────────────────────────────
@Composable
fun CRTLineasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = CRTTypography,
        content     = content
    )
}
