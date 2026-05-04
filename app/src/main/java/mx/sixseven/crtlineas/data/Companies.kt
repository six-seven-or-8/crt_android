package mx.sixseven.crtlineas.data

import mx.sixseven.crtlineas.model.*

// ══════════════════════════════════════════════════════════
// Companies.kt — CRT Líneas Android
// Misma lógica que companies.js de la extensión
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

object Companies {

    // ── Portales con API directa ───────────────────────────
    val apiCompanies = listOf(

        Company(
            id        = "mobig",
            name      = "MoBig",
            queryMode = QueryMode.API_DIRECT,
            personas  = PortalPersonas.FISICA_MX,
        ),
        Company(
            id        = "mobig_bien",
            name      = "MoBig Internet Bienestar",
            queryMode = QueryMode.API_DIRECT,
            personas  = PortalPersonas.FISICA_MX,
        ),
        Company(
            id        = "yo_mobile",
            name      = "Yo Mobile",
            queryMode = QueryMode.API_DIRECT,
            personas  = PortalPersonas.FISICA_MX,
        ),
        Company(
            id        = "ientc",
            name      = "IENTC",
            queryMode = QueryMode.API_DIRECT,
            personas  = PortalPersonas.FISICA_MX_MORAL,
        ),
        Company(
            id        = "mirlo",
            name      = "Mirlo",
            queryMode = QueryMode.API_DIRECT,
            personas  = PortalPersonas.FISICA_MX_MORAL,
        ),
        Company(
            id        = "sorcel",
            name      = "Sorcel",
            queryMode = QueryMode.API_DIRECT,
            personas  = PortalPersonas.FISICA_MX_MORAL,
        ),
    )

    // ── Portales con WebView ───────────────────────────────
    val webviewCompanies = listOf(
        Company(
            id        = "weex",
            name      = "Weex",
            url       = "https://weex.mx/consultalineas.html",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.TODOS,
        ),
        Company(
            id        = "altan",
            name      = "Altán Redes (~67 OMVs)",
            url       = "https://rnu.altanredes.com/consulta",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.TODOS,
            detail    = "2y2x, Abafon, Abix, Addinteli, AI Telecomm, Appcel, BienCel, Bigcel, Bromovil, CFE Telecom, Chip Macropay, CoolMobile, Comunicaciones Green, Conect2, Diri Movil, ENI Networks, Fangio Mobile, Fibracell, FRC Mobile, Gamers, Gane, Glovo Telecom, Gmovil, Grupo Inten, Hashtag, I AM Abundance, Interlinked, Inxel, Iusatel, Kolors Mobile, Maifon, Mexico Movil, Mexfon, Mi Movil/Altan, MobileArionet, Movired, Movil para Todos, Nabi, Netmas, On-Link, OUI/Altan, Othisi Mobile, PilloFon, Playcell, Red Blak, Red Dog, Redicoppel, Retemex, RETESEC, Rincel, Secure Witness, Sfon, Spot 1, Starline, Telefonica Luna, Telgen, Telmovil, Teracel, TIC-OMV, Tuis, TurboCel, Turbored, Ultracel, Vasanta, VivaMX, Wiki Katat, Wimotelecom, Wiicel, ALLCE",
        ),
        Company(
            id        = "logistica",
            name      = "Dua / Fedego! / Flash Mobile",
            url       = "https://consulta.logisticaacn.mx/",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.FISICA_MX,
        ),
        Company(
            id        = "dalefon",
            name      = "Dalefon",
            url       = "https://www.dalefon.mx/vinculatulinea/",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.TODOS,
        ),
        Company(
            id        = "dalefon_bien",
            name      = "Dalefon Internet Bienestar",
            url       = "https://www.internetbienestarmex.com/vinculatulinea/",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.TODOS,
        ),
        Company(
            id        = "redphone",
            name      = "Redphone",
            url       = "https://vinculacion.redphone.com.mx/consulta",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.FISICA_MX_MORAL,
        ),
        Company(
            id        = "virgin",
            name      = "Virgin Mobile",
            url       = "https://virginmobile.mx/v1/consultatulinea",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.FISICA_MX,
        ),
        Company(
            id        = "mi_movil",
            name      = "Mi Movil",
            url       = "https://vinculacion.mimovil.com.mx/consulta",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.FISICA_MX_MORAL,
        ),

        Company(
            id        = "mosi",
            name      = "Mosi",
            url       = "https://vinculacion.mosi.mx/consulta",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.FISICA_MX_MORAL,
        ),
        Company(
            id        = "oxio",
            name      = "Oxio",
            url       = "https://verificar.oxiomobile.com/consultatuslineas",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.FISICA_MX,
        ),
        Company(
            id        = "bait",
            name      = "Bait",
            url       = "https://btz.mx/consultaregistro",
            queryMode = QueryMode.WEBVIEW,
            personas  = PortalPersonas.FISICA_MX,
        ),
    )

    val allActive = apiCompanies + webviewCompanies

