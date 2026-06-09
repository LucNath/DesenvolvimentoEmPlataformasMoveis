package com.bibliotecadigital.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bibliotecadigital.app.AppPrefs
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.FragmentProfileBinding
import com.bibliotecadigital.app.entity.User
import com.bibliotecadigital.app.repository.AuthRepository
import com.bibliotecadigital.app.viewmodels.ProfileState
import com.bibliotecadigital.app.viewmodels.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenuRows()
        setupClickListeners()
        observeViewModel()
        
        // Garante o carregamento dos dados
        profileViewModel.loadUserProfile()
    }

    private fun setupUserData(state: ProfileState.Success) {
        val user = state.user
        
        if (user.photoUrl.isNotEmpty()) {
            binding.ivUserProfile.visibility = View.VISIBLE
            binding.tvAvatar.visibility = View.GONE
            // Glide.with(this).load(user.photoUrl).into(binding.ivUserProfile)
        } else {
            binding.ivUserProfile.visibility = View.GONE
            binding.tvAvatar.visibility = View.VISIBLE
            binding.tvAvatar.text = user.name.take(2).uppercase()
        }

        binding.tvUserName.text = user.name
        binding.tvUserName.visibility = View.VISIBLE // Make sure it's visible if we want it
        
        val displayRole = when(user.role) {
            "admin" -> "Administrador"
            else -> "Estudante"
        }
        binding.tvUserCourse.text = displayRole
        
        binding.tvBorrowed.text = state.borrowedCount.toString()
        binding.tvReturned.text = state.returnedCount.toString()
        binding.tvReserved.text = state.reservedCount.toString()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collect { state ->
                    when (state) {
                        is ProfileState.Loading -> {
                            // Mostrar loading se necessário
                        }
                        is ProfileState.Success -> {
                            setupUserData(state)
                        }
                        is ProfileState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun configRow(
        rowView: View,
        iconRes: Int,
        title: String,
        isDestructive: Boolean = false
    ) {
        val ivIcon = rowView.findViewById<ImageView>(R.id.ivRowIcon)
        ivIcon.setImageResource(iconRes)

        val tvTitle = rowView.findViewById<TextView>(R.id.tvRowTitle)
        tvTitle.text = title

        if (isDestructive) {
            tvTitle.setTextColor(resources.getColor(R.color.text_red, null))
            ivIcon.setColorFilter(resources.getColor(R.color.text_red, null))
            rowView.findViewById<TextView>(R.id.tvRowArrow).visibility = View.GONE
        }
    }

    private fun setupMenuRows() {
        val isAdmin = AppPrefs(requireContext()).userRole == "admin"

        if (isAdmin) {
            binding.rowUserManagement.root.visibility = View.VISIBLE
            binding.dividerUserManagement.visibility = View.VISIBLE
            configRow(binding.rowUserManagement.root, R.drawable.ic_perfil, "Gestão de Usuários")
            binding.rowUserManagement.root.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, com.bibliotecadigital.app.ui.admin.UserManagementFragment())
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            binding.rowUserManagement.root.visibility = View.GONE
            binding.dividerUserManagement.visibility = View.GONE
        }

        // Atividades Literárias
        configRow(binding.rowMyReservations.root, R.drawable.ic_bookmark, getString(R.string.my_reservations_title))
        configRow(binding.rowReadingHistory.root, R.drawable.ic_history, getString(R.string.profile_menu_history))
        configRow(binding.rowReadingGoals.root, R.drawable.ic_check_circle, getString(R.string.profile_menu_goals))

        // Configurações e Acessibilidade
        configRow(binding.rowSettingsApp.root, R.drawable.ic_palette, getString(R.string.settings_title))
        configRow(binding.rowChangePassword.root, R.drawable.ic_lock, getString(R.string.profile_menu_password))
        configRow(binding.rowLogout.root, R.drawable.ic_exit_to_app, getString(R.string.profile_menu_logout), isDestructive = true)
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            val bottomSheet = EditProfileBottomSheet()
            bottomSheet.show(childFragmentManager, EditProfileBottomSheet.TAG)
        }

        binding.rowChangePassword.root.setOnClickListener {
            val bottomSheet = ChangePasswordBottomSheet()
            bottomSheet.show(childFragmentManager, ChangePasswordBottomSheet.TAG)
        }

        binding.rowLogout.root.setOnClickListener {
            showLogoutDialog()
        }

        binding.rowSettingsApp.root.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rowMyReservations.root.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MyReservationsFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rowReadingHistory.root.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ReadingHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rowReadingGoals.root.setOnClickListener {
            val bottomSheet = AddReadingGoalBottomSheet()
            bottomSheet.show(childFragmentManager, AddReadingGoalBottomSheet.TAG)
        }

        binding.btnAddGoal.setOnClickListener {
            val bottomSheet = AddReadingGoalBottomSheet()
            bottomSheet.show(childFragmentManager, AddReadingGoalBottomSheet.TAG)
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.profile_logout_confirm_title))
            .setMessage(getString(R.string.profile_logout_confirm_msg))
            .setPositiveButton(getString(R.string.btn_logout)) { _, _ ->
                AuthRepository().logout()
                AppPrefs(requireContext()).logout()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
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