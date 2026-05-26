package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemEventBinding
import com.bibliotecadigital.app.entity.Event
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EventAdapter(
    private val currentUserId: String,
    private val onAction: (Event, Boolean) -> Unit // Boolean: true for enroll, false for cancel
) : ListAdapter<Event, EventAdapter.ViewHolder>(EventDiffCallback()) {

    class ViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        val isRegistered = event.participants.contains(currentUserId)
        val availableSpots = event.totalSlots - event.usedSlots

        with(holder.binding) {
            tvEventName.text = event.name
            tvEventDateTime.text = "${event.date} · ${event.time}"
            tvFacilitator.text = event.facilitator
            tvSpots.text = root.context.getString(R.string.spots_available, availableSpots)

            updateButtonStyle(this, isRegistered, availableSpots)

            btnRegister.setOnClickListener {
                showConfirmationDialog(it.context, event, isRegistered)
            }
        }
    }

    private fun updateButtonStyle(binding: ItemEventBinding, isRegistered: Boolean, availableSpots: Int) {
        val context = binding.root.context
        when {
            isRegistered -> {
                binding.btnRegister.text = context.getString(R.string.event_registered)
                binding.btnRegister.isEnabled = true
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(context, R.color.green_text))
            }
            availableSpots <= 0 -> {
                binding.btnRegister.text = context.getString(R.string.event_sold_out)
                binding.btnRegister.isEnabled = false
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(context, R.color.text_gray))
            }
            else -> {
                binding.btnRegister.text = context.getString(R.string.btn_register_event)
                binding.btnRegister.isEnabled = true
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(context, R.color.brand_orange))
            }
        }
    }

    private fun showConfirmationDialog(context: android.content.Context, event: Event, isRegistered: Boolean) {
        val title = if (isRegistered) 
            context.getString(R.string.cancel_registration) 
            else context.getString(R.string.confirm_registration)
        
        val message = if (isRegistered) 
            context.getString(R.string.cancel_registration_msg, event.name) 
            else context.getString(R.string.confirm_registration_msg, event.name)

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.btn_confirm) { _, _ ->
                onAction(event, !isRegistered)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem
    }
}