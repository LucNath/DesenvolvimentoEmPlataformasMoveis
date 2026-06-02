package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.ReservationAdapter
import com.bibliotecadigital.app.databinding.FragmentMyReservationsBinding
import com.bibliotecadigital.app.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

class MyReservationsFragment : Fragment() {

    private var _binding: FragmentMyReservationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReservationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.tvToolbarTitle.text = "Minhas Reservas"
        binding.toolbar.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = ReservationAdapter { reservation ->
            // Navegar para detalhes do livro
        }
        binding.rvMyReservations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyReservations.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.reservations)
                binding.tvEmptyReservations.visibility = if (state.reservations.isEmpty()) View.VISIBLE else View.GONE
                binding.rvMyReservations.visibility = if (state.reservations.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}