package com.bibliotecadigital.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.ActivityAdminBooksBinding
import com.bibliotecadigital.app.databinding.DialogAdminBookBinding

class AdminBooksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBooksBinding
    private lateinit var adapter: AdminBookAdapter
    private var allBooks = mutableListOf<Book>()
    private var filteredBooks = mutableListOf<Book>()

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Toast.makeText(this, "Imagem selecionada: $it", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMockData()
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
    }

    private fun setupMockData() {
        allBooks = mutableListOf(
            Book("1", "O Senhor dos Anéis", "J.R.R. Tolkien", "Fantasia", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9780007525546", "HarperCollins", "1954", 5),
            Book("2", "1984", "George Orwell", "Ficção Científica", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9780451524935", "Signet Classic", "1949", 3),
            Book("3", "Dom Casmurro", "Machado de Assis", "Clássico", BookStatus.BORROWED, R.drawable.bg_cover_placeholder, "9788520921029", "Editora Nova Fronteira", "1899", 2),
            Book("4", "O Pequeno Príncipe", "Antoine de Saint-Exupéry", "Infantil", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder, "9780156012195", "Harcourt", "1943", 10),
            Book("5", "Harry Potter", "J.K. Rowling", "Fantasia", BookStatus.BORROWED, R.drawable.bg_cover_placeholder, "9780439708180", "Scholastic", "1997", 4)
        )
        filteredBooks.addAll(allBooks)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AdminBookAdapter(
            onEditClick = { book -> showBookDialog(book) },
            onDeleteClick = { book -> showDeleteConfirmation(book) }
        )
        binding.rvAdminBooks.layoutManager = LinearLayoutManager(this)
        binding.rvAdminBooks.adapter = adapter
        adapter.submitList(filteredBooks.toList())
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(query: String) {
        val lowerCaseQuery = query.lowercase()
        filteredBooks = allBooks.filter {
            it.title.lowercase().contains(lowerCaseQuery) || it.isbn.contains(lowerCaseQuery)
        }.toMutableList()
        adapter.submitList(filteredBooks.toList())
    }

    private fun setupFab() {
        binding.fabAddBook.setOnClickListener { showBookDialog(null) }
    }

    private fun showBookDialog(book: Book?) {
        val dialogBinding = DialogAdminBookBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        if (book != null) {
            dialogBinding.tvDialogTitle.text = "Editar Obra"
            dialogBinding.etTitle.setText(book.title)
            dialogBinding.etAuthor.setText(book.author)
            dialogBinding.etIsbn.setText(book.isbn)
            dialogBinding.etPublisher.setText(book.publisher)
            dialogBinding.etYear.setText(book.year)
            dialogBinding.etQuantity.setText(book.quantity.toString())
            dialogBinding.etCategory.setText(book.category)
        }

        dialogBinding.btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSave.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString()
            val author = dialogBinding.etAuthor.text.toString()
            val isbn = dialogBinding.etIsbn.text.toString()

            if (title.isBlank() || author.isBlank() || isbn.isBlank()) {
                Toast.makeText(this, "Preencha título, autor e ISBN", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedBook = Book(
                id = book?.id ?: System.currentTimeMillis().toString(),
                title = title,
                author = author,
                isbn = isbn,
                publisher = dialogBinding.etPublisher.text.toString(),
                year = dialogBinding.etYear.text.toString(),
                quantity = dialogBinding.etQuantity.text.toString().toIntOrNull() ?: 1,
                category = dialogBinding.etCategory.text.toString(),
                status = book?.status ?: BookStatus.AVAILABLE,
                coverRes = book?.coverRes ?: R.drawable.bg_cover_placeholder
            )

            if (book == null) {
                allBooks.add(0, updatedBook)
                Toast.makeText(this, "Obra cadastrada!", Toast.LENGTH_SHORT).show()
            } else {
                val index = allBooks.indexOfFirst { it.id == book.id }
                if (index != -1) {
                    allBooks[index] = updatedBook
                }
                Toast.makeText(this, "Obra atualizada!", Toast.LENGTH_SHORT).show()
            }

            filter(binding.etSearch.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(book: Book) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Obra")
            .setMessage("Deseja realmente excluir '${book.title}'?")
            .setPositiveButton("Excluir") { _, _ ->
                allBooks.remove(book)
                filter(binding.etSearch.text.toString())
                Toast.makeText(this, "Obra removida", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}