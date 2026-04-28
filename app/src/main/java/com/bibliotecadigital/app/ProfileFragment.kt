package com.bibliotecadigital.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        setupUserData()
        setupMenuRows()
        setupFines()
        setupClickListeners()
    }

    private fun setupUserData() {
        binding.tvAvatar.text = "LM"
        binding.tvUserName.text = "Lucas Mendes"
        binding.tvUserCourse.text = "Aluno · Direito"
        binding.tvBorrowed.text = "2"
        binding.tvReturned.text = "14"
        binding.tvReserved.text = "1"
    }

    private fun setupFines() {
        val fines = listOf(
            Fine("1", "Engenharia de Software", 5, 10.0, FineStatus.PENDENTE),
            Fine("2", "Código Limpo", 3, 6.0, FineStatus.PENDENTE),
            Fine("3", "Estruturas de Dados", 2, 4.0, FineStatus.PAGO)
        )

        // Calcula total pendente
        val totalPending = fines.filter { it.status == FineStatus.PENDENTE }.sumOf { it.amount }
        binding.tvTotalFine.text = String.format(Locale("pt", "BR"), "R$ %.2f", totalPending)

        // Configura lista
        val adapter = FineAdapter()
        binding.rvFines.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        adapter.submitList(fines)
    }

    private fun configRow(
        rowView: View,
        iconRes: Int,
        title: String,
        isDestructive: Boolean = false
    ) {
        val ivIcon = rowView.findViewById<android.widget.ImageView>(R.id.ivRowIcon)
        ivIcon.setImageResource(iconRes)

        val tvTitle = rowView.findViewById<TextView>(R.id.tvRowTitle)
        tvTitle.text = title

        if (isDestructive) {
            tvTitle.setTextColor(
                resources.getColor(R.color.text_red, null)
            )
            ivIcon.setColorFilter(resources.getColor(R.color.text_red, null))
            rowView.findViewById<TextView>(R.id.tvRowArrow).visibility = View.GONE
        }
    }

    private fun setupMenuRows() {
        configRow(
            binding.rowReadingHistory.root,
            R.drawable.ic_history,
            getString(R.string.profile_menu_history)
        )
        configRow(
            binding.rowReadingGoals.root,
            R.drawable.ic_flag,
            getString(R.string.profile_menu_goals)
        )
        configRow(
            binding.rowFines.root,
            R.drawable.ic_payments,
            getString(R.string.profile_menu_fines)
        )
        configRow(
            binding.rowChangePassword.root,
            R.drawable.ic_lock,
            getString(R.string.profile_menu_password)
        )
        configRow(
            binding.rowLogout.root,
            R.drawable.ic_exit_to_app,
            getString(R.string.profile_menu_logout),
            isDestructive = true
        )
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            val bottomSheet = EditProfileBottomSheet(
                currentName = binding.tvUserName.text.toString(),
                currentCourse = binding.tvUserCourse.text.toString(),
                onSave = { name, course ->
                    binding.tvUserName.text = name
                    binding.tvUserCourse.text = course

                    val initials = name
                        .split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .joinToString("") { it.first().uppercase() }
                    binding.tvAvatar.text = initials

                    Snackbar.make(
                        binding.root,
                        "Perfil atualizado com sucesso!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            )
            bottomSheet.show(childFragmentManager, EditProfileBottomSheet.TAG)
        }

        binding.btnSettings.setOnClickListener {
            openSettings()
        }

        binding.rowReadingHistory.root.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HistoricoFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rowReadingGoals.root.setOnClickListener {
            val intent = android.content.Intent(requireContext(), ReadingGoalsActivity::class.java)
            startActivity(intent)
        }

        binding.rowFines.root.setOnClickListener {
            val intent = Intent(requireContext(), FinesActivity::class.java)
            startActivity(intent)
        }

        binding.rowChangePassword.root.setOnClickListener {
            val bottomSheet = ChangePasswordBottomSheet()
            bottomSheet.show(childFragmentManager, ChangePasswordBottomSheet.TAG)
        }

        binding.rowLogout.root.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun openSettings() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SettingsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.profile_logout_confirm_title))
            .setMessage(getString(R.string.profile_logout_confirm_msg))
            .setPositiveButton(getString(R.string.btn_logout)) { _, _ ->
                val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putBoolean("is_logged_in", false)
                    remove("user_role")
                    remove("user_email")
                }.apply()

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