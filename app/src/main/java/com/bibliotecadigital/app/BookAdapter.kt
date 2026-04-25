package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemBookBinding

class BookAdapter(private var books: List<Book>, private val onCategoryClick: ((String) -> Unit)? = null) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        with(holder.binding) {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            tvCategory.text = book.category
            ivCover.setImageResource(book.coverRes)

            if (book.available) {
                tvStatus.text = "DISPONÍVEL"
                tvStatus.setBackgroundResource(R.drawable.bg_status_green)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.green_text))
            } else {
                tvStatus.text = "EMPRESTADO"
                tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.text_red))
            }

            tvCategory.setOnClickListener {
                onCategoryClick?.invoke(book.category)
            }
        }
    }

    override fun getItemCount() = books.size

    fun updateList(newList: List<Book>) {
        books = newList
        notifyDataSetChanged()
    }
}