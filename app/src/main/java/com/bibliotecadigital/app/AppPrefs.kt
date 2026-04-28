package com.bibliotecadigital.app

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_ONBOARDING_FINISHED = "onboarding_finished"
        private const val KEY_DARK_MODE = "pref_dark_mode"
        private const val KEY_FONT_SIZE = "pref_font_size"
        private const val KEY_ROTATION = "pref_rotation"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit { putBoolean(KEY_IS_LOGGED_IN, value) }

    var userRole: String?
        get() = prefs.getString(KEY_USER_ROLE, "user")
        set(value) = prefs.edit { putString(KEY_USER_ROLE, value) }

    var userEmail: String?
        get() = prefs.getString(KEY_USER_EMAIL, null)
        set(value) = prefs.edit { putString(KEY_USER_EMAIL, value) }

    var onboardingFinished: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_FINISHED, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_FINISHED, value) }

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, true)
        set(value) = prefs.edit { putBoolean(KEY_DARK_MODE, value) }

    var fontSize: Int
        get() = prefs.getInt(KEY_FONT_SIZE, 1)
        set(value) = prefs.edit { putInt(KEY_FONT_SIZE, value) }

    var autoRotation: Boolean
        get() = prefs.getBoolean(KEY_ROTATION, true)
        set(value) = prefs.edit { putBoolean(KEY_ROTATION, value) }

    fun logout() {
        prefs.edit {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USER_ROLE)
            remove(KEY_USER_EMAIL)
        }
    }
}