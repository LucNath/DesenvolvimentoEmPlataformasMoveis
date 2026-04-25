package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentAcervoBinding
import com.google.android.material.chip.Chip

class AcervoFragment : Fragment() {

    private var _binding: FragmentAcervoBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: AcervoViewModel
    
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
        viewModel = ViewModelProvider(this)[AcervoViewModel::class.java]
        
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
        viewModel.filteredBooks.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)
        }

        viewModel.mostBorrowedBooks.observe(viewLifecycleOwner) { books ->
            mostBorrowedAdapter.submitList(books)
            val visibility = if (books.isEmpty()) View.GONE else View.VISIBLE
            binding.tvMostBorrowedHeader.visibility = visibility
            binding.rvMostBorrowed.visibility = visibility
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            setupCategoryChips(categories)
        }
    }

    private fun setupCategoryChips(categories: List<String>) {
        val currentCheckedId = binding.chipGroupCategories.checkedChipId
        binding.chipGroupCategories.removeAllViews()
        
        categories.forEachIndexed { index, category ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = category
                isCheckable = true
                isChecked = (category == viewModel.selectedCategory.value)
                
                setChipBackgroundColorResource(R.color.bg_card_inner)
                setTextColor(resources.getColor(R.color.text_white, null))
                
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