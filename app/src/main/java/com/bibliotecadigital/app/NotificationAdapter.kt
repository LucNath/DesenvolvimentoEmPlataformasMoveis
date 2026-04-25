package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemNotificationBinding

class NotificationAdapter(
    private var items: List<Notification>,
    private val onItemClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvMessage.text = item.message
            tvTimestamp.text = item.timestamp
            
            // Destaque de não lida
            viewUnreadIndicator.visibility = if (item.isRead) View.GONE else View.VISIBLE
            rootLayout.alpha = if (item.isRead) 0.7f else 1.0f

            // Ícone por tipo
            val iconRes = when (item.type) {
                NotificationType.LOAN_REMINDER -> android.R.drawable.ic_dialog_alert
                NotificationType.RESERVATION_READY -> android.R.drawable.ic_input_add
                NotificationType.SYSTEM_ALERT -> android.R.drawable.ic_dialog_info
            }
            ivIcon.setImageResource(iconRes)

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Notification>) {
        items = newItems
        notifyDataSetChanged()
    }
}