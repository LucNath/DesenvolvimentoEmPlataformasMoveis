package com.bibliotecadigital.app

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        appPrefs = AppPrefs(this)
        AppCompatDelegate.setDefaultNightMode(
            if (appPrefs.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        
        super.onCreate(savedInstanceState)
        
        applyPreferences()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_acervo -> AcervoFragment()
                R.id.nav_eventos -> EventosFragment()
                R.id.nav_notificacoes -> NotificacoesFragment()
                R.id.nav_perfil -> ProfileFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun applyPreferences() {
        // 1. Tema
        AppCompatDelegate.setDefaultNightMode(
            if (appPrefs.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // 2. Rotação
        requestedOrientation = if (appPrefs.autoRotation) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = AppPrefs(newBase)
        val fontSizeIndex = prefs.fontSize // 0: Pequeno, 1: Médio, 2: Grande
        
        val scale = when (fontSizeIndex) {
            0 -> 0.85f
            2 -> 1.15f
            else -> 1.0f
        }

        val configuration = Configuration(newBase.resources.configuration)
        configuration.fontScale = scale
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }
}