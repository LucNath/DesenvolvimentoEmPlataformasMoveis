package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.FragmentNotificationsBinding

class NotificacoesFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NotificationAdapter
    private var notifications = mutableListOf<Notification>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        loadMockNotifications()

        binding.tvMarkAllRead.setOnClickListener {
            markAllAsRead()
        }
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter(notifications) { notification ->
            markAsRead(notification)
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter
    }

    private fun loadMockNotifications() {
        notifications = mutableListOf(
            Notification(
                id = "1",
                message = "O livro 'Código Limpo' deve ser devolvido em 2 dias.",
                type = NotificationType.LOAN_REMINDER,
                timestamp = "Há 2 horas",
                isRead = false
            ),
            Notification(
                id = "2",
                message = "Sua reserva para 'Algoritmos' está disponível para retirada!",
                type = NotificationType.RESERVATION_READY,
                timestamp = "Há 5 horas",
                isRead = false
            ),
            Notification(
                id = "3",
                message = "A biblioteca fechará mais cedo nesta sexta-feira.",
                type = NotificationType.SYSTEM_ALERT,
                timestamp = "Ontem",
                isRead = true
            )
        )
        updateUI()
    }

    private fun markAsRead(notification: Notification) {
        val index = notifications.indexOf(notification)
        if (index != -1 && !notification.isRead) {
            notifications[index].isRead = true
            adapter.notifyItemChanged(index)
            Toast.makeText(requireContext(), "Notificação lida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun markAllAsRead() {
        var count = 0
        notifications.forEachIndexed { index, notification ->
            if (!notification.isRead) {
                notification.isRead = true
                adapter.notifyItemChanged(index)
                count++
            }
        }
        if (count > 0) {
            Toast.makeText(requireContext(), "Todas as notificações marcadas como lidas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        if (notifications.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvNotifications.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvNotifications.visibility = View.VISIBLE
            adapter.updateData(notifications)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}