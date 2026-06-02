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
import com.bibliotecadigital.app.ui.adapter.BookDetailFragment
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
            // Gera dados de teste e navega
            viewModel.seedHomeData()
            navigateToAcervo()
        }

        binding.tvVerTodosEmprestimos.setOnClickListener {
            // Em nosso app, os empréstimos ativos já estão na Home,
            // mas podemos redirecionar para uma visão mais detalhada ou perfil
            navigateToProfile()
        }

        binding.tvVerTodasReservas.setOnClickListener {
            navigateToAcervo()
        }
    }

    private fun navigateToProfile() {
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = R.id.nav_perfil
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.tvUserName.text = getString(R.string.home_welcome, state.userName)

                    // Se for Admin, podemos mostrar opções extras ou mudar o layout
                    if (state.isAdmin) {
                        setupAdminView(state)
                    } else {
                        setupUserView()
                    }

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
            onVerClick = { loan ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        BookDetailFragment.newInstance(loan.bookId, loan.title, loan.author)
                    )
                    .addToBackStack(null)
                    .commit()
            },
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
        val adapter = ReservationAdapter { reservation ->
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    BookDetailFragment.newInstance(reservation.bookId, reservation.title, reservation.author)
                )
                .addToBackStack(null)
                .commit()
        }

        binding.rvReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        adapter.submitList(reservations)
    }

    private fun setupAdminView(state: com.bibliotecadigital.app.viewmodels.HomeUiState) {
        binding.layoutAdminPanel.visibility = View.VISIBLE
        
        // Atualiza estatísticas no painel
        binding.tvTotalBooksCount.text = state.totalBooks.toString()
        binding.tvGlobalActiveLoansCount.text = state.totalActiveLoans.toString()
        binding.tvGlobalReservationsCount.text = state.totalReservations.toString()

        binding.btnNewBook.setOnClickListener {
            // Abre fragmento de cadastro de obra
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.bibliotecadigital.app.ui.admin.AddBookFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnManageUsers.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.bibliotecadigital.app.ui.admin.UserManagementFragment())
                .addToBackStack(null)
                .commit()
        }
        
        // Esconde as seções de empréstimos pessoais para o admin na visão geral
        binding.layoutLoansHeader.visibility = View.GONE
        binding.rvLoans.visibility = View.GONE
        binding.layoutReservationsHeader.visibility = View.GONE
        binding.rvReservations.visibility = View.GONE
        binding.layoutEmptyState.visibility = View.GONE
    }

    private fun setupUserView() {
        binding.layoutAdminPanel.visibility = View.GONE
    }

    private fun updateEmptyState(hasLoans: Boolean, hasReservations: Boolean) {
        val isEmpty = !hasLoans && !hasReservations

        binding.layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvLoans.visibility = if (hasLoans) View.VISIBLE else View.GONE
        binding.rvReservations.visibility = if (hasReservations) View.VISIBLE else View.GONE

        // RF05: O sistema deve exibir mensagem clara quando não houver empréstimos
        binding.layoutLoansHeader.visibility = if (hasLoans) View.VISIBLE else if (!hasReservations) View.GONE else View.VISIBLE
        binding.layoutReservationsHeader.visibility = if (hasReservations) View.VISIBLE else if (!hasLoans) View.GONE else View.VISIBLE
    }

    private fun navigateToAcervo() {
        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = R.id.nav_acervo
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}