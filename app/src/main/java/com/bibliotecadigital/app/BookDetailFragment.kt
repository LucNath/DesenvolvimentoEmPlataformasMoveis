package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentBookDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: BookDetailViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewsList = mutableListOf<Review>()
    
    private var bookId: String = ""

    companion object {
        fun newInstance(bookId: String, title: String, author: String) = BookDetailFragment().apply {
            arguments = Bundle().apply {
                putString("bookId", bookId)
                putString("title", title)
                putString("author", author)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[BookDetailViewModel::class.java]
        bookId = arguments?.getString("bookId") ?: ""
        
        setupToolbar()
        setupReviews()
        setupSubmitAction()
        observeViewModel()
        
        viewModel.loadBook(bookId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.book.observe(viewLifecycleOwner) { book ->
            displayBookDetails(book)
        }
    }

    private fun displayBookDetails(book: Book) {
        with(binding) {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            ivCover.setImageResource(book.coverRes)
            tvPublisher.text = book.publisher
            tvYear.text = book.year
            tvIsbn.text = book.isbn
            tvLoanPeriod.text = book.loanPeriod
            tvSynopsis.text = book.synopsis
            
            // Status and Availability
            when (book.status) {
                BookStatus.AVAILABLE -> {
                    tvStatusLabel.text = "DISPONÍVEL"
                    tvStatusLabel.setBackgroundResource(R.drawable.bg_status_green)
                    tvStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_text))
                    tvAvailability.text = "${book.availableQuantity} exemplares disponíveis"
                    btnLoan.visibility = View.VISIBLE
                    btnReserve.visibility = View.GONE
                }
                BookStatus.BORROWED, BookStatus.RESERVED -> {
                    val statusText = if (book.status == BookStatus.BORROWED) "EMPRESTADO" else "RESERVADO"
                    val bgRes = if (book.status == BookStatus.BORROWED) R.drawable.bg_status_red else R.drawable.bg_status_yellow
                    val colorRes = if (book.status == BookStatus.BORROWED) R.color.text_red else R.color.star_yellow
                    
                    tvStatusLabel.text = statusText
                    tvStatusLabel.setBackgroundResource(bgRes)
                    tvStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
                    tvAvailability.text = "Fila de espera: ${book.waitingListCount} pessoas"
                    
                    btnLoan.visibility = View.GONE
                    btnReserve.visibility = View.VISIBLE
                }
            }
            
            btnLoan.setOnClickListener {
                Snackbar.make(root, "Solicitação de empréstimo enviada!", Snackbar.LENGTH_LONG).show()
            }
            
            btnReserve.setOnClickListener {
                Snackbar.make(root, "Reserva realizada com sucesso!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupReviews() {
        reviewsList.clear()
        reviewsList.add(Review("1", "Maria Oliveira", bookId, 5f, "Livro fantástico!", "10/10/2023"))
        reviewsList.add(Review("2", "Pedro Santos", bookId, 4f, "Muito bom.", "12/10/2023"))

        reviewAdapter = ReviewAdapter(
            onEditClick = { review -> editReview(review) },
            onDeleteClick = { review -> deleteReview(review) }
        )
        
        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews.adapter = reviewAdapter
        reviewAdapter.submitList(reviewsList.toList())
        
        updateRatingSummary()
    }

    private fun setupSubmitAction() {
        binding.btnAddReview.setOnClickListener {
            showReviewDialog()
        }
    }

    private fun showReviewDialog() {
        val dialogBinding = com.bibliotecadigital.app.databinding.DialogReviewBinding.inflate(layoutInflater)
        val builder = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSubmit.setOnClickListener {
            val rating = dialogBinding.ratingBarInput.rating
            val comment = dialogBinding.etComment.text.toString()

            if (rating == 0f) {
                Snackbar.make(binding.root, "Por favor, selecione uma nota", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("dd/10/yyyy", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val newReview = Review(
                id = System.currentTimeMillis().toString(),
                userName = "Você", // Simulando usuário logado
                bookId = bookId,
                rating = rating,
                comment = if (comment.isBlank()) "Sem comentário" else comment,
                date = currentDate
            )

            reviewsList.add(0, newReview)
            reviewAdapter.submitList(reviewsList.toList())
            updateRatingSummary()
            
            dialog.dismiss()
            Snackbar.make(binding.root, "Avaliação enviada com sucesso!", Snackbar.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun updateRatingSummary() {
        if (reviewsList.isEmpty()) {
            binding.tvRatingAvg.text = "0.0"
            binding.ratingBar.rating = 0f
            binding.tvTotalReviews.text = "(0 avaliações)"
            return
        }

        val avg = reviewsList.map { it.rating }.average()
        binding.tvRatingAvg.text = String.format(Locale.getDefault(), "%.1f", avg)
        binding.ratingBar.rating = avg.toFloat()
        binding.tvTotalReviews.text = "(${reviewsList.size} avaliações)"
    }

    private fun editReview(review: Review) {
        Snackbar.make(binding.root, "Edição disponível em breve", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteReview(review: Review) {
        reviewsList.remove(review)
        reviewAdapter.submitList(reviewsList.toList())
        updateRatingSummary()
        Snackbar.make(binding.root, "Avaliação removida", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}