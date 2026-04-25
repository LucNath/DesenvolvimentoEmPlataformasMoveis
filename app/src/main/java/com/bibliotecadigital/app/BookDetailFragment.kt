package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentBookDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!
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
        
        bookId = arguments?.getString("bookId") ?: ""
        binding.tvTitle.text = arguments?.getString("title")
        binding.tvAuthor.text = arguments?.getString("author")

        setupReviews()
        setupSubmitAction()
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
        binding.btnSubmitReview.setOnClickListener {
            val rating = binding.rbInput.rating
            val comment = binding.etComment.text?.toString() ?: ""

            if (rating == 0f) {
                Snackbar.make(binding.root, "Selecione uma nota", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (comment.isBlank()) {
                Snackbar.make(binding.root, "Escreva um comentário", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val newReview = Review(
                id = System.currentTimeMillis().toString(),
                userName = "Você",
                bookId = bookId,
                rating = rating,
                comment = comment,
                date = sdf.format(Date())
            )

            reviewsList.add(0, newReview)
            reviewAdapter.submitList(reviewsList.toList())
            updateRatingSummary()
            
            binding.rbInput.rating = 0f
            binding.etComment.text?.clear()
            Snackbar.make(binding.root, "Avaliação enviada!", Snackbar.LENGTH_SHORT).show()
        }
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
        binding.rbInput.rating = review.rating
        binding.etComment.setText(review.comment)
        Snackbar.make(binding.root, "Edite sua avaliação no formulário", Snackbar.LENGTH_SHORT).show()
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