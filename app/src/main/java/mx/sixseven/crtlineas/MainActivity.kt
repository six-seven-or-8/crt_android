package mx.sixseven.crtlineas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.sixseven.crtlineas.data.SecureStorage
import mx.sixseven.crtlineas.model.UserData
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.sixseven.crtlineas.ui.about.AboutScreen
import mx.sixseven.crtlineas.ui.form.FormScreen
import mx.sixseven.crtlineas.ui.manual.ManualScreen
import mx.sixseven.crtlineas.ui.progress.ProgressScreen
import mx.sixseven.crtlineas.ui.results.ResultsScreen
import mx.sixseven.crtlineas.ui.splash.SplashScreen
import mx.sixseven.crtlineas.ui.theme.CRTColors
import mx.sixseven.crtlineas.ui.theme.CRTLineasTheme

// ══════════════════════════════════════════════════════════
// MainActivity.kt — CRT Líneas Android
//
// Navegación: Splash → Formulario → Resultados / Manuales
// La app usa navegación interna simple con estado en memoria.
// No se usa Navigation Compose para mantener simple el estado
// durante la consulta en progreso.
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

enum class Screen { SPLASH, FORM, PROGRESS, RESULTS, MANUAL, ABOUT }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash Screen nativa de Android 12+
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val storage = SecureStorage(this)
        storage.expireStale()

        setContent {
            CRTLineasTheme {
                CRTLineasApp(storage = storage)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CRTLineasApp(storage: SecureStorage) {
    val vm: MainViewModel = viewModel()
    val uiState by vm.state.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    val showTabs = currentScreen != Screen.SPLASH

    Scaffold(
        bottomBar = {
            if (showTabs) {
                CRTBottomBar(
                    current  = currentScreen,
                    onSelect = { currentScreen = it },
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                Screen.SPLASH -> SplashScreen(
                    onEnter = { currentScreen = Screen.FORM }
                )
                Screen.FORM -> FormScreen(
                    savedUserData    = vm.loadUserData(),
                    hasExistingResults = uiState.results.isNotEmpty(),
                    onStart          = { userData ->
                        vm.startQuery(userData)
                        currentScreen = Screen.PROGRESS
                    },
                )
                Screen.PROGRESS -> ProgressScreen(
                    uiState       = uiState,
                    userData      = vm.loadUserData() ?: return@Box,
                    onWebviewDone = { id, phones -> vm.webviewDone(id, phones) },
                    onWebviewError = { id, msg  -> vm.webviewError(id, msg) },
                )
                Screen.RESULTS -> ResultsScreen(
                    results    = uiState.results,
                    isDone     = uiState.isDone,
                    onClearAll = {
                        vm.clearAll()
                        currentScreen = Screen.FORM
                    },
                )
                Screen.MANUAL -> ManualScreen()
                Screen.ABOUT  -> AboutScreen()
            }
        }
    }
}

@Composable
private fun CRTBottomBar(current: Screen, onSelect: (Screen) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        val items = listOf(
            Triple(Screen.FORM,     Icons.Default.Home,        "Inicio"),
            Triple(Screen.PROGRESS, Icons.Default.Sync,        "Consulta"),
            Triple(Screen.RESULTS, Icons.Default.List,        "Resultados"),
            Triple(Screen.MANUAL,  Icons.Default.OpenInNew,   "Manuales"),
            Triple(Screen.ABOUT,   Icons.Default.Info,        "Acerca"),
        )
        items.forEach { (screen, icon, label) ->
            NavigationBarItem(
                selected = current == screen,
                onClick  = { onSelect(screen) },
                icon     = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                    )
                },
                label = {
                    Text(
                        text  = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = CRTColors.Azul800,
                    selectedTextColor   = CRTColors.Azul800,
                    indicatorColor      = CRTColors.Azul100,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

