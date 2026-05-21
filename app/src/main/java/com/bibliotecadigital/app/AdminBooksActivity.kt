package com.bibliotecadigital.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.ActivityAdminBooksBinding
import com.bibliotecadigital.app.databinding.DialogAdminBookBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import android.net.Uri
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminBooksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBooksBinding
    private lateinit var adapter: AdminBookAdapter
    private var allBooks = mutableListOf<Book>()
    private var filteredBooks = mutableListOf<Book>()
    private lateinit var bookRepository: BookRepository
    private lateinit var storageRepository: StorageRepository
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookRepository = BookRepository(FirebaseFirestore.getInstance())
        storageRepository = StorageRepository(FirebaseStorage.getInstance(), applicationContext)
        
        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeBooks()
    }

    private fun observeBooks() {
        lifecycleScope.launch {
            bookRepository.getBooks().collectLatest { books ->
                allBooks = books.toMutableList()
                filter(binding.etSearch.text.toString())
            }
        }
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
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        if (book != null) {
            dialogBinding.tvDialogTitle.text = "Editar Obra"
            dialogBinding.etTitle.setText(book.title)
            dialogBinding.etAuthor.setText(book.author)
            dialogBinding.etIsbn.setText(book.isbn)
            dialogBinding.etPublisher.setText(book.publisher)
            dialogBinding.etYear.setText(book.year)
            dialogBinding.etQuantity.setText(book.available.toString())
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

            val quantity = dialogBinding.etQuantity.text.toString().toIntOrNull() ?: 1
            val updatedBook = Book(
                id = book?.id ?: "",
                title = title,
                author = author,
                isbn = isbn,
                publisher = dialogBinding.etPublisher.text.toString(),
                year = dialogBinding.etYear.text.toString(),
                available = quantity,
                total = quantity,
                category = dialogBinding.etCategory.text.toString(),
                status = book?.status ?: "available",
                coverUrl = book?.coverUrl ?: ""
            )

            lifecycleScope.launch {
                try {
                    val finalBook = if (selectedImageUri != null) {
                        val tempId = book?.id ?: FirebaseFirestore.getInstance().collection("books").document().id
                        val imageUrl = storageRepository.uploadCover(tempId, selectedImageUri!!).getOrThrow()
                        updatedBook.copy(id = tempId, coverUrl = imageUrl)
                    } else {
                        updatedBook
                    }

                    val result = if (book == null && selectedImageUri == null) {
                        bookRepository.addBook(finalBook)
                    } else if (book == null) {
                        bookRepository.addBook(finalBook)
                    } else {
                        bookRepository.updateBook(finalBook)
                    }

                    result.onSuccess {
                        Toast.makeText(this@AdminBooksActivity, 
                            if (book == null) "Obra cadastrada!" else "Obra atualizada!", 
                            Toast.LENGTH_SHORT).show()
                        selectedImageUri = null
                        dialog.dismiss()
                    }.onFailure {
                        Toast.makeText(this@AdminBooksActivity, "Erro ao salvar: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AdminBooksActivity, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(book: Book) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Excluir Obra")
            .setMessage("Deseja realmente excluir '${book.title}'?")
            .setPositiveButton("Excluir") { _, _ ->
                lifecycleScope.launch {
                    bookRepository.deleteBook(book.id).onSuccess {
                        storageRepository.deleteCover(book.id)
                        Toast.makeText(this@AdminBooksActivity, "Obra removida", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(this@AdminBooksActivity, "Erro ao remover: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
