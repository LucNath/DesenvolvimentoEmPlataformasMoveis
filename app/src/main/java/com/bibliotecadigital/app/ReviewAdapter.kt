package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemReviewBinding

class ReviewAdapter(
    private val onEditClick: (Review) -> Unit,
    private val onDeleteClick: (Review) -> Unit
) : ListAdapter<Review, ReviewAdapter.ViewHolder>(ReviewDiffCallback()) {

    class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = getItem(position)
        with(holder.binding) {
            tvUserName.text = review.userName
            tvReviewDate.text = review.date
            tvComment.text = review.comment
            reviewRating.rating = review.rating

            root.setOnLongClickListener {
                android.widget.PopupMenu(root.context, it).apply {
                    menu.add("Editar")
                    menu.add("Excluir")
                    setOnMenuItemClickListener { item ->
                        when (item.title) {
                            "Editar" -> onEditClick(review)
                            "Excluir" -> onDeleteClick(review)
                        }
                        true
                    }
                    show()
                }
                true
            }
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem == newItem
    }
}