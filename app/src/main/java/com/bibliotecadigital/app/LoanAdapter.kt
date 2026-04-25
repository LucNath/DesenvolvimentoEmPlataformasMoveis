package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemLoanBinding

class LoanAdapter(
    private val items: List<Loan>,
    private val onVerClick: (Loan) -> Unit,
    private val onRenovarClick: (Loan) -> Unit
) : RecyclerView.Adapter<LoanAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLoanBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvTitle.text = item.title
            tvAuthor.text = item.author
            tvDueDate.text = item.dueDate

            // Lógica de Urgência (Prazos ≤ 3 dias)
            if (item.isUrgent) {
                tvDueDate.setTextColor(ContextCompat.getColor(root.context, android.R.color.holo_red_dark))
                btnRenovar.visibility = View.VISIBLE
            } else {
                tvDueDate.setTextColor(ContextCompat.getColor(root.context, R.color.brand_orange))
                btnRenovar.visibility = View.GONE
            }

            if (item.coverRes != 0) {
                ivCover.setImageResource(item.coverRes)
            } else {
                ivCover.setImageResource(android.R.drawable.ic_dialog_info)
            }

            btnVer.setOnClickListener {
                onVerClick(item)
            }

            btnRenovar.setOnClickListener {
                onRenovarClick(item)
            }
        }
    }

    override fun getItemCount() = items.size
}