    // ── Portales manuales ──────────────────────────────────
    val manualCompanies = listOf(
        ManualCompany("att",     "AT&T / Unefon / WIM",  "https://att.com.mx/controlpersonal/",                          "man.note.webcomp",     "man.cred.curp_slider"),
        ManualCompany("abib",    "Abib",                  "https://abib.com.mx/#/consultatuslineas",                      "man.note.phone_abib",  "man.cred.phone_abib"),
        ManualCompany("abib_b",  "Abib Internet Bienestar","https://www.abibinternetdelbienestar.mx/consultatulinea",     "man.note.phone_abib_bien","man.cred.phone_abib_bien"),
        ManualCompany("bestel",  "Bestel / Cablecom",     "https://facturacion.bestel.com.mx/",                           "man.note.user_pass",   "man.cred.user_pass_bestel"),
        ManualCompany("dialo",   "Dialo",                 "https://dialo.mx/vinculatulinea/consulta.html",                "man.note.email_curp",  "man.cred.curp_email"),
        ManualCompany("izzi",    "Izzi",                  "https://www.izzi.mx/login",                                    "man.note.user_pass",   "man.cred.user_pass_izzi"),
        ManualCompany("movistar","Telefónica Movistar",   "https://www.movistar.com.mx/consulta-tu-linea",                "man.note.id_oficial",  "man.cred.id_oficial"),
        ManualCompany("rp_koon", "Redphone Koonol",       "https://redphone.vinculacion.koonolmexico.com/session/new",    "man.note.user_pass",   "man.cred.user_pass_redphone"),
        ManualCompany("sky",     "Sky",                   "https://micuenta.sky.com.mx/login",                            "man.note.user_pass",   "man.cred.user_pass_sky"),
        ManualCompany("telcel",  "Telcel",                "https://registro.telcel.com/vinculatulinea",                   "man.note.biometrico",  "man.cred.biometrico"),
        ManualCompany("tokamov", "Tokamovil",             "https://tokamovil.mx/cumplimiento/consulta-vinculacion/",      "man.note.email_vinc",  "man.cred.email_vinc"),
        ManualCompany("yumovil", "Yu Movil",              "https://www.yumovil.com.mx/login",                             "man.note.user_pass",   "man.cred.user_pass_yumovil"),
    )

    // ── Portales con error conocido ────────────────────────
    val errorCompanies = listOf(
        ErrorCompany("beneleit",    "Beneleit Movil",                  "https://beneleit.mx/consultalineas/",                        "err.proximamente", "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("link_movil",  "Link Movil",                      "https://movil.linkteconectamos.com/consultar-vinculacion/",   "err.timeout",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("mega_movil",  "Mega Movil",                      "https://consultavinculacion.megamovil.mx/",                   "err.403",          "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("newww",       "Newww",                           "https://consultavinculacion.newww.mx/",                       "err.timeout",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("nextor",      "Nextor Movil",                    "https://vinculacion.nextormovil.mx/",                         "err.timeout",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("red_aguila",  "Red Águila",                      "https://consultavinculacion.redaguila.com.mx/",               "err.timeout",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("viralcel",    "Viral Cel",                       "https://www.viralcel.com/mi-linea",                           "err.403",          "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("wiicel",      "Wiicel (portal propio)",          "https://wiicel.com/",                                         "err.522",          "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("ahorrocel",   "AhorroCel (VinculaTuLinea)",      "https://vinculatulinea.com/Ahorrocel",                        "err.403_vtl",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("chedraui",    "Chedraui Movil (VinculaTuLinea)", "https://vinculatulinea.com/Chedrauimovil",                    "err.403_vtl",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("freedompop",  "Freedompop (VinculaTuLinea)",     "https://vinculatulinea.com/Freedompop",                       "err.403_vtl",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("oxxocel",     "OXXO CEL (VinculaTuLinea)",       "https://vinculatulinea.com/Oxxocel",                          "err.403_vtl",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("oui",         "OUI (VinculaTuLinea)",            "https://vinculatulinea.com/oui/welcome",                      "err.403_vtl",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("ubercel",     "Uber Cel (VinculaTuLinea)",       "https://vinculatulinea.com/Ubercel",                          "err.403_vtl",      "28/04/2026", PortalPersonas.FISICA_MX),
        ErrorCompany("yobi",        "Yobi Telecom (VinculaTuLinea)",   "https://vinculatulinea.com/YobiTelecom",                      "err.403_vtl",      "28/04/2026", PortalPersonas.FISICA_MX),
    )

    // ── Filtrar portales según tipo de persona ─────────────
    fun filterForUser(
        personType:  PersonType,
        citizenship: Citizenship,
    ): List<Company> {
        val seen = mutableSetOf<String>()
        return allActive.filter { co ->
            val ok = when (co.personas) {
                PortalPersonas.TODOS         -> true
                PortalPersonas.FISICA_MX     -> personType == PersonType.FISICA && citizenship == Citizenship.MEXICANO
                PortalPersonas.FISICA_MX_MORAL -> (personType == PersonType.FISICA && citizenship == Citizenship.MEXICANO) || personType == PersonType.MORAL
            }
            if (!ok) return@filter false
            val key = if (co.url.isNotEmpty()) co.url else co.id
            if (seen.contains(key)) return@filter false
            seen.add(key)
            true
        }
    }

    fun filterApiForUser(p: PersonType, c: Citizenship) =
        filterForUser(p, c).filter { it.queryMode == QueryMode.API_DIRECT }

    fun filterWebviewForUser(p: PersonType, c: Citizenship) =
        filterForUser(p, c).filter { it.queryMode == QueryMode.WEBVIEW }
}
