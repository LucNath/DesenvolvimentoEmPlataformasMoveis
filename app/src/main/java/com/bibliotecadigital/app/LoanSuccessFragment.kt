package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.databinding.FragmentLoanSuccessBinding

class LoanSuccessFragment : Fragment() {

    private var _binding: FragmentLoanSuccessBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_BOOK_TITLE = "book_title"
        private const val ARG_DUE_DATE = "due_date"

        fun newInstance(bookTitle: String, dueDate: String) = LoanSuccessFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_BOOK_TITLE, bookTitle)
                putString(ARG_DUE_DATE, dueDate)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookTitle = arguments?.getString(ARG_BOOK_TITLE) ?: ""
        val dueDate = arguments?.getString(ARG_DUE_DATE) ?: ""

        binding.tvBookTitle.text = bookTitle
        binding.tvDueDate.text = dueDate

        binding.btnViewDetails.setOnClickListener {
            // Navegar para "Meus Empréstimos" (Geralmente na Home ou Perfil)
            // Para seguir o padrão do projeto, voltamos para a Home onde estão os empréstimos ativos
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }

        binding.btnBackToCatalog.setOnClickListener {
            // Volta para o Acervo
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AcervoFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}