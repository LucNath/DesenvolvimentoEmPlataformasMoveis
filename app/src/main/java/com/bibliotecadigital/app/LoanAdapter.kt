package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemLoanBinding

class LoanAdapter(
    private val items: List<Loan>,
    private val onVerClick: (Loan) -> Unit
) : RecyclerView.Adapter<LoanAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLoanBinding) :
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

            if (item.coverRes != 0) {
                ivCover.setImageResource(item.coverRes)
            }

            btnVer.setOnClickListener {
                onVerClick(item)
            }
        }
    }

    override fun getItemCount() = items.size
}