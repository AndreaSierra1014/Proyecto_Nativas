package com.techstore.app.data.local

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("techstore_session", Context.MODE_PRIVATE)

    var uid: String?
        get() = prefs.getString(KEY_UID, null)
        set(value) = prefs.edit().putString(KEY_UID, value).apply()

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var role: String?
        get() = prefs.getString(KEY_ROLE, null)
        set(value) = prefs.edit().putString(KEY_ROLE, value).apply()

    var fullName: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var biometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC, value).apply()

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_UID = "uid"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_NAME = "name"
        private const val KEY_BIOMETRIC = "biometric"
    }
}
