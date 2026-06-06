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
import com.bibliotecadigital.app.databinding.FragmentAllLoansBinding
import com.bibliotecadigital.app.ui.adapter.BookDetailFragment
import com.bibliotecadigital.app.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AllLoansFragment : Fragment() {

    private var _binding: FragmentAllLoansBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllLoansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Meus Empréstimos"
        binding.toolbar.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
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
                Snackbar.make(binding.root, "Renovando: ${loan.title}", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvAllLoans.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllLoans.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.loans)
                    binding.tvEmptyLoans.visibility = if (state.loans.isEmpty()) View.VISIBLE else View.GONE
                    binding.rvAllLoans.visibility = if (state.loans.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}