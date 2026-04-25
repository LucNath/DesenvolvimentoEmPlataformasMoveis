package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemBookHorizontalBinding

class MostBorrowedAdapter(private val onBookClick: ((Book) -> Unit)? = null) :
    ListAdapter<Book, MostBorrowedAdapter.ViewHolder>(BookDiffCallback()) {

    class ViewHolder(val binding: ItemBookHorizontalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getItem(position)
        with(holder.binding) {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            ivCover.setImageResource(book.coverRes)

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