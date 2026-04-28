package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemReadingGoalBinding

class ReadingGoalAdapter : ListAdapter<ReadingGoal, ReadingGoalAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemReadingGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReadingGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = getItem(position)
        with(holder.binding) {
            tvGoalTitle.text = root.context.getString(R.string.reading_goal_label, goal.quantity, goal.period)
            tvGoalProgress.text = root.context.getString(R.string.reading_goal_progress, goal.progress, goal.quantity)
            progressBar.max = goal.quantity
            progressBar.progress = goal.progress
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ReadingGoal>() {
        override fun areItemsTheSame(oldItem: ReadingGoal, newItem: ReadingGoal) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReadingGoal, newItem: ReadingGoal) = oldItem == newItem
    }
}