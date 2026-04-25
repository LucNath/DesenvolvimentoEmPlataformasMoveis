package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemBookBinding

class BookAdapter(private val onBookClick: ((Book) -> Unit)? = null) : 
    ListAdapter<Book, BookAdapter.ViewHolder>(BookDiffCallback()) {

    class ViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getItem(position)
        with(holder.binding) {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            tvCategory.text = book.category
            ivCover.setImageResource(book.coverRes)

            when (book.status) {
                BookStatus.AVAILABLE -> {
                    tvStatus.text = "DISPONÍVEL"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_green)
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.green_text))
                }
                BookStatus.BORROWED -> {
                    tvStatus.text = "EMPRESTADO"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.text_red))
                }
                BookStatus.RESERVED -> {
                    tvStatus.text = "RESERVADO"
                    tvStatus.setBackgroundResource(R.drawable.bg_status_yellow)
                    tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.star_yellow))
                }
            }

            root.setOnClickListener {
                onBookClick?.invoke(book)
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem == newItem
    }
}