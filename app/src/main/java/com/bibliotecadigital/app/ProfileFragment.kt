package com.bibliotecadigital.app

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentProfileBinding
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
        val adapter = FineAdapter(fines)
        binding.rvFines.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    private fun setupMenuRows() {
        configRow(binding.rowReadingHistory.root, "📖", "Histórico de Leituras")
        configRow(binding.rowReadingGoals.root, "🎯", "Metas de Leitura")
        configRow(binding.rowFines.root, "💰", "Multas e Pagamentos")
        configRow(binding.rowChangePassword.root, "🔒", "Alterar Senha")
        configRow(
            binding.rowLogout.root,
            "📕",
            "Sair da conta",
            isDestructive = true
        )
    }

    private fun configRow(
        rowView: View,
        icon: String,
        title: String,
        isDestructive: Boolean = false
    ) {
        rowView.findViewById<TextView>(R.id.tvRowIcon).text = icon

        val tvTitle = rowView.findViewById<TextView>(R.id.tvRowTitle)
        tvTitle.text = title

        if (isDestructive) {
            tvTitle.setTextColor(
                resources.getColor(R.color.text_red, null)
            )
            rowView.findViewById<TextView>(R.id.tvRowArrow).visibility = View.GONE
        }
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

        binding.rowReadingHistory.root.setOnClickListener {
            Toast.makeText(requireContext(), "Histórico de Leituras", Toast.LENGTH_SHORT).show()
        }

        binding.rowReadingGoals.root.setOnClickListener {
            Toast.makeText(requireContext(), "Metas de Leitura", Toast.LENGTH_SHORT).show()
        }

        binding.rowFines.root.setOnClickListener {
            Toast.makeText(requireContext(), "Multas e Pagamentos", Toast.LENGTH_SHORT).show()
        }

        binding.rowChangePassword.root.setOnClickListener {
            Toast.makeText(requireContext(), "Alterar Senha", Toast.LENGTH_SHORT).show()
        }

        binding.rowLogout.root.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair da conta")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sair") { _, _ ->
                Toast.makeText(requireContext(), "Logout realizado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}