package com.bibliotecadigital.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.databinding.FragmentAddBookBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class AddBookFragment : Fragment() {

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private var bookId: String? = null

    companion object {
        fun newInstance(bookId: String? = null) = AddBookFragment().apply {
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
        
        if (bookId != null) {
            binding.toolbar.title = "Editar Obra"
            loadBookData(bookId!!)
        }

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSave.setOnClickListener {
            saveBook()
        }
    }

    private fun loadBookData(id: String) {
        db.collection("books").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
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

        val quantity = quantityStr.toIntOrNull() ?: 0
        val year = yearStr.toIntOrNull() ?: 0

        val book = hashMapOf(
            "title" to title,
            "author" to author,
            "category" to category,
            "isbn" to isbn,
            "quantity" to quantity,
            "availableQuantity" to quantity,
            "year" to year,
            "coverUrl" to coverUrl,
            "searchKeywords" to listOf(title.lowercase(), author.lowercase(), category.lowercase())
        )

        binding.btnSave.isEnabled = false
        
        val task = if (bookId == null) {
            db.collection("books").add(book)
        } else {
            db.collection("books").document(bookId!!).set(book)
        }

        task.addOnSuccessListener {
                val msg = if (bookId == null) "Obra cadastrada!" else "Obra atualizada!"
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                binding.btnSave.isEnabled = true
                Snackbar.make(binding.root, "Erro: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
