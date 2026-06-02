package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.BookAdapter
import com.bibliotecadigital.app.ui.adapter.BookDetailFragment
import com.bibliotecadigital.app.MostBorrowedAdapter
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.FragmentAcervoBinding
import com.bibliotecadigital.app.viewmodels.AcervoViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AcervoFragment : Fragment() {

    private var _binding: FragmentAcervoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AcervoViewModel by viewModels()

    private lateinit var bookAdapter: BookAdapter
    private lateinit var mostBorrowedAdapter: MostBorrowedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAcervoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearch()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSeedDatabase.setOnClickListener {
            viewModel.seedDatabase()
        }
    }

    private fun setupRecyclerViews() {
        bookAdapter = BookAdapter(
            onBookClick = { book ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        BookDetailFragment.Companion.newInstance(book.id, book.title, book.author)
                    )
                    .addToBackStack(null)
                    .commit()
            },
            onReserveClick = { book ->
                viewModel.handleBookAction(book)
            }
        )
        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = bookAdapter

        mostBorrowedAdapter = MostBorrowedAdapter { book ->
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    BookDetailFragment.Companion.newInstance(book.id, book.title, book.author)
                )
                .addToBackStack(null)
                .commit()
        }
        binding.rvMostBorrowed.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvMostBorrowed.adapter = mostBorrowedAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actionMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredBooks
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { books ->
                    bookAdapter.submitList(books)
                    updateVisibility(books.isEmpty())
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mostBorrowedBooks
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { books ->
                    mostBorrowedAdapter.submitList(books)
                    val visibility = if (books.isEmpty()) View.GONE else View.VISIBLE
                    binding.tvMostBorrowedHeader.visibility = visibility
                    binding.rvMostBorrowed.visibility = visibility
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { categories ->
                    setupCategoryChips(categories)
                }
        }
    }

    private fun updateVisibility(isEmpty: Boolean) {
        if (isEmpty) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvBooks.visibility = View.GONE
            binding.tvAllBooksHeader.visibility = View.GONE
            binding.tvMostBorrowedHeader.visibility = View.GONE
            binding.rvMostBorrowed.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvBooks.visibility = View.VISIBLE
            binding.tvAllBooksHeader.visibility = View.VISIBLE
        }
    }

    private fun setupCategoryChips(categories: List<String>) {
        binding.chipGroupCategories.removeAllViews()

        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = category
                isCheckable = true
                isChecked = (category == viewModel.selectedCategory.value)

                if (isChecked) {
                    setChipBackgroundColorResource(R.color.blue_royal)
                    setTextColor(resources.getColor(R.color.white, null))
                } else {
                    setChipBackgroundColorResource(R.color.blue_ice)
                    setTextColor(resources.getColor(R.color.blue_royal, null))
                }

                setChipStrokeColorResource(R.color.blue_royal)
                setChipStrokeWidth(1f)

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.setSelectedCategory(category)
                    }
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}