package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemReadingHistoryBinding

class ReadingHistoryAdapter : ListAdapter<ReadingHistoryEntry, ReadingHistoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemReadingHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReadingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        with(holder.binding) {
            tvTitle.text = entry.title
            tvAuthor.text = entry.author
            tvReturnDate.text = root.context.getString(R.string.reading_history_returned_at, entry.returnDate)

            if (entry.coverRes != 0) {
                ivCover.setImageResource(entry.coverRes)
            } else {
                ivCover.setImageResource(R.drawable.bg_cover_placeholder)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ReadingHistoryEntry>() {
        override fun areItemsTheSame(oldItem: ReadingHistoryEntry, newItem: ReadingHistoryEntry) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReadingHistoryEntry, newItem: ReadingHistoryEntry) = oldItem == newItem
    }
}