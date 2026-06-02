package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemBookBinding
import com.bibliotecadigital.app.entity.Book

class BookAdapter(
    private val isAdmin: Boolean = false,
    private val onBookClick: (Book) -> Unit,
    private val onReserveClick: (Book) -> Unit,
    private val onEditClick: ((Book) -> Unit)? = null,
    private val onDeleteClick: ((Book) -> Unit)? = null
) : ListAdapter<Book, BookAdapter.ViewHolder>(BookDiffCallback()) {

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
            ivCover.load(book.coverUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_cover_placeholder)
                error(R.drawable.bg_cover_placeholder)
            }

            // Configuração de Status
            if (book.isBorrowedByUser) {
                tvStatus.text = "EMPRESTADO"
                tvStatus.setBackgroundResource(R.drawable.bg_status_yellow)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.status_warning_text))
                btnReservar.text = "Emprestado"
                btnReservar.isEnabled = false
                btnReservar.setBackgroundColor(ContextCompat.getColor(root.context, R.color.green_text))
                btnReservar.setTextColor(ContextCompat.getColor(root.context, R.color.white))
            } else {
                btnReservar.isEnabled = true
                when (book.status) {
                    "available" -> {
                        tvStatus.text = "DISPONÍVEL"
                        tvStatus.setBackgroundResource(R.drawable.bg_status_green)
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.green_text))
                        btnReservar.text = "Empréstimo"
                        btnReservar.setBackgroundColor(ContextCompat.getColor(root.context, R.color.blue_royal))
                    }
                    else -> {
                        tvStatus.text = "INDISPONÍVEL"
                        tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                        tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.text_red))
                        btnReservar.text = "Reservar"
                        btnReservar.setBackgroundColor(ContextCompat.getColor(root.context, R.color.blue_royal))
                    }
                }
            }

            // Controle de visibilidade Admin vs Usuário
            if (isAdmin) {
                btnEdit.visibility = android.view.View.VISIBLE
                btnDelete.visibility = android.view.View.VISIBLE
                btnReservar.visibility = android.view.View.GONE
            } else {
                btnEdit.visibility = android.view.View.GONE
                btnDelete.visibility = android.view.View.GONE
                btnReservar.visibility = android.view.View.VISIBLE
            }

            btnReservar.setOnClickListener {
                onReserveClick(book)
            }

            btnEdit.setOnClickListener {
                onEditClick?.invoke(book)
            }

            btnDelete.setOnClickListener {
                onDeleteClick?.invoke(book)
            }

            root.setOnClickListener {
                onBookClick(book)
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean = oldItem == newItem
    }
}