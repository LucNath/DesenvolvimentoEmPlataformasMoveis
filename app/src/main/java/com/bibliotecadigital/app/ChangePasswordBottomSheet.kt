package com.bibliotecadigital.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bibliotecadigital.app.databinding.DialogChangePasswordBinding
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
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Senha alterada com sucesso!", Snackbar.LENGTH_SHORT).show()
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateForm() {
        val current = binding.etCurrentPassword.text.toString()
        val new = binding.etNewPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        binding.btnChangePassword.isEnabled = current.isNotEmpty() && new.isNotEmpty() && confirm.isNotEmpty()
    }

    private fun performValidation(): Boolean {
        val current = binding.etCurrentPassword.text.toString()
        val new = binding.etNewPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        var isValid = true

        // Validação senha atual
        if (current != MOCK_CURRENT_PASSWORD) {
            binding.tilCurrentPassword.error = "Senha atual incorreta"
            isValid = false
        } else {
            binding.tilCurrentPassword.error = null
        }

        // Validação tamanho nova senha
        if (new.length < 6) {
            binding.tilNewPassword.error = "Senha muito curta"
            isValid = false
        } else {
            binding.tilNewPassword.error = null
        }

        // Validação coincidência
        if (new != confirm) {
            binding.tilConfirmPassword.error = "As senhas não coincidem"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ChangePasswordBottomSheet"
    }
}