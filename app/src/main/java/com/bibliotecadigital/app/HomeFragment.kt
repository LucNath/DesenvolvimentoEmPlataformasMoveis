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

        setupListeners()
        loadData()
    }

    private fun setupListeners() {
        binding.btnExploreCatalog.setOnClickListener {
            navigateToAcervo()
        }

        binding.tvVerTodosEmprestimos.setOnClickListener {
            // Ação para ver todos os empréstimos
        }

        binding.tvVerTodasReservas.setOnClickListener {
            navigateToAcervo()
        }
    }

    private fun loadData() {
        // Iniciando com listas vazias conforme solicitado.
        // Ao integrar com o backend, estas listas serão preenchidas com dados reais.
        val loans = emptyList<Loan>()
        val reservations = emptyList<Reservation>()

        setupLoansAdapter(loans)
        setupReservationsAdapter(reservations)
        
        updateEmptyState(loans.isNotEmpty(), reservations.isNotEmpty())
    }

    private fun setupLoansAdapter(loans: List<Loan>) {
        val adapter = LoanAdapter(
            onVerClick = { /* navegação */ },
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

    private fun setupReservationsAdapter(reservations: List<Reservation>) {
        val adapter = ReservationAdapter { /* navegação */ }

        binding.rvReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        adapter.submitList(reservations)
    }

    private fun updateEmptyState(hasLoans: Boolean, hasReservations: Boolean) {
        val isEmpty = !hasLoans && !hasReservations
        
        binding.layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        
        // Controla as seções de Empréstimos
        binding.layoutLoansHeader.visibility = if (hasLoans) View.VISIBLE else View.GONE
        binding.rvLoans.visibility = if (hasLoans) View.VISIBLE else View.GONE
        
        // Controla as seções de Reservas
        binding.layoutReservationsHeader.visibility = if (hasReservations) View.VISIBLE else View.GONE
        binding.rvReservations.visibility = if (hasReservations) View.VISIBLE else View.GONE
    }

    private fun navigateToAcervo() {
        (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = R.id.nav_acervo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}