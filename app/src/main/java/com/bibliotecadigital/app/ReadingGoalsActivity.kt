package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
        adapter = ReadingGoalAdapter(
            onIncrement = { id -> viewModel.incrementProgress(id) },
            onDelete = { id -> viewModel.deleteGoal(id) }
        )
        binding.rvGoals.apply {
            layoutManager = LinearLayoutManager(this@ReadingGoalsActivity)
            this.adapter = this@ReadingGoalsActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.goals.observe(this) { goals ->
            adapter.submitList(goals)
            updateSummary(goals)
            
            if (goals.isEmpty()) {
                binding.tvSummary.text = "Nenhuma meta cadastrada"
            }
        }
    }

    private fun updateSummary(goals: List<ReadingGoal>) {
        val activeGoals = goals.filter { it.status != GoalStatus.CONCLUIDA }
        if (activeGoals.isNotEmpty()) {
            val totalProgress = activeGoals.sumOf { it.progress }
            val totalTarget = activeGoals.sumOf { it.quantity }
            binding.tvSummary.text = "Você leu $totalProgress de $totalTarget livros das suas metas ativas."
        } else if (goals.isNotEmpty()) {
            binding.tvSummary.text = "Todas as suas metas foram concluídas! Parabéns!"
        } else {
            binding.tvSummary.text = "Crie uma meta para acompanhar seu progresso."
        }
    }

    private fun showAddGoalDialog() {
        val dialogBinding = DialogAddGoalBinding.inflate(LayoutInflater.from(this))
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etGoalTitle.text.toString().trim()
            val quantityStr = dialogBinding.etBooksQuantity.text.toString().trim()
            val deadline = dialogBinding.etDeadline.text.toString().trim()

            if (title.isEmpty() || quantityStr.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityStr.toIntOrNull() ?: 0
            if (quantity <= 0) {
                Toast.makeText(this, "Quantidade inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addGoal(title, quantity, deadline)
            dialog.dismiss()
            Toast.makeText(this, "Meta adicionada!", Toast.LENGTH_SHORT).show()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}