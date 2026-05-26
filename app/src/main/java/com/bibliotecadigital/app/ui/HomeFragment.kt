package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.LoanAdapter
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.entity.Reservation
import com.bibliotecadigital.app.ReservationAdapter
import com.bibliotecadigital.app.databinding.FragmentHomeBinding
import com.bibliotecadigital.app.entity.Loan
import com.bibliotecadigital.app.viewmodels.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

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

        setupListeners()
        observeViewModel()
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

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.tvUserName.text = "Olá, ${state.userName}"

                    setupLoansAdapter(state.loans)
                    setupReservationsAdapter(state.reservations)
                    updateEmptyState(state.loans.isNotEmpty(), state.reservations.isNotEmpty())

                    if (state.error != null) {
                        Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupLoansAdapter(loans: List<Loan>) {
        val adapter = LoanAdapter(
            onVerClick = { /* navegação */ },
            onRenovarClick = { loan ->
                viewModel.renewLoan(loan)
                Snackbar.make(
                    binding.root,
                    "Renovando empréstimo de: ${loan.title}",
                    Snackbar.LENGTH_SHORT
                ).show()
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
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = R.id.nav_acervo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}