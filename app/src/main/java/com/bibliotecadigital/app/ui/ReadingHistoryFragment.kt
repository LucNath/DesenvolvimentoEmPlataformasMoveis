package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentReadingHistoryBinding
import com.bibliotecadigital.app.ui.adapter.HistoryAdapter
import com.bibliotecadigital.app.viewmodels.ReadingHistoryViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ReadingHistoryFragment : Fragment() {

    private var _binding: FragmentReadingHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadingHistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        
        viewModel.loadHistory()
    }

    private fun setupUI() {
        // RF-HL05: Botão de retorno para o Perfil
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        // RF-HL06: Registro de avaliações
        historyAdapter = HistoryAdapter { loanId, rating ->
            viewModel.updateRating(loanId, rating)
        }
        
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupSearch() {
        // RF-HL07: Pesquisa dentro do histórico
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observa a lista filtrada (RF-HL01, RF-HL02, RF-HL07)
                launch {
                    viewModel.filteredHistory.collect { items ->
                        historyAdapter.submitList(items)
                        // RF-HL04: Mensagem informativa quando vazio
                        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                // Estado de carregamento
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                // Mensagens de feedback (Avaliação, Erros)
                launch {
                    viewModel.message.collect { msg ->
                        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
