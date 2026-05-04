package mx.sixseven.crtlineas.model


// ── Tipos de persona ───────────────────────────────────────
enum class PersonType { FISICA, MORAL }

enum class Citizenship { MEXICANO, EXTRANJERO }

// ── Identificador según tipo de persona ───────────────────
enum class IdType(val label: String, val placeholder: String, val maxLength: Int) {
    CURP("CURP", "GOML920314HDFLPS08", 18),
    RFC("RFC", "ZVM890515KG3", 13),
    PASAPORTE("Pasaporte", "ZAB000022133", 20),
}

fun getIdType(personType: PersonType, citizenship: Citizenship): IdType = when {
    personType == PersonType.MORAL        -> IdType.RFC
    citizenship == Citizenship.EXTRANJERO -> IdType.PASAPORTE
    else                                  -> IdType.CURP
}

// ── Datos del usuario (se cifran en storage) ──────────────
data class UserData(
    val identificador:  String,
    val personType:     PersonType,
    val citizenship:    Citizenship,
    val acceptedTerms:  Boolean,
    val acceptedPrivacy: Boolean,
    val timestamp:      Long = System.currentTimeMillis(),
)

// ── Modo de consulta de cada portal ───────────────────────
enum class QueryMode { API_DIRECT, WEBVIEW }

// ── Tipos de personas que acepta un portal ─────────────────
enum class PortalPersonas {
    FISICA_MX,
    FISICA_MX_MORAL,
    TODOS,
}

// ── Definición de un portal del CRT ───────────────────────
data class Company(
    val id:        String,
    val name:      String,
    val url:       String = "",
    val queryMode: QueryMode,
    val personas:  PortalPersonas,
    val detail:    String = "",
)

// ── Estado de resultado de una consulta ───────────────────
enum class ResultStatus { OK, ERROR, PENDING }

// ── Resultado de consultar un portal ──────────────────────
data class QueryResult(
    val companyId:   String,
    val companyName: String,
    val phones:      List<String> = emptyList(),
    val found:       Boolean = false,
    val url:         String = "",
    val status:      ResultStatus = ResultStatus.OK,
    val errorMsg:    String = "",
    val viaApi:      Boolean = false,
    val timestamp:   Long = System.currentTimeMillis(),
)

// ── Portal con error conocido ──────────────────────────────
data class ErrorCompany(
    val id:          String,
    val name:        String,
    val url:         String,
    val errorMsgKey: String,
    val knownSince:  String,
    val personas:    PortalPersonas = PortalPersonas.FISICA_MX,
)

// ── Portal manual ──────────────────────────────────────────
data class ManualCompany(
    val id:      String,
    val name:    String,
    val url:     String,
    val noteKey: String,
    val credKey: String,
)

// ── Estado global de la sesión de consulta ────────────────
data class SessionState(
    val isActive:     Boolean = false,
    val currentIndex: Int = 0,
    val totalPortals: Int = 0,
    val results:      Map<String, QueryResult> = emptyMap(),
    val apiDone:      Boolean = false,
)
