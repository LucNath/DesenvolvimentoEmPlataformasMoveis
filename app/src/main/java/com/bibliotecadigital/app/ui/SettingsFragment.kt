package com.bibliotecadigital.app.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.AppPrefs
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var appPrefs: AppPrefs

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
        appPrefs = AppPrefs(requireContext())

        setupMenu()
        setupListeners()
    }

    private fun setupMenu() {
        // Seção SEGURANÇA
        configRow(binding.itemChangePassword.root, R.drawable.ic_lock, getString(R.string.settings_change_password))

        // Seção PERSONALIZAÇÃO
        configRow(binding.itemFontSize.root, R.drawable.ic_text_fields, getString(R.string.settings_font_size))
        configRow(binding.itemRotation.root, R.drawable.ic_screen_rotation, getString(R.string.settings_rotation))
        configRow(binding.itemNotifications.root, R.drawable.ic_notifications, getString(R.string.settings_notifications))

        // Seção SESSÃO
        configRow(binding.itemLogout.root, R.drawable.ic_exit_to_app, getString(R.string.settings_logout), isDestructive = true)
    }

    private fun configRow(rowView: View, iconRes: Int, title: String, isDestructive: Boolean = false) {
        val ivIcon = rowView.findViewById<ImageView>(R.id.ivRowIcon)
        val tvTitle = rowView.findViewById<TextView>(R.id.tvRowTitle)
        val tvArrow = rowView.findViewById<TextView>(R.id.tvRowArrow)

        ivIcon.setImageResource(iconRes)
        tvTitle.text = title

        if (isDestructive) {
            val red = resources.getColor(R.color.status_unavailable, null)
            ivIcon.setColorFilter(red)
            tvTitle.setTextColor(red)
            tvArrow.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.itemChangePassword.root.setOnClickListener {
            val bottomSheet = ChangePasswordBottomSheet()
            bottomSheet.show(childFragmentManager, ChangePasswordBottomSheet.TAG)
        }

        binding.itemFontSize.root.setOnClickListener { showFontSizeDialog() }
        binding.itemRotation.root.setOnClickListener { toggleRotation() }

        binding.itemNotifications.root.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, NotificationSettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.itemLogout.root.setOnClickListener { showLogoutConfirmation() }
    }

    private fun showFontSizeDialog() {
        val options = arrayOf(
            getString(R.string.settings_font_small),
            getString(R.string.settings_font_medium),
            getString(R.string.settings_font_large)
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.settings_font_size))
            .setSingleChoiceItems(options, appPrefs.fontSize) { dialog, which ->
                appPrefs.fontSize = which
                dialog.dismiss()
                requireActivity().recreate()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun toggleRotation() {
        val newValue = !appPrefs.autoRotation
        appPrefs.autoRotation = newValue
        requireActivity().requestedOrientation = if (newValue) 
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        Toast.makeText(requireContext(), 
            if (newValue) getString(R.string.settings_rotation_on) else getString(R.string.settings_rotation_off), 
            Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.profile_logout_confirm_title))
            .setMessage(getString(R.string.settings_logout_confirm))
            .setPositiveButton(getString(R.string.btn_logout)) { _, _ ->
                appPrefs.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
