package mx.sixseven.crtlineas.ui.form

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mx.sixseven.crtlineas.model.*
import mx.sixseven.crtlineas.ui.theme.CRTColors

// ══════════════════════════════════════════════════════════
// FormScreen.kt — CRT Líneas Android
//
// Formulario de consulta:
// - Selector tipo de persona (Física / Moral)
// - Selector ciudadanía (Mexicano / Extranjero) — solo física
// - Campo de identificador (CURP / RFC / Pasaporte)
// - Checkboxes TC y Aviso de Privacidad
// - Validación en tiempo real
// - Botón habilitado solo cuando todo es válido
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

@Composable
fun FormScreen(
    savedUserData:       UserData?,
    hasExistingResults:  Boolean = false,
    onStart:             (UserData) -> Unit,
) {
    // ── Estado del formulario ──────────────────────────────
    var personType  by remember { mutableStateOf(savedUserData?.personType  ?: PersonType.FISICA) }
    var citizenship by remember { mutableStateOf(savedUserData?.citizenship ?: Citizenship.MEXICANO) }
    var idValue     by remember { mutableStateOf(savedUserData?.identificador ?: "") }
    var acceptedTc  by remember { mutableStateOf(savedUserData?.acceptedTerms   ?: false) }
    var acceptedAv  by remember { mutableStateOf(savedUserData?.acceptedPrivacy ?: false) }

    val idType = getIdType(personType, citizenship)

    // Validación
    val idValid = when (idType) {
        IdType.CURP      -> idValue.length == 18
        IdType.RFC       -> idValue.length in 12..13
        IdType.PASAPORTE -> idValue.length >= 6
    }
    val canStart = idValid && acceptedTc && acceptedAv
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingUserData   by remember { mutableStateOf<UserData?>(null) }

    // Diálogo de confirmación si hay resultados previos
    if (showConfirmDialog && pendingUserData != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("¿Nueva consulta?") },
            text  = {
                Text(
                    "Ya tienes resultados de una consulta anterior. " +
                    "Al iniciar una nueva consulta se borrarán esos resultados. ¿Continuar?",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    pendingUserData?.let { onStart(it) }
                }) {
                    Text("Sí, nueva consulta", color = CRTColors.Naranja600)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        // ── Header ─────────────────────────────────────────
        FormHeader()

        // ── Hint ───────────────────────────────────────────
        HintCard()

        // ── Tipo de persona ────────────────────────────────
        SectionLabel("Tipo de persona")
        SegmentedSelector(
            options  = listOf("Persona física", "Persona moral"),
            selected = if (personType == PersonType.FISICA) 0 else 1,
            onSelect = { idx ->
                personType = if (idx == 0) PersonType.FISICA else PersonType.MORAL
                idValue = ""
            },
        )

        // ── Ciudadanía (solo para persona física) ──────────
        AnimatedVisibility(visible = personType == PersonType.FISICA) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("Ciudadanía")
                SegmentedSelector(
                    options  = listOf("Mexicano", "Extranjero"),
                    selected = if (citizenship == Citizenship.MEXICANO) 0 else 1,
                    onSelect = { idx ->
                        citizenship = if (idx == 0) Citizenship.MEXICANO else Citizenship.EXTRANJERO
                        idValue = ""
                    },
                )
            }
        }

        // ── Campo de identificador ─────────────────────────
        IdField(
            idType   = idType,
            value    = idValue,
            isValid  = idValue.isEmpty() || idValid,
            onChange = { new ->
                if (new.length <= idType.maxLength) {
                    idValue = new.uppercase()
                }
            },
        )

        // ── Checkboxes ─────────────────────────────────────
        CheckRow(
            checked = acceptedTc,
            label   = "Acepto los Términos y Condiciones de cada portal consultado",
            onCheck = { acceptedTc = it },
        )
        CheckRow(
            checked = acceptedAv,
            label   = "Acepto el Aviso de Privacidad de cada portal consultado",
            onCheck = { acceptedAv = it },
        )

        // ── Nota de privacidad ─────────────────────────────
        PrivacyNote()

        // ── Botón de inicio ────────────────────────────────
        Button(
            onClick = {
                if (canStart) {
                    val ud = UserData(
                        identificador   = idValue,
                        personType      = personType,
                        citizenship     = citizenship,
                        acceptedTerms   = acceptedTc,
                        acceptedPrivacy = acceptedAv,
                    )
                    if (hasExistingResults) {
                        pendingUserData   = ud
                        showConfirmDialog = true
                    } else {
                        onStart(ud)
                    }
                }
            },
            enabled  = canStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = CRTColors.Naranja600,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
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

        Spacer(Modifier.height(24.dp))
    }
}

// ── Subcomponentes ─────────────────────────────────────────

@Composable
private fun FormHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            shape  = RoundedCornerShape(10.dp),
            color  = CRTColors.Naranja600,
        ) {
            Text(
                text     = "LS",
                style    = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color      = CRTColors.Blanco,
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
        Column {
            Text(
                text  = "LineShield",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Text(
                text  = "Protege tu identidad telefónica",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun HintCard() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text  = "Ingresa tus datos una sola vez. La app abrirá solo los portales compatibles con tu tipo de persona y pegará los datos automáticamente. Todo se guarda cifrado en tu dispositivo durante 24 horas.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text  = text,
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Composable
private fun SegmentedSelector(
    options:  List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { idx, label ->
            val isSelected = idx == selected
            Surface(
                modifier  = Modifier.weight(1f),
                shape     = RoundedCornerShape(10.dp),
                color     = if (isSelected) CRTColors.Azul800 else CRTColors.Azul800.copy(alpha = 0f),
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                onClick   = { onSelect(idx) },
            ) {
                Text(
                    text      = label,
                    style     = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isSelected) CRTColors.Blanco
                                     else MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun IdField(
    idType:  IdType,
    value:   String,
    isValid: Boolean,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        label         = { Text(idType.label) },
        placeholder   = { Text(idType.placeholder) },
        isError       = !isValid && value.isNotEmpty(),
        supportingText = {
            if (!isValid && value.isNotEmpty()) {
                Text("Formato inválido para ${idType.label}")
            }
        },
        singleLine    = true,
        modifier      = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            keyboardType   = KeyboardType.Ascii,
            imeAction      = ImeAction.Done,
        ),
        shape  = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = CRTColors.Azul800,
            focusedLabelColor    = CRTColors.Azul800,
            errorBorderColor     = MaterialTheme.colorScheme.error,
        ),
    )
}

@Composable
private fun CheckRow(
    checked: Boolean,
    label:   String,
    onCheck: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheck(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Checkbox(
            checked         = checked,
            onCheckedChange = onCheck,
            colors          = CheckboxDefaults.colors(
                checkedColor = CRTColors.Azul800,
            ),
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun PrivacyNote() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("🔒", style = MaterialTheme.typography.bodySmall)
        Text(
            text  = "Tus datos nunca salen de este dispositivo. Se eliminan automáticamente a las 24 horas.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}
