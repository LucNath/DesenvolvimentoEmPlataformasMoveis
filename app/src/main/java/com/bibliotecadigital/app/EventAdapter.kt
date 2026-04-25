package com.bibliotecadigital.app

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemEventBinding

class EventAdapter(
    private var events: List<Event>,
    private val onRegistrationChanged: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        with(holder.binding) {
            tvEventName.text = event.name
            tvEventDateTime.text = "${event.date} · ${event.time}"
            tvFacilitator.text = event.facilitator
            tvSpots.text = "${event.availableSpots} vagas disponíveis"

            updateButtonStyle(this, event)

            btnRegister.setOnClickListener {
                showConfirmationDialog(holder, event)
            }
        }
    }

    private fun updateButtonStyle(binding: ItemEventBinding, event: Event) {
        val context = binding.root.context
        when {
            event.isRegistered -> {
                binding.btnRegister.text = "Inscrito"
                binding.btnRegister.isEnabled = true
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(context, R.color.green_text))
            }
            event.availableSpots <= 0 -> {
                binding.btnRegister.text = "Esgotado"
                binding.btnRegister.isEnabled = false
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(context, R.color.text_gray))
            }
            else -> {
                binding.btnRegister.text = "Inscrever-se"
                binding.btnRegister.isEnabled = true
                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(context, R.color.brand_orange))
            }
        }
    }

    private fun showConfirmationDialog(holder: ViewHolder, event: Event) {
        val context = holder.binding.root.context
        val title = if (event.isRegistered) "Cancelar Inscrição" else "Confirmar Inscrição"
        val message = if (event.isRegistered) 
            "Deseja cancelar sua inscrição no evento ${event.name}?" 
            else "Deseja se inscrever no evento ${event.name}?"

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirmar") { _, _ ->
                if (event.isRegistered) {
                    event.isRegistered = false
                    event.availableSpots++
                } else {
                    event.isRegistered = true
                    event.availableSpots--
                }
                notifyItemChanged(holder.adapterPosition)
                onRegistrationChanged(event)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}