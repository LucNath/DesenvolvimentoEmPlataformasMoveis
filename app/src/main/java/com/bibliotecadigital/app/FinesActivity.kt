package com.bibliotecadigital.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.ActivityFinesBinding
import java.util.Locale

class FinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinesBinding
    private lateinit var adapter: FineAdapter
    private val finesList = mutableListOf<Fine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMockData()
        setupRecyclerView()
        updateTotalAmount()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupMockData() {
        finesList.add(Fine("1", "Engenharia de Software", 5, 10.0, FineStatus.PENDENTE))
        finesList.add(Fine("2", "Código Limpo", 3, 6.0, FineStatus.PENDENTE))
        finesList.add(Fine("3", "Estruturas de Dados", 2, 4.0, FineStatus.PAGO))
    }

    private fun setupRecyclerView() {
        adapter = FineAdapter { fine ->
            handlePayment(fine)
        }
        binding.rvFinesHistory.layoutManager = LinearLayoutManager(this)
        binding.rvFinesHistory.adapter = adapter
        refreshList()
    }

    private fun handlePayment(fine: Fine) {
        val index = finesList.indexOfFirst { it.id == fine.id }
        if (index != -1) {
            finesList[index] = fine.copy(status = FineStatus.PAGO)
            refreshList()
            updateTotalAmount()
        }
    }

    private fun refreshList() {
        if (finesList.isEmpty()) {
            binding.tvEmptyMessage.visibility = View.VISIBLE
            binding.rvFinesHistory.visibility = View.GONE
        } else {
            binding.tvEmptyMessage.visibility = View.GONE
            binding.rvFinesHistory.visibility = View.VISIBLE
            adapter.submitList(finesList.toList())
        }
    }

    private fun updateTotalAmount() {
        val totalPending = finesList.filter { it.status == FineStatus.PENDENTE }.sumOf { it.amount }
        binding.tvTotalFineAmount.text = String.format(Locale("pt", "BR"), "R$ %.2f", totalPending)
    }
}