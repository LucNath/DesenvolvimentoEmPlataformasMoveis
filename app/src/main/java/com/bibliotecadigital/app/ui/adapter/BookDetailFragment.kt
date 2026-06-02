package com.bibliotecadigital.app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.bibliotecadigital.app.ui.LoanSuccessFragment
import com.bibliotecadigital.app.R
import com.bibliotecadigital.app.databinding.FragmentBookDetailBinding
import com.bibliotecadigital.app.entity.Book
import com.bibliotecadigital.app.viewmodels.BookDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.bibliotecadigital.app.repository.ReservationRepository
import com.bibliotecadigital.app.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookDetailFragment : Fragment() {

    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookDetailViewModel

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

        viewModel.isBorrowedByUser.observe(viewLifecycleOwner) { isBorrowed ->
            updateActionButtons(isBorrowed)
        }

        viewModel.borrowResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { dueDate ->
                val title = viewModel.book.value?.title ?: ""
                navigateToLoanSuccess(title, dueDate)
            }.onFailure {
                Snackbar.make(binding.root, it.message ?: "Erro ao realizar empréstimo", Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.reserveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Snackbar.make(binding.root, "Reserva realizada com sucesso!", Snackbar.LENGTH_LONG).show()
            }.onFailure {
                Snackbar.make(binding.root, it.message ?: "Erro ao reservar", Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.returnResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Snackbar.make(binding.root, "Livro devolvido com sucesso!", Snackbar.LENGTH_LONG).show()
            }.onFailure {
                Snackbar.make(binding.root, it.message ?: "Erro ao devolver livro", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun updateActionButtons(isBorrowed: Boolean) {
        if (isBorrowed) {
            binding.btnLoan.visibility = View.VISIBLE
            binding.btnLoan.text = "Devolver"
            binding.btnLoan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_unavailable))
            binding.btnLoan.setOnClickListener {
                confirmReturnBook()
            }
            binding.btnReserve.visibility = View.GONE
        } else {
            // Reset to normal state based on book status
            viewModel.book.value?.let { setupUserButtons(it) }
        }
    }

    private fun confirmReturnBook() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Devolver Livro")
            .setMessage("Deseja confirmar a devolução deste exemplar?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Devolver") { _, _ ->
                viewModel.returnBook(bookId)
            }
            .show()
    }

    private fun displayBookDetails(book: Book) {
        with(binding) {
            tvTitle.text = book.title
            tvAuthor.text = book.author
            ivCover.load(book.coverUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_cover_placeholder)
                error(R.drawable.bg_cover_placeholder)
            }
            tvPublisher.text = book.publisher
            tvYear.text = book.year
            tvIsbn.text = book.isbn
            tvLoanPeriod.text = "15 dias"
            tvSynopsis.text = book.synopsis

            val appPrefs = com.bibliotecadigital.app.AppPrefs(requireContext())
            val isAdmin = appPrefs.userRole == "admin"

            if (isAdmin) {
                layoutAdminActions.visibility = View.VISIBLE
                btnLoan.visibility = View.GONE
                btnReserve.visibility = View.GONE
                
                btnDeleteBook.setOnClickListener {
                    confirmDeleteBook(book)
                }
                
                btnEditBook.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, com.bibliotecadigital.app.ui.admin.AddBookFragment.newInstance(book.id))
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                layoutAdminActions.visibility = View.GONE
                // Status and Availability para usuários
                setupUserButtons(book)
            }
            
            // Hide reviews section
            tvRatingAvg.visibility = View.GONE
            ratingBar.visibility = View.GONE
            tvTotalReviews.visibility = View.GONE
            rvReviews.visibility = View.GONE
            btnAddReview.visibility = View.GONE
        }
    }

    private fun setupUserButtons(book: Book) {
        if (viewModel.isBorrowedByUser.value == true) return // Devolver já está visível via updateActionButtons

        with(binding) {
            when (book.status) {
                "available" -> {
                    tvStatusLabel.text = "DISPONÍVEL"
                    tvStatusLabel.setBackgroundResource(R.drawable.bg_status_green)
                    tvStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_text))
                    tvAvailability.text = "${book.available} exemplares disponíveis"
                    btnLoan.text = "Emprestar"
                    btnLoan.visibility = View.VISIBLE
                    btnReserve.visibility = View.GONE
                }
                "borrowed", "reserved", "unavailable" -> {
                    val isBorrowed = book.status == "borrowed" || book.status == "unavailable"
                    val statusText = if (isBorrowed) "EMPRESTADO" else "RESERVADO"
                    val bgRes = if (isBorrowed) R.drawable.bg_status_red else R.drawable.bg_status_yellow
                    val colorRes = if (isBorrowed) R.color.text_red else R.color.star_yellow

                    tvStatusLabel.text = statusText
                    tvStatusLabel.setBackgroundResource(bgRes)
                    tvStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
                    tvAvailability.text = "Indisponível no momento"

                    btnLoan.visibility = View.GONE
                    btnReserve.visibility = View.VISIBLE
                }
                else -> {
                    tvStatusLabel.text = book.status.uppercase()
                    tvStatusLabel.setBackgroundResource(R.drawable.bg_status_yellow)
                    tvStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_royal))
                    tvAvailability.text = "${book.available} exemplares"
                }
            }

            btnLoan.setOnClickListener {
                viewModel.borrowBook(book)
            }

            btnReserve.setOnClickListener {
                viewModel.reserveBook(book)
            }
        }
    }

    private fun confirmDeleteBook(book: Book) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Excluir Obra")
            .setMessage("Tem certeza que deseja excluir '${book.title}'? Esta ação não pode ser desfeita.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Excluir") { _, _ ->
                deleteBook(book.id)
            }
            .show()
    }

    private fun deleteBook(bookId: String) {
        FirebaseFirestore.getInstance().collection("books").document(bookId)
            .delete()
            .addOnSuccessListener {
                Snackbar.make(binding.root, "Obra excluída com sucesso", Snackbar.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Snackbar.make(binding.root, "Erro ao excluir: ${it.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun navigateToLoanSuccess(title: String, dueDate: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoanSuccessFragment.newInstance(title, dueDate))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
