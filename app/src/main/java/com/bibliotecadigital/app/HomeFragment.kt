package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvUserName.text = getString(R.string.user_name_placeholder)

        setupLoans()
        setupReservations()
    }

    private fun setupLoans() {
        val loans = listOf(
            Loan(
                id = "1",
                title = "Código Limpo",
                author = "Robert C. Martin",
                dueDate = "Vence em 2 dias",
                isUrgent = true,
                coverRes = 0
            ),
            Loan(
                id = "2",
                title = "Direito Constitucional Esquematizado",
                author = "Pedro Lenza",
                dueDate = "Vence em 8 dias",
                isUrgent = false,
                coverRes = 0
            )
        )

        val adapter = LoanAdapter(
            onVerClick = { loan ->
                // Navegação removida
            },
            onRenovarClick = { loan ->
                Snackbar.make(binding.root, "Renovação solicitada para: ${loan.title}", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvLoans.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        adapter.submitList(loans)
    }

    private fun setupReservations() {
        val reservations = listOf(
            Reservation(
                id = "1",
                title = "Algoritmos: Teoria e Prática",
                author = "Cormen, Leiserson, Rivest",
                coverRes = 0,
                queuePosition = 3
            ),
            Reservation(
                id = "2",
                title = "Inteligência Artificial: Uma Abordagem Moderna",
                author = "Stuart Russell, Peter Norvig",
                coverRes = 0,
                queuePosition = 1
            ),
            Reservation(
                id = "3",
                title = "Design Patterns: Elements of Reusable Object-Oriented Software",
                author = "Erich Gamma, Richard Helm",
                coverRes = 0,
                queuePosition = 5
            )
        )

        val adapter = ReservationAdapter { reservation ->
             // Navegação removida
        }

        binding.rvReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            isNestedScrollingEnabled = false
        }
        adapter.submitList(reservations)

        binding.tvVerTodasReservas.setOnClickListener {
            // Navegar para aba de Acervo ou filtro de reservas
            (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = R.id.nav_acervo
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}