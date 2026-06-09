package com.bibliotecadigital.app.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.AppPrefs
import com.bibliotecadigital.app.ui.HomeFragment
import com.bibliotecadigital.app.NetworkMonitor
import com.bibliotecadigital.app.ui.ProfileFragment
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.ActivityMainBinding
import com.bibliotecadigital.app.viewmodels.NotificationsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appPrefs: AppPrefs
    private lateinit var networkMonitor: NetworkMonitor
    private val notificationsViewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        appPrefs = AppPrefs(this)
        networkMonitor = NetworkMonitor(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)

        applyPreferences()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupNetworkMonitor()
        observeNotifications()
        observeUserRole()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.root.findViewById<View>(R.id.btnRetry)?.setOnClickListener {
            // Recarrega o estado de rede ou apenas tenta dar o refresh na tela
            // Como o NetworkMonitor já é reativo, ele vai atualizar o estado
        }
    }

    private fun setupNetworkMonitor() {
        val offlineLayout = binding.root.findViewById<View>(R.id.offline_layout)

        networkMonitor.isConnected
            .onEach { isConnected ->
                offlineLayout?.visibility = if (isConnected) View.GONE else View.VISIBLE
            }
            .launchIn(lifecycleScope)
    }

    private fun observeNotifications() {
        notificationsViewModel.notifications
            .onEach { notifications ->
                val unreadCount = notifications.count { !it.isRead }
                val badge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_notificacoes)
                if (unreadCount > 0) {
                    badge.isVisible = true
                    badge.number = unreadCount
                } else {
                    badge.isVisible = false
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun observeUserRole() {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->
                val newRole = snapshot?.getString("role") ?: "student"
                if (newRole != appPrefs.userRole) {
                    appPrefs.userRole = newRole
                    // Atualiza a UI da navegação
                    updateNavigationVisibility(newRole == "admin")
                }
            }
    }

    private fun updateNavigationVisibility(isAdmin: Boolean) {
        binding.bottomNavigation.menu.findItem(R.id.nav_notificacoes).isVisible = !isAdmin
        // Se o usuário estiver na aba de notificações e for promovido, move ele para a Home
        if (isAdmin && binding.bottomNavigation.selectedItemId == R.id.nav_notificacoes) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun setupNavigation() {
        val isAdmin = appPrefs.userRole == "admin"
        updateNavigationVisibility(isAdmin)

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
        // 1. Rotação
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