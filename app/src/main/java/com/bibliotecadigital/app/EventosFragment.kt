package com.bibliotecadigital.app

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentEventosBinding

class EventosFragment : Fragment() {

    private var _binding: FragmentEventosBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private var allEvents = mutableListOf<Event>()

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
        setupMockData()
        setupCalendar()
        setupRecyclerView()
    }

    private fun setupMockData() {
        allEvents = mutableListOf(
            Event("1", "Clube de Leitura: 1984", "15 Out", "19:00", "Prof. Ricardo Silva", 12),
            Event("2", "Oficina de Poesia", "15 Out", "14:00", "Maria Oliveira", 0),
            Event("3", "Palestra: IA na Literatura", "16 Out", "10:00", "Dr. Fabio Santos", 50),
            Event("4", "Workshop de Escrita", "17 Out", "15:30", "Ana Costa", 8),
            Event("5", "Lançamento de Livro", "18 Out", "18:00", "Autor Convidado", 100)
        )
    }

    private fun setupCalendar() {
        val days = listOf("15\nOut", "16\nOut", "17\nOut", "18\nOut", "19\nOut", "20\nOut", "21\nOut")
        
        days.forEachIndexed { index, day ->
            val textView = TextView(requireContext()).apply {
                text = day
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

                if (index == 0) {
                    setBackgroundResource(R.drawable.bg_tag_orange)
                } else {
                    setBackgroundResource(R.drawable.bg_card)
                }

                setOnClickListener {
                    // Limpa seleções anteriores
                    for (i in 0 until binding.llCalendar.childCount) {
                        binding.llCalendar.getChildAt(i).setBackgroundResource(R.drawable.bg_card)
                    }
                    // Seleciona o atual
                    setBackgroundResource(R.drawable.bg_tag_orange)
                    
                    val dateFilter = day.replace("\n", " ")
                    filterEvents(dateFilter)
                }
            }
            binding.llCalendar.addView(textView)
        }
    }

    private fun filterEvents(date: String) {
        val filtered = allEvents.filter { it.date == date }
        eventAdapter.submitList(filtered)
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            // Lógica adicional se necessário (ex: analytics)
        }
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = eventAdapter
        eventAdapter.submitList(allEvents.filter { it.date == "15 Out" })
    }

    private fun Int.spToPx(): Float {
        return this * resources.displayMetrics.scaledDensity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}