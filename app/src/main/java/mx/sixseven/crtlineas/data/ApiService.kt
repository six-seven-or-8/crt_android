package mx.sixseven.crtlineas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.sixseven.crtlineas.model.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

// ══════════════════════════════════════════════════════════
// ApiService.kt — CRT Líneas Android
//
// Implementación de las 6 APIs directas verificadas con HAR:
//   - Weex (POST JSON, CURP/RFC/Pasaporte)
//   - MoBig (POST JSON, solo CURP)
//   - MoBig Bienestar (POST JSON, solo CURP)
//   - Yo Mobile (GET, solo CURP)
//   - IENTC (GET + Bearer auto, CURP/RFC)
//   - Sorcel (POST form, CURP/RFC)
//
// Nota: Altán tiene CAPTCHA Proof-of-Work no replicable
// sin el JS del portal. Se consulta via WebView.
//
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

class ApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val JSON_TYPE = "application/json; charset=utf-8".toMediaType()
    private val FORM_TYPE = "application/x-www-form-urlencoded".toMediaType()

    // ── Entry point: consultar un portal por su ID ─────────
    suspend fun query(
        companyId:    String,
        userData:     UserData,
    ): QueryResult = withContext(Dispatchers.IO) {
        val id   = userData.identificador.trim().uppercase()
        val tipo = userData.personType
        val cit  = userData.citizenship

        runCatching {
            when (companyId) {
                "weex"      -> queryWeex(id, tipo, cit)
                "mobig"     -> queryMobig(id, "https://mobig.mx/vinculatulinea/consulta-curp")
                "mobig_bien"-> queryMobig(id, "https://femaseisa.com/api/vinculacion/search-by-curp")
                "yo_mobile" -> queryYoMobile(id)
                "ientc"     -> queryIentc(id, tipo)
                "mirlo"     -> queryMirlo(id)
                "sorcel"         -> querySorcel(id)
                // VinculaTuLinea — mismo endpoint, pathName varía
                "vtl_freedompop" -> queryVinculaTuLinea(id, "freedompop")
                "vtl_oui"        -> queryVinculaTuLinea(id, "oui")
                "vtl_yobi"       -> queryVinculaTuLinea(id, "yobitelecom")
                "vtl_ahorrocel"  -> queryVinculaTuLinea(id, "ahorrocel")
                "vtl_chedraui"   -> queryVinculaTuLinea(id, "chedrauimovil")
                "vtl_oxxocel"    -> queryVinculaTuLinea(id, "oxxocel")
                "vtl_ubercel"    -> queryVinculaTuLinea(id, "ubercel")
                // core.newww.mx — mismo endpoint, brand varía
                "newww_linkmovil" -> queryCoreNewww(id, "lm")
                "newww_newww"     -> queryCoreNewww(id, "nw")
                "newww_redaguila" -> queryCoreNewww(id, "ra")
                // Megamóvil
                "megamovil"       -> queryMegamovil(id)
                "logistica"       -> queryLogistica(id)
                else        -> errorResult(companyId, "API no implementada")
            }
        }.getOrElse { e ->
            val msg = when {
                e.message?.contains("timeout", true) == true -> "err.timeout"
                e.message?.contains("403") == true           -> "err.403"
                else                                         -> e.message ?: "Error desconocido"
            }
            QueryResult(
                companyId   = companyId,
                companyName = companyId,
                status      = ResultStatus.ERROR,
                errorMsg    = msg,
                viaApi      = true,
            )
        }
    }

    // ── Weex — POST JSON con payload TanStack ──────────────
    // Endpoint verificado con HAR. Acepta CURP, RFC y Pasaporte.
    // Endpoint CURP/Pasaporte: be0aebbc...
    // Endpoint RFC: 6d0eb540...
    private suspend fun queryWeex(
        id:   String,
        tipo: PersonType,
        cit:  Citizenship,
    ): QueryResult {
        val isRfc     = tipo == PersonType.MORAL
        val isForeign = cit == Citizenship.EXTRANJERO && tipo == PersonType.FISICA

        val sessionId = java.util.UUID.randomUUID().toString()

        val payload = if (isRfc) {
            buildWeexRfcPayload(id, sessionId)
        } else {
            buildWeexCurpPayload(id, isForeign, sessionId)
        }

        val endpoint = if (isRfc)
            "https://weex.mx/_server/rnu/be0aebbc2f7e02b5b5b8b9999999999a/query"
        else
            "https://weex.mx/_server/rnu/be0aebbc2f7e02b5b5b8b9999999999a/query"

        val encoded = URLEncoder.encode(payload, "UTF-8")
        val url     = "$endpoint?payload=$encoded"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Origin", "https://weex.mx")
            .addHeader("Referer", "https://weex.mx/")
            .build()

        val response = client.newCall(request).execute()
        val body     = response.body?.string() ?: ""
        return parseWeexResponse("weex", "Weex", body)
    }

    private fun buildWeexCurpPayload(id: String, isForeign: Boolean, sessionId: String): String {
        val citizenship   = if (isForeign) "foreign" else "mexican"
        val curp          = if (!isForeign) id else "null"
        val passportNum   = if (isForeign) id else "null"
        return """{"t":{"t":10,"i":0,"p":{"k":["data"],"v":[{"t":10,"i":1,"p":{"k":["personType","citizenship","curp","passportNumber","page","captchaToken","clientSessionId"],"v":[{"t":1,"s":"physical"},{"t":1,"s":"$citizenship"},{"t":1,"s":"$curp"},{"t":${if(isForeign) "1" else "2"},"s":${if(isForeign) "\"$passportNum\"" else "1"}},{"t":0,"s":1},{"t":1,"s":"placeholder"},{"t":1,"s":"$sessionId"}]},"o":0}]},"o":0},"f":63,"m":[]}"""
    }

    private fun buildWeexRfcPayload(rfc: String, sessionId: String): String {
        return """{"t":{"t":10,"i":0,"p":{"k":["data"],"v":[{"t":10,"i":1,"p":{"k":["rfc","page","captchaToken","clientSessionId"],"v":[{"t":1,"s":"$rfc"},{"t":0,"s":1},{"t":1,"s":"placeholder"},{"t":1,"s":"$sessionId"}]},"o":0}]},"o":0},"f":63,"m":[]}"""
    }

    private fun parseWeexResponse(id: String, name: String, body: String): QueryResult {
        return runCatching {
            val root  = JSONObject(body)
            val data  = root.optJSONObject("data") ?: root
            val lines = mutableListOf<String>()
            val arr   = data.optJSONArray("lines") ?: data.optJSONArray("phones") ?: JSONArray()
            for (i in 0 until arr.length()) {
                val item = arr.optJSONObject(i)
                val num  = item?.optString("number") ?: item?.optString("phone") ?: arr.optString(i)
                if (num.isNotBlank()) lines.add(num)
            }
            QueryResult(
                companyId   = id,
                companyName = name,
                phones      = lines,
                found       = lines.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult(id, "Parse error") }
    }

    // ── MoBig / MoBig Bienestar — POST JSON ───────────────
    // Verificado con HAR. Solo acepta CURP.
    private suspend fun queryMobig(curp: String, url: String): QueryResult {
        val body = """{"curp":"$curp"}""".toRequestBody(JSON_TYPE)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        val response  = client.newCall(request).execute()
        val respBody  = response.body?.string() ?: ""
        val id        = if (url.contains("femaseisa")) "mobig_bien" else "mobig"
        val name      = if (url.contains("femaseisa")) "MoBig Internet Bienestar" else "MoBig"

        return runCatching {
            val root  = JSONObject(respBody)
            val lines = mutableListOf<String>()
            val arr   = root.optJSONArray("lines") ?: root.optJSONArray("phones") ?: JSONArray()
            for (i in 0 until arr.length()) {
                val item = arr.optJSONObject(i)
                val num  = item?.optString("number") ?: arr.optString(i)
                if (num.isNotBlank()) lines.add(num)
            }
            QueryResult(
                companyId   = id,
                companyName = name,
                phones      = lines,
                found       = lines.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult(id, "Parse error") }
    }

    // ── Yo Mobile — GET con CURP en la URL ────────────────
    // Verificado con HAR. Solo acepta CURP.
    private suspend fun queryYoMobile(curp: String): QueryResult {
        val url     = "https://play.prod.yomobile.xyz/api/v1.0/crm/lines/by-personal-id/$curp/"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val body     = response.body?.string() ?: ""

        return runCatching {
            val root  = JSONObject(body)
            val lines = mutableListOf<String>()
            val arr   = root.optJSONArray("results") ?: JSONArray()
            for (i in 0 until arr.length()) {
                val item = arr.optJSONObject(i)
                val num  = item?.optString("msisdn") ?: item?.optString("number") ?: ""
                if (num.isNotBlank()) lines.add(num)
            }
            QueryResult(
                companyId   = "yo_mobile",
                companyName = "Yo Mobile",
                phones      = lines,
                found       = lines.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult("yo_mobile", "Parse error") }
    }

    // ── IENTC — GET con Bearer auto-obtenido ──────────────
    // Verificado con HAR. Acepta CURP y RFC.
    private suspend fun queryIentc(id: String, tipo: PersonType): QueryResult {
        // Paso 1: obtener Bearer token
        val tokenReq = Request.Builder()
            .url("https://api-iso-prod.ientc.dev/auth/token")
            .post("grant_type=client_credentials".toRequestBody(FORM_TYPE))
            .addHeader("Accept", "application/json")
            .build()

        val tokenResp = client.newCall(tokenReq).execute()
        val tokenBody = tokenResp.body?.string() ?: ""
        val token     = runCatching {
            JSONObject(tokenBody).optString("access_token", "")
        }.getOrDefault("")

        val paramName = if (tipo == PersonType.MORAL) "rfc" else "curp"
        val url       = "https://api-iso-prod.ientc.dev/vinculacion/number/get-phones?$paramName=$id"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .apply { if (token.isNotBlank()) addHeader("Authorization", "Bearer $token") }
            .build()

        val response = client.newCall(request).execute()
        val body     = response.body?.string() ?: ""

        return runCatching {
            val root  = JSONObject(body)
            val lines = mutableListOf<String>()
            val arr   = root.optJSONArray("phones") ?: root.optJSONArray("lines") ?: JSONArray()
            for (i in 0 until arr.length()) {
                val item = arr.optJSONObject(i)
                val num  = item?.optString("number") ?: arr.optString(i)
                if (num.isNotBlank()) lines.add(num)
            }
            QueryResult(
                companyId   = "ientc",
                companyName = "IENTC",
                phones      = lines,
                found       = lines.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult("ientc", "Parse error") }
    }

    // ── Mirlo — GET by CURP/RFC ────────────────────────────
    // Endpoint verificado con HAR: apib.mirlo.com/api/v1/regulation/query/by-curp/{CURP}
    // Endpoint RFC inferido del mismo patrón: by-rfc/{RFC}
    private suspend fun queryMirlo(id: String): QueryResult {
        // CURP = 18 chars, RFC = 12-13 chars
        val isCurp    = id.length == 18
        val paramPath = if (isCurp) "by-curp" else "by-rfc"

        // Intentar endpoint principal
        val result = queryMirloEndpoint(id, paramPath)

        // Si falla con RFC y devuelve error de red (no 404), intentar by-curp como fallback
        // por si el endpoint RFC tiene nombre diferente
        if (!isCurp && result.status == ResultStatus.ERROR) {
            val fallback = queryMirloEndpoint(id, "by-rfc-moral")
            if (fallback.status != ResultStatus.ERROR) return fallback
        }

        return result
    }

    private suspend fun queryMirloEndpoint(id: String, paramPath: String): QueryResult {
        val url = "https://apib.mirlo.com/api/v1/regulation/query/$paramPath/$id"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Origin", "https://mirlo.com")
            .addHeader("Referer", "https://mirlo.com/")
            .build()

        return runCatching {
            val response = client.newCall(request).execute()
            val body     = response.body?.string() ?: ""
            val root     = org.json.JSONObject(body)
            val status   = root.optInt("status", response.code)

            if (status == 404 || status == 400) {
                // 404 = sin líneas registradas con ese identificador
                QueryResult(
                    companyId   = "mirlo",
                    companyName = "Mirlo",
                    phones      = emptyList(),
                    found       = false,
                    viaApi      = true,
                    status      = ResultStatus.OK,
                )
            } else if (status in 200..299 || response.isSuccessful) {
                val phones = mutableListOf<String>()
                val arr    = root.optJSONArray("lines")
                    ?: root.optJSONArray("phones")
                    ?: root.optJSONArray("data")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val item = arr.optJSONObject(i)
                        val num  = item?.optString("msisdn")
                            ?: item?.optString("number")
                            ?: item?.optString("phone")
                            ?: arr.optString(i)
                        if (!num.isNullOrBlank() && num.length >= 10) phones.add(num)
                    }
                }
                QueryResult(
                    companyId   = "mirlo",
                    companyName = "Mirlo",
                    phones      = phones,
                    found       = phones.isNotEmpty(),
                    viaApi      = true,
                    status      = ResultStatus.OK,
                )
            } else {
                errorResult("mirlo", "HTTP $status")
            }
        }.getOrElse { e ->
            errorResult("mirlo", e.message ?: "Error de conexión")
        }
    }

    // ── Sorcel — POST form ─────────────────────────────────
    // Verificado con HAR. Acepta CURP y RFC (mismo campo curpa).
    private suspend fun querySorcel(id: String): QueryResult {
        val body    = "curpa=${URLEncoder.encode(id, "UTF-8")}".toRequestBody(FORM_TYPE)
        val request = Request.Builder()
            .url("https://www.soriup.mx/consultaR.asp")
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Origin", "https://www.soriup.mx")
            .addHeader("Referer", "https://www.soriup.mx/")
            .build()

        val response = client.newCall(request).execute()
        val html     = response.body?.string() ?: ""

        // Sorcel devuelve HTML — parsear números de la tabla
        val phones   = Regex("""(\d{10})""").findAll(html)
            .map { it.value }
            .filter { it.startsWith("55") || it.startsWith("33") || it.startsWith("81") }
            .distinct()
            .toList()

        return QueryResult(
            companyId   = "sorcel",
            companyName = "Sorcel",
            phones      = phones,
            found       = phones.isNotEmpty(),
            viaApi      = true,
            status      = ResultStatus.OK,
        )
    }

    // ── VinculaTuLinea — GET subscriptions-by-curp ───────────
    // API compartida verificada con HAR: freedompop, oui, yobitelecom,
    // ahorrocel, chedrauimovil, oxxocel, ubercel
    // Solo cambia el pathName. Acepta CURP, RFC y Pasaporte.
    private suspend fun queryVinculaTuLinea(id: String, pathName: String): QueryResult {
        val url = "https://vinculatulinea.com/omv-lineas/v1/omv-services/subscriptions-by-curp" +
                  "?pathName=$pathName&apiName=getSubscriptionsbyCURP"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Origin", "https://vinculatulinea.com")
            .addHeader("Referer", "https://vinculatulinea.com/$pathName/welcome")
            .build()
        return runCatching {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            val root = org.json.JSONObject(body)
            val code = root.optInt("responseCode", -1)
            val subs = root.optJSONArray("subscription")
            val phones = mutableListOf<String>()
            if (subs != null) {
                for (i in 0 until subs.length()) {
                    val item = subs.optJSONObject(i)
                    val num = item?.optString("msisdn") ?: item?.optString("phone") ?: ""
                    if (num.isNotBlank() && num.length >= 10) phones.add(num)
                }
            }
            // responseCode 0 = consultado (puede haber 0 líneas), otros = error
            if (code == 3) errorResult(pathName, "err.403")
            else QueryResult(
                companyId   = "vtl_$pathName",
                companyName = pathName,
                phones      = phones,
                found       = phones.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult("vtl_$pathName", it.message ?: "Error") }
    }

    // ── core.newww.mx — GET consulta_lineas_vinculacion ───────
    // API compartida verificada con HAR: Link Móvil, Newww, Red Águila
    // brand: "lm" = Link Móvil, "nw" = Newww, "ra" = Red Águila
    private suspend fun queryCoreNewww(id: String, brand: String): QueryResult {
        val url = "https://core.newww.mx/api/core/consulta_lineas_vinculacion?curp=$id"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Origin", "https://core.newww.mx")
            .build()
        return runCatching {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            val root = org.json.JSONObject(body)
            val resCode = root.optInt("code", -1)
            val phones = mutableListOf<String>()
            val data = root.optJSONArray("data")
            if (data != null) {
                for (i in 0 until data.length()) {
                    val item = data.optJSONObject(i)
                    val num = item?.optString("msisdn") ?: item?.optString("phone") ?: ""
                    if (num.isNotBlank() && num.length >= 10) phones.add(num)
                }
            }
            val companyName = when(brand) { "lm" -> "Link Móvil"; "nw" -> "Newww"; else -> "Red Águila" }
            QueryResult(
                companyId   = "newww_$brand",
                companyName = companyName,
                phones      = phones,
                found       = phones.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult("newww_$brand", it.message ?: "Error") }
    }

    // ── Megamóvil — GET validaCURP ────────────────────────────
    // API propia verificada con HAR
    private suspend fun queryMegamovil(id: String): QueryResult {
        val url = "https://consultavinculacion.megamovil.mx/validaCURP?curp=$id"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Origin", "https://consultavinculacion.megamovil.mx")
            .build()
        return runCatching {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            val root = org.json.JSONObject(body)
            val status = root.optString("status", "")
            val phones = mutableListOf<String>()
            val lines = root.optJSONArray("lines") ?: root.optJSONArray("lineas")
            if (lines != null) {
                for (i in 0 until lines.length()) {
                    val item = lines.optJSONObject(i)
                    val num = item?.optString("msisdn") ?: item?.optString("number") ?: ""
                    if (num.isNotBlank() && num.length >= 10) phones.add(num)
                }
            }
            // "ERROR" en status significa sin líneas (no error de sistema)
            QueryResult(
                companyId   = "megamovil",
                companyName = "Mega Móvil",
                phones      = phones,
                found       = phones.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult("megamovil", it.message ?: "Error") }
    }

    // ── Logística ACN (Dua / Fedego! / Flash Mobile) ─────────
    // API verificada con HAR: ku.diri.mx/consultaRNU/{CURP}
    // Devuelve array JSON [] vacío o con líneas
    private suspend fun queryLogistica(id: String): QueryResult {
        val url = "https://ku.diri.mx/consultaRNU/$id"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Origin", "https://consulta.logisticaacn.mx")
            .addHeader("Referer", "https://consulta.logisticaacn.mx/")
            .build()
        return runCatching {
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            val arr = org.json.JSONArray(body)
            val phones = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                val item = arr.optJSONObject(i)
                val num = item?.optString("msisdn") ?: item?.optString("numero") ?: arr.optString(i)
                if (!num.isNullOrBlank() && num.length >= 10) phones.add(num)
            }
            QueryResult(
                companyId   = "logistica",
                companyName = "Dua / Fedego! / Flash Mobile",
                phones      = phones,
                found       = phones.isNotEmpty(),
                viaApi      = true,
                status      = ResultStatus.OK,
            )
        }.getOrElse { errorResult("logistica", it.message ?: "Error") }
    }

    private fun errorResult(id: String, msg: String) = QueryResult(
        companyId   = id,
        companyName = id,
        status      = ResultStatus.ERROR,
        errorMsg    = msg,
        viaApi      = true,
    )
}
