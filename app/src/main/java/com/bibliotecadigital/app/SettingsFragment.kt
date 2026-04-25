package com.bibliotecadigital.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.databinding.FragmentSettingsBinding

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
        binding.itemChangePassword.tvRowIcon.text = "🔒"
        binding.itemChangePassword.tvRowTitle.text = "Alterar Senha"

        // Seção PERSONALIZAÇÃO
        binding.itemFontSize.tvRowIcon.text = "Aa"
        binding.itemFontSize.tvRowTitle.text = "Tamanho da Fonte"

        binding.itemTheme.tvRowIcon.text = "🎨"
        binding.itemTheme.tvRowTitle.text = "Tema do Aplicativo"

        binding.itemRotation.tvRowIcon.text = "🔄"
        binding.itemRotation.tvRowTitle.text = "Rotação Automática"

        // Seção SESSÃO
        binding.itemLogout.tvRowIcon.text = "🚪"
        binding.itemLogout.tvRowTitle.text = "Sair"
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

        binding.itemLogout.root.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showFontSizeDialog() {
        val options = arrayOf("Pequeno", "Médio", "Grande")
        val current = prefs.getInt("pref_font_size", 1)

        AlertDialog.Builder(requireContext(), R.style.Theme_DEVWEB_Dialog)
            .setTitle("Tamanho da Fonte")
            .setSingleChoiceItems(options, current) { dialog, which ->
                prefs.edit().putInt("pref_font_size", which).apply()
                applyFontSize(which)
                dialog.dismiss()
            }
            .show()
    }

    private fun applyFontSize(sizeIndex: Int) {
        // Simulação de aplicação (requer reinicialização da activity ou ajuste dinâmico de escala)
        AlertDialog.Builder(requireContext(), R.style.Theme_DEVWEB_Dialog)
            .setMessage("Alteração salva. Reinicie o app para aplicar totalmente.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun toggleTheme() {
        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        val newMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        
        AppCompatDelegate.setDefaultNightMode(newMode)
        prefs.edit().putBoolean("pref_dark_mode", !isDarkMode).apply()
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
        
        val status = if (newValue) "Ativada" else "Desativada (Apenas Retrato)"
        AlertDialog.Builder(requireContext(), R.style.Theme_DEVWEB_Dialog)
            .setMessage("Rotação $status")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext(), R.style.Theme_DEVWEB_Dialog)
            .setTitle("Encerrar Sessão")
            .setMessage("Tem certeza que deseja sair da sua conta?")
            .setPositiveButton("Sair") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun logout() {
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