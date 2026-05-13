package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val onItemClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationAdapter.ViewHolder>(NotificationDiffCallback()) {

    class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvMessage.text = item.message
            tvTimestamp.text = item.timestamp
            
            // Destaque de não lida
            viewUnreadIndicator.visibility = if (item.isRead) View.GONE else View.VISIBLE
            rootLayout.setBackgroundResource(if (item.isRead) R.color.white else R.color.blue_ice)
            rootLayout.alpha = if (item.isRead) 0.8f else 1.0f

            // Ícone por tipo
            val (iconRes, iconTint) = when (item.type) {
                NotificationType.LOAN_REMINDER -> Pair(R.drawable.ic_notification, R.color.status_unavailable)
                NotificationType.RESERVATION_READY -> Pair(R.drawable.ic_notification, R.color.status_available_text)
                NotificationType.SYSTEM_ALERT -> Pair(R.drawable.ic_notification, R.color.blue_royal)
            }
            ivIcon.setImageResource(iconRes)
            ivIcon.imageTintList = ContextCompat.getColorStateList(root.context, iconTint)
            ivIcon.backgroundTintList = ContextCompat.getColorStateList(root.context, iconTint)?.withAlpha(40)

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
            oldItem == newItem
    }
}
