package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemFineBinding
import java.util.Locale

class FineAdapter : ListAdapter<Fine, FineAdapter.ViewHolder>(FineDiffCallback()) {

    class ViewHolder(val binding: ItemFineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvBookTitle.text = item.bookTitle
            tvDetails.text = root.context.getString(android.R.string.unknownName) // Placeholder
            tvDetails.text = "${item.daysLate} dias de atraso"
            tvAmount.text = String.format(Locale("pt", "BR"), "R$ %.2f", item.amount)
            tvStatus.text = item.status.name

            val context = root.context
            if (item.status == FineStatus.PENDENTE) {
                tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                tvStatus.setBackgroundResource(R.drawable.bg_status_red)
            } else {
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text))
                tvStatus.setBackgroundResource(R.drawable.bg_status_green)
            }
        }
    }

    class FineDiffCallback : DiffUtil.ItemCallback<Fine>() {
        override fun areItemsTheSame(oldItem: Fine, newItem: Fine): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Fine, newItem: Fine): Boolean = oldItem == newItem
    }
}