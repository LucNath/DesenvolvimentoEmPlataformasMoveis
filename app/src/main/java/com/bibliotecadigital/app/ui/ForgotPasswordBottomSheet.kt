package com.bibliotecadigital.app.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.DialogForgotPasswordBinding
import com.bibliotecadigital.app.viewmodels.LoginResult
import com.bibliotecadigital.app.viewmodels.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ForgotPasswordBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels({ requireActivity() })

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSendReset.setOnClickListener {
            val email = binding.etEmailReset.text.toString().trim()
            if (validateEmail(email)) {
                viewModel.resetPassword(email)
            }
        }

        binding.btnCancelReset.setOnClickListener {
            dismiss()
        }
    }

    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.tilEmailReset.error = "Insira seu e-mail"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmailReset.error = "E-mail inválido"
                false
            }
            else -> {
                binding.tilEmailReset.error = null
                true
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginResult.collectLatest { result ->
                when (result) {
                    is LoginResult.Loading -> {
                        binding.btnSendReset.isEnabled = false
                        binding.btnCancelReset.isEnabled = false
                    }
                    is LoginResult.ResetEmailSent -> {
                        binding.btnSendReset.isEnabled = true
                        binding.btnCancelReset.isEnabled = true
                        dismiss()
                    }
                    is LoginResult.Error -> {
                        binding.btnSendReset.isEnabled = true
                        binding.btnCancelReset.isEnabled = true
                        binding.tilEmailReset.error = result.message
                    }
                    else -> {
                        binding.btnSendReset.isEnabled = true
                        binding.btnCancelReset.isEnabled = true
                    }
                }
            }
        }
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
        const val TAG = "ForgotPasswordBottomSheet"
    }
}