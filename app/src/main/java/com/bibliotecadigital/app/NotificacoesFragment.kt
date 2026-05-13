package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificacoesFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter

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
        observeViewModel()

        binding.tvMarkAllRead.setOnClickListener {
            viewModel.markAllAsRead()
        }
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter { notification ->
            viewModel.markAsRead(notification.id)
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val notification = adapter.currentList[position]
                viewModel.deleteNotification(notification.id)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvNotifications)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notifications
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { notifications: List<Notification> ->
                    adapter.submitList(notifications)
                    updateVisibility(notifications)
                }
        }
    }

    private fun updateVisibility(notifications: List<Notification>) {
        if (notifications.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvNotifications.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvNotifications.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
