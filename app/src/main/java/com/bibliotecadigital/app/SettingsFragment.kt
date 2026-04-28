package com.bibliotecadigital.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        setupMenu()
        setupListeners()
    }

    private fun setupMenu() {
        // Seção SEGURANÇA
        binding.itemChangePassword.ivRowIcon.setImageResource(R.drawable.ic_lock)
        binding.itemChangePassword.tvRowTitle.text = getString(R.string.settings_change_password)

        // Seção PERSONALIZAÇÃO
        binding.itemFontSize.ivRowIcon.setImageResource(R.drawable.ic_text_fields)
        binding.itemFontSize.tvRowTitle.text = getString(R.string.settings_font_size)

        binding.itemTheme.ivRowIcon.setImageResource(R.drawable.ic_palette)
        binding.itemTheme.tvRowTitle.text = getString(R.string.settings_theme)

        binding.itemRotation.ivRowIcon.setImageResource(R.drawable.ic_screen_rotation)
        binding.itemRotation.tvRowTitle.text = getString(R.string.settings_rotation)

        binding.itemNotifications.ivRowIcon.setImageResource(R.drawable.ic_notifications)
        binding.itemNotifications.tvRowTitle.text = getString(R.string.settings_notifications)

        // Seção SESSÃO
        binding.itemLogout.ivRowIcon.setImageResource(R.drawable.ic_exit_to_app)
        binding.itemLogout.ivRowIcon.setColorFilter(resources.getColor(R.color.text_red, null))
        binding.itemLogout.tvRowTitle.text = getString(R.string.settings_logout)
        binding.itemLogout.tvRowTitle.setTextColor(resources.getColor(R.color.text_red, null))
        binding.itemLogout.tvRowArrow.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.itemChangePassword.root.setOnClickListener {
            val bottomSheet = ChangePasswordBottomSheet()
            bottomSheet.show(childFragmentManager, ChangePasswordBottomSheet.TAG)
        }

        binding.itemFontSize.root.setOnClickListener {
            showFontSizeDialog()
        }

        binding.itemTheme.root.setOnClickListener {
            toggleTheme()
        }

        binding.itemRotation.root.setOnClickListener {
            toggleRotation()
        }

        binding.itemNotifications.root.setOnClickListener {
            val intent = Intent(requireContext(), NotificationPrefsActivity::class.java)
            startActivity(intent)
        }

        binding.itemLogout.root.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showFontSizeDialog() {
        val options = arrayOf(
            getString(R.string.settings_font_small),
            getString(R.string.settings_font_medium),
            getString(R.string.settings_font_large)
        )
        val current = prefs.getInt("pref_font_size", 1)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_font_size))
            .setSingleChoiceItems(options, current) { dialog, which ->
                prefs.edit().putInt("pref_font_size", which).apply()
                dialog.dismiss()
                requireActivity().recreate()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun toggleTheme() {
        val isDarkMode = prefs.getBoolean("pref_dark_mode", true)
        val newMode = !isDarkMode
        
        prefs.edit().putBoolean("pref_dark_mode", newMode).apply()
        
        AppCompatDelegate.setDefaultNightMode(
            if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        requireActivity().recreate()
    }

    private fun toggleRotation() {
        val currentRotation = prefs.getBoolean("pref_rotation", true)
        val newValue = !currentRotation
        
        prefs.edit().putBoolean("pref_rotation", newValue).apply()
        
        val orientation = if (newValue) 
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED 
        else 
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            
        requireActivity().requestedOrientation = orientation
        
        val status = if (newValue) getString(R.string.settings_rotation_on) else getString(R.string.settings_rotation_off)
        Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.profile_logout_confirm_title))
            .setMessage(getString(R.string.settings_logout_confirm))
            .setPositiveButton(getString(R.string.btn_logout)) { _, _ ->
                logout()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun logout() {
        prefs.edit().apply {
            putBoolean("is_logged_in", false)
            remove("user_role")
            remove("user_email")
            apply()
        }

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}