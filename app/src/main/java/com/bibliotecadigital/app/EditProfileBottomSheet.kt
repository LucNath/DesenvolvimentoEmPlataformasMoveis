package com.bibliotecadigital.app

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bibliotecadigital.app.databinding.DialogEditProfileBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProfileBottomSheet(
    private val currentName: String,
    private val currentCourse: String,
    private val onSave: (name: String, course: String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Preenche com dados atuais
        binding.etName.setText(currentName)
        binding.etCourse.setText(currentCourse)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val course = binding.etCourse.text.toString().trim()

            if (!validateFields(name, course)) return@setOnClickListener

            onSave(name, course)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateFields(name: String, course: String): Boolean {
        if (name.isEmpty()) {
            binding.etName.error = "Preencha o nome"
            binding.etName.requestFocus()
            return false
        }
        if (name.length < 3) {
            binding.etName.error = "Nome muito curto"
            binding.etName.requestFocus()
            return false
        }
        if (course.isEmpty()) {
            binding.etCourse.error = "Preencha o curso"
            binding.etCourse.requestFocus()
            return false
        }
        return true
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
        const val TAG = "EditProfileBottomSheet"
    }
}