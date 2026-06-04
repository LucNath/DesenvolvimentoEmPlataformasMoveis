package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.databinding.FragmentSimpleListBinding

class ReadingHistoryFragment : Fragment() {

    private var _binding: FragmentSimpleListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimpleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.tvToolbarTitle.text = "Histórico de Leituras"
        binding.toolbar.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.tvEmpty.text = "Seu histórico está vazio"
        binding.tvEmpty.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}