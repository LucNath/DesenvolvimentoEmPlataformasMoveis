package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.BookAdapter
import com.bibliotecadigital.app.databinding.FragmentCategoryBinding
import com.bibliotecadigital.app.entity.Book
import com.bibliotecadigital.app.viewmodels.CategoryViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var category: String
    private lateinit var bookAdapter: BookAdapter
    private val viewModel: CategoryViewModel by viewModels()

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String) = CategoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY, category)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString(ARG_CATEGORY) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        viewModel.loadBooks(category)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.filteredBooks.collect { books ->
                        bookAdapter.submitList(books)
                        updateEmptyState(books.isEmpty())
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.actionMessage.collect { message ->
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.toolbar.title = category
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        val appPrefs = com.bibliotecadigital.app.AppPrefs(requireContext())
        val isAdmin = appPrefs.userRole == "admin"

        // Exibir FAB apenas para Admin
        binding.fabAddBook.visibility = if (isAdmin) View.VISIBLE else View.GONE
        binding.fabAddBook.setOnClickListener {
            val fragment = com.bibliotecadigital.app.ui.admin.AddBookFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(com.bibliotecadigital.app.R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        bookAdapter = BookAdapter(
            isAdmin = isAdmin,
            onBookClick = { book ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        com.bibliotecadigital.app.R.id.fragmentContainer,
                        com.bibliotecadigital.app.ui.adapter.BookDetailFragment.newInstance(book.id, book.title, book.author)
                    )
                    .addToBackStack(null)
                    .commit()
            },
            onReserveClick = { book ->
                viewModel.handleBookAction(book)
            },
            onEditClick = { book ->
                // Ação para Administrador: Editar obra
                val fragment = com.bibliotecadigital.app.ui.admin.AddBookFragment.newInstance(book.id)
                parentFragmentManager.beginTransaction()
                    .replace(com.bibliotecadigital.app.R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { book ->
                // Ação para Administrador: Excluir obra
                showDeleteConfirmation(book)
            }
        )
        binding.rvCategoryBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategoryBooks.adapter = bookAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun showDeleteConfirmation(book: Book) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Excluir Obra")
            .setMessage("Tem certeza que deseja excluir '${book.title}'? Esta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.deleteBook(book.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}