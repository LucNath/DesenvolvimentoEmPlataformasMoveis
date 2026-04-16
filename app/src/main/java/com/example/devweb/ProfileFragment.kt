package com.example.devweb

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserData(view)
        setupMenuRows(view)
        setupClickListeners(view)
    }

    private fun setupUserData(view: View) {
        view.findViewById<TextView>(R.id.tvAvatar).text      = "LM"
        view.findViewById<TextView>(R.id.tvUserName).text    = "Lucas Mendes"
        view.findViewById<TextView>(R.id.tvUserCourse).text  = "Aluno · Direito"
        view.findViewById<TextView>(R.id.tvBorrowed).text    = "2"
        view.findViewById<TextView>(R.id.tvReturned).text    = "14"
        view.findViewById<TextView>(R.id.tvReserved).text    = "1"
    }

    private fun setupMenuRows(view: View) {
        configRow(view.findViewById(R.id.rowReadingHistory),  "📖", "Histórico de Leituras")
        configRow(view.findViewById(R.id.rowReadingGoals),    "🎯", "Metas de Leitura")
        configRow(view.findViewById(R.id.rowFines),           "💰", "Multas e Pagamentos")
        configRow(view.findViewById(R.id.rowChangePassword),  "🔒", "Alterar Senha")
        configRow(
            view.findViewById(R.id.rowLogout),
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

    private fun setupClickListeners(view: View) {
        view.findViewById<View>(R.id.btnEditProfile).setOnClickListener {
            // TODO: TASK-15 — Pop-up Editar Perfil
            Toast.makeText(requireContext(), "Editar Perfil (em breve)", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.rowReadingHistory).setOnClickListener {
            Toast.makeText(requireContext(), "Histórico de Leituras", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.rowReadingGoals).setOnClickListener {
            Toast.makeText(requireContext(), "Metas de Leitura", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.rowFines).setOnClickListener {
            Toast.makeText(requireContext(), "Multas e Pagamentos", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.rowChangePassword).setOnClickListener {
            Toast.makeText(requireContext(), "Alterar Senha", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.rowLogout).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair da conta")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sair") { _, _ ->
                Toast.makeText(requireContext(), "Logout realizado!", Toast.LENGTH_SHORT).show()
                // TODO: navegar para LoginActivity
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}