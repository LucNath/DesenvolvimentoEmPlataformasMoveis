package com.bibliotecadigital.app.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.DialogEditProfileBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditProfileBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogEditProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

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

        loadUserData()

        binding.btnSave.setOnClickListener {
            saveUserData()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {
                val document = db.collection("users").document(uid).get().await()
                if (document.exists()) {
                    val name = document.getString("name") ?: document.getString("displayName") ?: ""
                    val course = document.getString("course") ?: ""
                    
                    binding.etName.setText(name)
                    binding.etCourse.setText(course)
                }
            } catch (e: Exception) {
                // Silently fail or show small error if needed
            }
        }
    }

    private fun saveUserData() {
        val uid = auth.currentUser?.uid ?: return
        val newName = binding.etName.text.toString().trim()
        val newCourse = binding.etCourse.text.toString().trim()

        if (!validateFields(newName, newCourse)) return

        lifecycleScope.launch {
            try {
                // 1. Atualizar Firestore
                val updates = mapOf(
                    "name" to newName,
                    "course" to newCourse
                )
                db.collection("users").document(uid).update(updates).await()

                // 2. Atualizar FirebaseAuth display name
                val profileUpdates = userProfileChangeRequest {
                    displayName = newName
                }
                auth.currentUser?.updateProfile(profileUpdates)?.await()

                // Sucesso
                showSnackbar("Perfil atualizado com sucesso")
                dismiss()
            } catch (e: Exception) {
                showSnackbar("Erro ao atualizar perfil")
            }
        }
    }

    private fun showSnackbar(message: String) {
        val parentView = requireParentFragment().view ?: return
        Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun validateFields(name: String, course: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = "O nome não pode estar vazio"
            isValid = false
        } else if (name.length < 3) {
            binding.tilName.error = "O nome deve ter pelo menos 3 caracteres"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (course.isEmpty()) {
            binding.tilCourse.error = "O curso não pode estar vazio"
            isValid = false
        } else {
            binding.tilCourse.error = null
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
        const val TAG = "EditProfileBottomSheet"
    }
}