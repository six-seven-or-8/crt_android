package mx.sixseven.crtlineas.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import mx.sixseven.crtlineas.model.QueryResult
import mx.sixseven.crtlineas.model.UserData
import org.json.JSONObject

// ══════════════════════════════════════════════════════════
// SecureStorage.kt — CRT Líneas Android
// Cifrado: AES-256-GCM via Android Keystore
// Serialización: Gson + JSONObject (sin TypeToken genérico)
// TTL: 24 horas automático
// ══════════════════════════════════════════════════════════

class SecureStorage(context: Context) {

    companion object {
        private const val PREFS_FILE   = "crt67_secure"
        private const val TTL_MS       = 86_400_000L

        private const val KEY_USER     = "crt67_user"
        private const val KEY_USER_TS  = "crt67_user_ts"
        private const val KEY_RESULTS  = "crt67_results"
        private const val KEY_SESS_TS  = "crt67_sess_ts"
        private const val KEY_SESS_IDX = "crt67_sess_idx"
        private const val KEY_SESS_ON  = "crt67_sess_on"
        private const val KEY_LANG     = "crt67_lang"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context, PREFS_FILE, masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val gson = Gson()

    private fun isExpired(ts: Long) = ts == 0L || System.currentTimeMillis() - ts > TTL_MS

    fun ttlRemainingMs(): Long {
        val ts = prefs.getLong(KEY_USER_TS, 0L)
        return if (isExpired(ts)) 0L else TTL_MS - (System.currentTimeMillis() - ts)
    }

    // ── Datos del usuario ──────────────────────────────────
    fun saveUserData(data: UserData) {
        prefs.edit()
            .putString(KEY_USER, gson.toJson(data))
            .putLong(KEY_USER_TS, System.currentTimeMillis())
            .apply()
    }

    fun loadUserData(): UserData? {
        if (isExpired(prefs.getLong(KEY_USER_TS, 0L))) { clearUserData(); return null }
        val raw = prefs.getString(KEY_USER, null) ?: return null
        return runCatching { gson.fromJson(raw, UserData::class.java) }.getOrNull()
    }

    fun clearUserData() {
        prefs.edit().remove(KEY_USER).remove(KEY_USER_TS).apply()
    }

    // ── Sesión ─────────────────────────────────────────────
    fun startSession() {
        prefs.edit()
            .putString(KEY_RESULTS, "{}")
            .putLong(KEY_SESS_TS, System.currentTimeMillis())
            .putInt(KEY_SESS_IDX, 0)
            .putBoolean(KEY_SESS_ON, true)
            .apply()
    }

    data class SessionInfo(val exists: Boolean, val lastIndex: Int, val count: Int)

    fun checkSession(): SessionInfo {
        val on  = prefs.getBoolean(KEY_SESS_ON, false)
        val ts  = prefs.getLong(KEY_SESS_TS, 0L)
        val idx = prefs.getInt(KEY_SESS_IDX, 0)
        val count = runCatching {
            JSONObject(prefs.getString(KEY_RESULTS, "{}") ?: "{}").length()
        }.getOrDefault(0)
        if (!on || isExpired(ts)) { if (on) clearSession(); return SessionInfo(false, 0, count) }
        return SessionInfo(true, idx, count)
    }

    fun saveResult(result: QueryResult) {
        val raw = prefs.getString(KEY_RESULTS, "{}") ?: "{}"
        val obj = runCatching { JSONObject(raw) }.getOrDefault(JSONObject())
        obj.put(result.companyId, JSONObject(gson.toJson(result)))
        prefs.edit().putString(KEY_RESULTS, obj.toString()).apply()
    }

    fun setSessionIndex(idx: Int) { prefs.edit().putInt(KEY_SESS_IDX, idx).apply() }

    fun doneSession() { prefs.edit().putBoolean(KEY_SESS_ON, false).apply() }

    fun loadResults(): Map<String, QueryResult> {
        if (isExpired(prefs.getLong(KEY_SESS_TS, 0L))) { clearSession(); return emptyMap() }
        val raw = prefs.getString(KEY_RESULTS, "{}") ?: "{}"
        return runCatching {
            val obj = JSONObject(raw)
            val map = mutableMapOf<String, QueryResult>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val qr = gson.fromJson(obj.getJSONObject(key).toString(), QueryResult::class.java)
                if (qr != null) map[key] = qr
            }
            map as Map<String, QueryResult>
        }.getOrDefault(emptyMap())
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_RESULTS).remove(KEY_SESS_TS)
            .remove(KEY_SESS_IDX).remove(KEY_SESS_ON)
            .apply()
    }

    fun saveLang(lang: String) { prefs.edit().putString(KEY_LANG, lang).apply() }
    fun loadLang(): String = prefs.getString(KEY_LANG, "es") ?: "es"

    fun clearAll() { prefs.edit().clear().apply() }

    fun expireStale() {
        val edit = prefs.edit()
        if (isExpired(prefs.getLong(KEY_USER_TS, 0L))) edit.remove(KEY_USER).remove(KEY_USER_TS)
        if (isExpired(prefs.getLong(KEY_SESS_TS, 0L))) {
            edit.remove(KEY_RESULTS).remove(KEY_SESS_TS).remove(KEY_SESS_IDX).remove(KEY_SESS_ON)
        }
        edit.apply()
    }
}
