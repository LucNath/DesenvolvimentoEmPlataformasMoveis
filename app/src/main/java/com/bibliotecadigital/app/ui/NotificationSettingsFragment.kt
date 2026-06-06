package com.bibliotecadigital.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bibliotecadigital.app.databinding.FragmentNotificationSettingsBinding

class NotificationSettingsFragment : Fragment() {

    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.tvToolbarTitle.text = "Configurar Notificações"
        binding.toolbar.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupListeners() {
        // Futuramente integrar com SharedPreferences ou Firebase User Settings
        binding.switchLoans.setOnCheckedChangeListener { _, isChecked -> 
            // Salvar preferência de notificação de empréstimo
        }
        
        binding.switchReservations.setOnCheckedChangeListener { _, isChecked -> 
            // Salvar preferência de notificação de reserva
        }
        
        binding.switchEvents.setOnCheckedChangeListener { _, isChecked -> 
            // Salvar preferência de notificação de eventos
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}