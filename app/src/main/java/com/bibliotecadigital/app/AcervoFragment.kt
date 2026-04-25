package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentAcervoBinding

class AcervoFragment : Fragment() {

    private var _binding: FragmentAcervoBinding? = null
    private val binding get() = _binding!!

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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val books = listOf(
            Book("1", "O Senhor dos Anéis", "J.R.R. Tolkien", "Fantasia", true, R.drawable.bg_cover_placeholder),
            Book("2", "1984", "George Orwell", "Ficção Científica", true, R.drawable.bg_cover_placeholder),
            Book("3", "Dom Casmurro", "Machado de Assis", "Clássico", false, R.drawable.bg_cover_placeholder),
            Book("4", "O Pequeno Príncipe", "Antoine de Saint-Exupéry", "Infantil", true, R.drawable.bg_cover_placeholder),
            Book("5", "Harry Potter", "J.K. Rowling", "Fantasia", false, R.drawable.bg_cover_placeholder),
            Book("6", "A Menina que Roubava Livros", "Markus Zusak", "Drama", true, R.drawable.bg_cover_placeholder)
        )

        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = BookAdapter(books) { category ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CategoryFragment.newInstance(category))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}