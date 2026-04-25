package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentHomeBinding

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

        binding.tvUserName.text = "Lucas Mendes"

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
            loans,
            onVerClick = { loan ->
                Toast.makeText(requireContext(), "Ver detalhes: ${loan.title}", Toast.LENGTH_SHORT).show()
            },
            onRenovarClick = { loan ->
                Toast.makeText(requireContext(), "Renovação solicitada: ${loan.title}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvLoans.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
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

        val adapter = ReservationAdapter(reservations) { reservation ->
            Toast.makeText(requireContext(), "Ver reserva: ${reservation.title}", Toast.LENGTH_SHORT).show()
        }

        binding.rvReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            isNestedScrollingEnabled = false
        }

        binding.tvVerTodasReservas.setOnClickListener {
            Toast.makeText(requireContext(), "Navegar para todas as reservas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}