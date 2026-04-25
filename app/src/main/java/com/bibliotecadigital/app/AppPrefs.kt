package com.bibliotecadigital.app

import android.content.Context
import android.content.SharedPreferences

object AppPrefs {
    private const val PREFS_NAME = "app_prefs"
    
    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isDarkMode(context: Context) = getPrefs(context).getBoolean("pref_dark_mode", true)
    
    fun getFontSize(context: Context) = getPrefs(context).getFloat("pref_font_size", 1.0f)
    
    fun getUserRole(context: Context) = getPrefs(context).getString("user_role", "user") ?: "user"
    
    fun isOnboardingFinished(context: Context) = getPrefs(context).getBoolean("onboarding_finished", false)
}