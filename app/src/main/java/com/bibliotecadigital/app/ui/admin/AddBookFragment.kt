package com.bibliotecadigital.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bibliotecadigital.app.databinding.FragmentAddBookBinding
import com.bibliotecadigital.app.viewmodels.AddBookViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AddBookFragment : Fragment() {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddBookViewModel by viewModels()
    private var bookId: String? = null

    companion object {
        fun newInstance(bookId: String? = null): Fragment = AddBookFragment().apply {
            arguments = Bundle().apply {
                putString("bookId", bookId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookId = arguments?.getString("bookId")
        
        setupUI()
        observeViewModel()
        
        if (bookId != null) {
            binding.toolbar.title = "Editar Obra"
            loadBookData(bookId!!)
        }
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            saveBook()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    binding.btnSave.isEnabled = !isLoading
                    // Se você tiver um progress bar no layout, controle aqui
                    // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveSuccess.collect { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadBookData(id: String) {
        // Para carregar dados, ainda podemos usar o Firestore direto ou mover para o VM
        // Como é apenas leitura para preencher campos, manter aqui é aceitável, mas VM é melhor.
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("books").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && _binding != null) {
                    binding.etTitle.setText(doc.getString("title"))
                    binding.etAuthor.setText(doc.getString("author"))
                    binding.etCategory.setText(doc.getString("category"))
                    binding.etIsbn.setText(doc.getString("isbn"))
                    binding.etQuantity.setText(doc.getLong("quantity")?.toString())
                    binding.etYear.setText(doc.getLong("year")?.toString())
                    binding.etCoverUrl.setText(doc.getString("coverUrl"))
                }
            }
    }

    private fun saveBook() {
        val title = binding.etTitle.text.toString().trim()
        val author = binding.etAuthor.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val isbn = binding.etIsbn.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val yearStr = binding.etYear.text.toString().trim()
        val coverUrl = binding.etCoverUrl.text.toString().trim()

        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.saveBook(
            bookId = bookId,
            title = title,
            author = author,
            category = category,
            isbn = isbn,
            quantity = quantityStr.toIntOrNull() ?: 0,
            year = yearStr.toIntOrNull() ?: 0,
            coverUrl = coverUrl
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
