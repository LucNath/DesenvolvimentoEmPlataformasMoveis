package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemLoanBinding

class LoanAdapter(
    private val onVerClick: (Loan) -> Unit,
    private val onRenovarClick: (Loan) -> Unit
) : ListAdapter<Loan, LoanAdapter.ViewHolder>(LoanDiffCallback()) {

    class ViewHolder(val binding: ItemLoanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTitle.text = item.title
            tvAuthor.text = item.author
            tvDueDate.text = item.dueDate

            if (item.isUrgent) {
                tvDueDate.setTextColor(ContextCompat.getColor(root.context, android.R.color.holo_red_dark))
                btnRenovar.visibility = View.VISIBLE
            } else {
                tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.brand_orange))
                btnRenovar.visibility = View.GONE
            }

            ivCover.setImageResource(if (item.coverRes != 0) item.coverRes else R.drawable.bg_cover_placeholder)

            btnVer.setOnClickListener { onVerClick(item) }
            btnRenovar.setOnClickListener { onRenovarClick(item) }
        }
    }

    class LoanDiffCallback : DiffUtil.ItemCallback<Loan>() {
        override fun areItemsTheSame(oldItem: Loan, newItem: Loan): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Loan, newItem: Loan): Boolean = oldItem == newItem
    }
}