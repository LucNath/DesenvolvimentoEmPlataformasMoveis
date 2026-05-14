package com.bibliotecadigital.app

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
import com.bibliotecadigital.app.databinding.FragmentAcervoBinding
import com.google.android.material.chip.Chip
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
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        bookAdapter = BookAdapter { book ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BookDetailFragment.newInstance(book.id, book.title, book.author))
                .addToBackStack(null)
                .commit()
        }
        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = bookAdapter

        mostBorrowedAdapter = MostBorrowedAdapter { book ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BookDetailFragment.newInstance(book.id, book.title, book.author))
                .addToBackStack(null)
                .commit()
        }
        binding.rvMostBorrowed.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvMostBorrowed.adapter = mostBorrowedAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }
    }

    private fun observeViewModel() {
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
