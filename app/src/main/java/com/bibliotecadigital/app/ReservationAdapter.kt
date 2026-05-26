package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bibliotecadigital.app.databinding.ItemReservationBinding
import com.bibliotecadigital.app.entity.Reservation

class ReservationAdapter(
    private val onVerClick: (Reservation) -> Unit
) : ListAdapter<Reservation, ReservationAdapter.ViewHolder>(ReservationDiffCallback()) {

    class ViewHolder(val binding: ItemReservationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTitle.text = item.title
            tvAuthor.text = item.author
            tvQueue.text = "${item.queuePosition}º na fila de espera"

            ivCover.load(item.coverUrl) {
                placeholder(R.drawable.bg_cover_placeholder)
                error(R.drawable.bg_cover_placeholder)
            }

            btnVer.setOnClickListener { onVerClick(item) }
        }
    }

    class ReservationDiffCallback : DiffUtil.ItemCallback<Reservation>() {
        override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation): Boolean = oldItem == newItem
    }
}