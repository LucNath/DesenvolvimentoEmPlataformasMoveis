package com.bibliotecadigital.app

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bibliotecadigital.app.databinding.DialogChangePasswordBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class ChangePasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogChangePasswordBinding? = null
    private val binding get() = _binding!!

    // Senha atual mockada
    private val MOCK_CURRENT_PASSWORD = "senha123"

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
            if (performValidation()) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Senha alterada com sucesso!",
                    Snackbar.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
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

    private fun performValidation(): Boolean {
        val current = binding.etCurrentPassword.text.toString().trim()
        val new = binding.etNewPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        var isValid = true

        // Validação senha atual
        if (current != MOCK_CURRENT_PASSWORD) {
            binding.tilCurrentPassword.error = "Senha atual incorreta"
            isValid = false
        }

        // Validação tamanho nova senha
        if (new.length < 6) {
            binding.tilNewPassword.error = "Senha muito curta (mínimo 6)"
            isValid = false
        }

        // Validação coincidência
        if (new != confirm) {
            binding.tilConfirmPassword.error = "As senhas não coincidem"
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