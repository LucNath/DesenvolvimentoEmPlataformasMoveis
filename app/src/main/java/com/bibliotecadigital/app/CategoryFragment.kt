package com.bibliotecadigital.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var category: String
    private lateinit var bookAdapter: BookAdapter
    private var categoryBooks = listOf<Book>()

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
    }

    private fun setupUI() {
        binding.tvCategoryTitle.text = category
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        val allBooks = listOf(
            Book("1", "O Senhor dos Anéis", "J.R.R. Tolkien", "Fantasia", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder),
            Book("2", "1984", "George Orwell", "Ficção Científica", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder),
            Book("3", "Dom Casmurro", "Machado de Assis", "Clássico", BookStatus.BORROWED, R.drawable.bg_cover_placeholder),
            Book("4", "O Pequeno Príncipe", "Antoine de Saint-Exupéry", "Infantil", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder),
            Book("5", "Harry Potter", "J.K. Rowling", "Fantasia", BookStatus.RESERVED, R.drawable.bg_cover_placeholder),
            Book("6", "A Menina que Roubava Livros", "Markus Zusak", "Drama", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder),
            Book("7", "O Hobbit", "J.R.R. Tolkien", "Fantasia", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder),
            Book("8", "Fundação", "Isaac Asimov", "Ficção Científica", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder),
            Book("9", "Memórias Póstumas", "Machado de Assis", "Clássico", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder),
            Book("10", "Alice no País das Maravilhas", "Lewis Carroll", "Infantil", BookStatus.AVAILABLE, R.drawable.bg_cover_placeholder)
        )

        categoryBooks = allBooks.filter { it.category == category }
        
        bookAdapter = BookAdapter { _ ->
            // Navegação removida
        }
        binding.rvCategoryBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategoryBooks.adapter = bookAdapter
        bookAdapter.submitList(categoryBooks)
        
        updateEmptyState(categoryBooks.isEmpty())
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase() ?: ""
                val filteredList = if (query.isEmpty()) {
                    categoryBooks
                } else {
                    categoryBooks.filter { 
                        it.title.lowercase().contains(query) || it.author.lowercase().contains(query)
                    }
                }
                bookAdapter.submitList(filteredList)
                updateEmptyState(filteredList.isEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}