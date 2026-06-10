package ci.nsu.mobile.main.data.token

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            if (value == null) prefs.edit().remove(KEY_TOKEN).apply()
            else prefs.edit().putString(KEY_TOKEN, value).apply()
        }

    var userId: Long?
        get() {
            val id = prefs.getLong(KEY_USER_ID, -1L)
            return if (id == -1L) null else id
        }
        set(value) {
            if (value == null) prefs.edit().remove(KEY_USER_ID).apply()
            else prefs.edit().putLong(KEY_USER_ID, value).apply()
        }

    fun clear() {
        token = null
        userId = null
    }
}
