package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.BookAdapter
import com.bibliotecadigital.app.databinding.FragmentCategoryBinding
import com.bibliotecadigital.app.entity.Book

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
        binding.toolbar.title = category
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        val allBooks = listOf(
            Book(
                "1",
                "O Senhor dos Anéis",
                "J.R.R. Tolkien",
                "Fantasia",
                "",
                5,
                5,
                "available",
                "9780007525546",
                "",
                "HarperCollins",
                "1954"
            ),
            Book(
                "2",
                "1984",
                "George Orwell",
                "Ficção Científica",
                "",
                3,
                3,
                "available",
                "9780451524935",
                "",
                "Signet Classic",
                "1949"
            ),
            Book(
                "3",
                "Dom Casmurro",
                "Machado de Assis",
                "Clássico",
                "",
                2,
                2,
                "borrowed",
                "9788520921029",
                "",
                "Editora Nova Fronteira",
                "1899"
            ),
            Book(
                "4",
                "O Pequeno Príncipe",
                "Antoine de Saint-Exupéry",
                "Infantil",
                "",
                10,
                10,
                "available",
                "9780156012195",
                "",
                "Harcourt",
                "1943"
            ),
            Book(
                "5",
                "Harry Potter",
                "J.K. Rowling",
                "Fantasia",
                "",
                4,
                4,
                "reserved",
                "9780439708180",
                "",
                "Scholastic",
                "1997"
            ),
            Book(
                "6",
                "A Menina que Roubava Livros",
                "Markus Zusak",
                "Drama",
                "",
                1,
                1,
                "available",
                "9788539003839",
                "",
                "Intrínseca",
                "2005"
            ),
            Book(
                "7",
                "O Hobbit",
                "J.R.R. Tolkien",
                "Fantasia",
                "",
                2,
                2,
                "available",
                "9788578277109",
                "",
                "Martins Fontes",
                "1937"
            ),
            Book(
                "8",
                "Fundação",
                "Isaac Asimov",
                "Ficção Científica",
                "",
                3,
                3,
                "available",
                "9788576571551",
                "",
                "Editora Aleph",
                "1951"
            ),
            Book(
                "9",
                "Memórias Póstumas",
                "Machado de Assis",
                "Clássico",
                "",
                4,
                4,
                "available",
                "9788525406033",
                "",
                "L&PM",
                "1881"
            ),
            Book(
                "10",
                "Alice no País das Maravilhas",
                "Lewis Carroll",
                "Infantil",
                "",
                5,
                5,
                "available",
                "9788573266382",
                "",
                "Editora 34",
                "1865"
            )
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