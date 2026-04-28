package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.ActivityReadingGoalsBinding
import com.bibliotecadigital.app.databinding.DialogAddGoalBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ReadingGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadingGoalsBinding
    private lateinit var viewModel: ReadingGoalsViewModel
    private lateinit var adapter: ReadingGoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadingGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReadingGoalsViewModel::class.java]

        setupUI()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.fabAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReadingGoalAdapter()
        binding.rvGoals.apply {
            layoutManager = LinearLayoutManager(this@ReadingGoalsActivity)
            this.adapter = this@ReadingGoalsActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.goals.observe(this) { goals ->
            adapter.submitList(goals)
            updateSummary(goals)
        }
    }

    private fun updateSummary(goals: List<ReadingGoal>) {
        if (goals.isNotEmpty()) {
            val firstGoal = goals[0]
            binding.tvSummary.text = "Você concluiu ${firstGoal.progress} de ${firstGoal.quantity} livros da meta."
        } else {
            binding.tvSummary.text = "Crie uma meta para acompanhar seu progresso."
        }
    }

    private fun showAddGoalDialog() {
        val dialogBinding = DialogAddGoalBinding.inflate(LayoutInflater.from(this))
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.btnSave.setOnClickListener {
            val quantityStr = dialogBinding.etBooksQuantity.text.toString().trim()
            val period = dialogBinding.etPeriod.text.toString().trim()

            if (quantityStr.isEmpty() || period.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityStr.toIntOrNull() ?: 0
            if (quantity <= 0) {
                Toast.makeText(this, "Quantidade inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addGoal(quantity, period)
            dialog.dismiss()
            Toast.makeText(this, "Meta adicionada!", Toast.LENGTH_SHORT).show()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}