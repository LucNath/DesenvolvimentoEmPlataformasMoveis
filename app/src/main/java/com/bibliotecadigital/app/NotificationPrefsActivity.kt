package com.bibliotecadigital.app

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bibliotecadigital.app.databinding.ActivityNotificationPrefsBinding

class NotificationPrefsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationPrefsBinding
    private lateinit var prefs: SharedPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, getString(R.string.notif_permission_denied), Toast.LENGTH_LONG).show()
            disableSwitches()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationPrefsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setupListeners()
        loadPreferences()
        setupPreviews()
        checkNotificationPermission()
    }

    private fun setupPreviews() {
        // Preview Empréstimos
        binding.previewLoans.tvMessage.text = getString(R.string.preview_loan_msg)
        binding.previewLoans.tvTimestamp.text = getString(R.string.preview_time_5m)
        binding.previewLoans.ivIcon.setImageResource(R.drawable.ic_timer)
        binding.previewLoans.viewUnreadIndicator.visibility = android.view.View.VISIBLE

        // Preview Reservas
        binding.previewReservations.tvMessage.text = getString(R.string.preview_res_msg)
        binding.previewReservations.tvTimestamp.text = getString(R.string.preview_time_1h)
        binding.previewReservations.ivIcon.setImageResource(R.drawable.ic_check_circle)
        binding.previewReservations.viewUnreadIndicator.visibility = android.view.View.VISIBLE

        // Preview Eventos
        binding.previewEvents.tvMessage.text = getString(R.string.preview_event_msg)
        binding.previewEvents.tvTimestamp.text = getString(R.string.preview_time_2h)
        binding.previewEvents.ivIcon.setImageResource(R.drawable.ic_event)
        binding.previewEvents.viewUnreadIndicator.visibility = android.view.View.VISIBLE
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.switchLoans.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notif_loans", isChecked).apply()
        }

        binding.switchReservations.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notif_reservations", isChecked).apply()
        }

        binding.switchEvents.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notif_events", isChecked).apply()
        }
    }

    private fun loadPreferences() {
        binding.switchLoans.isChecked = prefs.getBoolean("notif_loans", true)
        binding.switchReservations.isChecked = prefs.getBoolean("notif_reservations", true)
        binding.switchEvents.isChecked = prefs.getBoolean("notif_events", true)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun disableSwitches() {
        binding.switchLoans.isEnabled = false
        binding.switchReservations.isEnabled = false
        binding.switchEvents.isEnabled = false
        
        binding.switchLoans.isChecked = false
        binding.switchReservations.isChecked = false
        binding.switchEvents.isChecked = false
    }
}