package com.bibliotecadigital.app.ui

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.DialogChangePasswordBinding
import com.bibliotecadigital.app.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ChangePasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels({ requireParentFragment() })

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateForm()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etCurrentPassword.addTextChangedListener(textWatcher)
        binding.etNewPassword.addTextChangedListener(textWatcher)
        binding.etConfirmPassword.addTextChangedListener(textWatcher)

        binding.btnChangePassword.setOnClickListener {
            if (performLocalValidation()) {
                performFirebaseUpdate()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun performFirebaseUpdate() {
        val current = binding.etCurrentPassword.text.toString().trim()
        val new = binding.etNewPassword.text.toString().trim()

        binding.btnChangePassword.isEnabled = false
        binding.btnCancel.isEnabled = false

        lifecycleScope.launch {
            profileViewModel.changePassword(current, new)
                .onSuccess {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Oba! Sua senha foi atualizada com sucesso ✨",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    dismiss()
                }
                .onFailure { e ->
                    binding.btnChangePassword.isEnabled = true
                    binding.btnCancel.isEnabled = true
                    binding.tilCurrentPassword.error = "Opa! Não conseguimos alterar sua senha: ${e.message} 😵"
                }
        }
    }

    private fun validateForm() {
        val current = binding.etCurrentPassword.text.toString().trim()
        val new = binding.etNewPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        binding.btnChangePassword.isEnabled = current.isNotEmpty() && new.isNotEmpty() && confirm.isNotEmpty()

        // Limpa erros ao digitar
        binding.tilCurrentPassword.error = null
        binding.tilNewPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    private fun performLocalValidation(): Boolean {
        val new = binding.etNewPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        var isValid = true

        // Validação tamanho nova senha
        if (new.length < 6) {
            binding.tilNewPassword.error = "Essa senha está muito curtinha. Que tal pelo menos 6 caracteres? 🔒"
            isValid = false
        }

        // Validação coincidência
        if (new != confirm) {
            binding.tilConfirmPassword.error = "As senhas novas não ficaram iguais. Pode conferir? 🧐"
            isValid = false
        }

        return isValid
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ChangePasswordBottomSheet"
    }
}