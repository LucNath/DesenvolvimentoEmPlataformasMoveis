package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemReadingGoalBinding

class ReadingGoalAdapter(
    private val onIncrement: (String) -> Unit,
    private val onDelete: (String) -> Unit
) : ListAdapter<ReadingGoal, ReadingGoalAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemReadingGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReadingGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = getItem(position)
        val context = holder.binding.root.context
        
        with(holder.binding) {
            tvGoalTitle.text = goal.title
            tvGoalDeadline.text = "Prazo: ${goal.deadline}"
            tvGoalProgress.text = "${goal.progress} de ${goal.quantity} livros lidos"
            progressBar.max = goal.quantity
            progressBar.progress = goal.progress

            // Status visual
            when (goal.status) {
                GoalStatus.CONCLUIDA -> {
                    tvGoalStatus.text = "Concluída"
                    tvGoalStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
                    btnRegisterReading.visibility = View.GONE
                }
                GoalStatus.ATRASADA -> {
                    tvGoalStatus.text = "Atrasada"
                    tvGoalStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                    btnRegisterReading.visibility = View.VISIBLE
                }
                GoalStatus.EM_ANDAMENTO -> {
                    tvGoalStatus.text = "Em andamento"
                    tvGoalStatus.setTextColor(ContextCompat.getColor(context, R.color.brand_orange))
                    btnRegisterReading.visibility = View.VISIBLE
                }
            }

            btnRegisterReading.setOnClickListener { onIncrement(goal.id) }
            btnDelete.setOnClickListener { onDelete(goal.id) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ReadingGoal>() {
        override fun areItemsTheSame(oldItem: ReadingGoal, newItem: ReadingGoal) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReadingGoal, newItem: ReadingGoal) = oldItem == newItem
    }
}