package com.bibliotecadigital.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadigital.app.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animation
        val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.ivLogo.startAnimation(animation)
        binding.tvAppName.startAnimation(animation)

        // Delay and navigation
        Handler(Looper.getMainLooper()).postDelayed({
            checkNavigation()
        }, 2000)
    }

    private fun checkNavigation() {
        val appPrefs = AppPrefs(this)
        val authRepository = AuthRepository()
        
        val intent = when {
            !appPrefs.onboardingFinished -> Intent(this, OnboardingActivity::class.java)
            authRepository.isUserLoggedIn() -> {
                // Sincroniza o estado de login local com o Firebase
                appPrefs.isLoggedIn = true
                if (appPrefs.userRole == "admin") {
                    Intent(this, AdminDashboardActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
            }
            else -> {
                appPrefs.isLoggedIn = false
                Intent(this, LoginActivity::class.java)
            }
        }

        startActivity(intent)
        finish()
    }
}