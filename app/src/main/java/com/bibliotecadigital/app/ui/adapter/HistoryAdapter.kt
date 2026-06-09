package com.bibliotecadigital.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.ItemHistoryBinding
import com.bibliotecadigital.app.entity.Loan

class HistoryAdapter(
    private val onRatingChanged: (String, Float) -> Unit
) : ListAdapter<Loan, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loan: Loan) {
            binding.tvTitle.text = loan.title
            binding.tvAuthor.text = loan.author
            binding.tvReturnDate.text = "Devolvido em: ${loan.returnDate}"
            
            binding.ivCover.load(loan.coverUrl) {
                placeholder(R.drawable.bg_cover_placeholder)
                error(R.drawable.bg_cover_placeholder)
            }

            // RF-HL06: Registro de avaliações
            binding.ratingBar.rating = loan.rating ?: 0f
            binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    onRatingChanged(loan.id, rating)
                }
            }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<Loan>() {
        override fun areItemsTheSame(oldItem: Loan, newItem: Loan) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Loan, newItem: Loan) = oldItem == newItem
    }
}
