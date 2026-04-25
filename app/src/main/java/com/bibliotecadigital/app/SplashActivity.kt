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
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val onboardingFinished = prefs.getBoolean("onboarding_finished", false)
        val isUserLoggedIn = prefs.getBoolean("is_logged_in", false)

        val intent = when {
            !onboardingFinished -> Intent(this, OnboardingActivity::class.java)
            isUserLoggedIn -> Intent(this, MainActivity::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}