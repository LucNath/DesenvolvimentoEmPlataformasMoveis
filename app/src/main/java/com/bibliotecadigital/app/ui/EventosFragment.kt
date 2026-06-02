package com.bibliotecadigital.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.EventAdapter
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.FragmentEventosBinding
import com.bibliotecadigital.app.viewmodels.EventViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EventosFragment : Fragment() {

    private var _binding: FragmentEventosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupCalendar()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSeedEvents.setOnClickListener {
            viewModel.seedEvents()
        }
    }

    private fun setupCalendar() {
        // Datas para o cabeçalho (exemplo fixo mantendo a lógica de UI original)
        val days = listOf("Todas", "15 Out", "16 Out", "17 Out", "18 Out", "19 Out", "20 Out")

        days.forEachIndexed { index, day ->
            val textView = TextView(requireContext()).apply {
                text = day.replace(" ", "\n")
                gravity = Gravity.CENTER
                setPadding(32, 16, 32, 16)
                setTextColor(Color.WHITE)
                textSize = 14f

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 16, 0)
                }
                layoutParams = params

                // Estado inicial
                if (day == "Todas") {
                    setBackgroundResource(R.drawable.bg_tag_orange)
                    setTextColor(Color.WHITE)
                    viewModel.setSelectedDate("Todas")
                } else {
                    setBackgroundResource(R.drawable.bg_card)
                    setTextColor(Color.parseColor("#4A90E2")) // Azul para datas não selecionadas
                }

                setOnClickListener {
                    // Limpa seleções anteriores
                    for (i in 0 until binding.llCalendar.childCount) {
                        val child = binding.llCalendar.getChildAt(i) as TextView
                        child.setBackgroundResource(R.drawable.bg_card)
                        child.setTextColor(Color.parseColor("#4A90E2"))
                    }
                    // Seleciona o atual
                    setBackgroundResource(R.drawable.bg_tag_orange)
                    setTextColor(Color.WHITE)

                    viewModel.setSelectedDate(day)
                }
            }
            binding.llCalendar.addView(textView)
        }
    }

    private fun setupRecyclerView() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        eventAdapter = EventAdapter(userId) { event, isEnrolling ->
            if (isEnrolling) {
                viewModel.enrollEvent(event.id, userId)
            } else {
                viewModel.cancelEnrollment(event.id, userId)
            }
        }

        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { events ->
                        eventAdapter.submitList(events)
                        updateEmptyState(events.isEmpty())
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.actionMessage.collect { message ->
                        message?.let {
                            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                            viewModel.clearActionMessage()
                        }
                    }
                }
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvEvents.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}