package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemAdminBookBinding

class AdminBookAdapter(
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : ListAdapter<Book, AdminBookAdapter.ViewHolder>(BookDiffCallback()) {

    class ViewHolder(val binding: ItemAdminBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getItem(position)
        with(holder.binding) {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            tvIsbn.text = "ISBN: ${book.isbn}"
            ivBookCover.setImageResource(if (book.coverRes != 0) book.coverRes else R.drawable.bg_cover_placeholder)

            btnEdit.setOnClickListener { onEditClick(book) }
            btnDelete.setOnClickListener { onDeleteClick(book) }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem == newItem
    }
}