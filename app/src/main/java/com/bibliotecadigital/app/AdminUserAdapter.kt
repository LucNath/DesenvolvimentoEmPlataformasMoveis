package com.bibliotecadigital.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ItemUserBinding

class AdminUserAdapter(
    private var users: List<User>,
    private val onEdit: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            tvUserName.text = user.name
            tvUserEmail.text = user.email
            tvUserRole.text = user.role.uppercase()
            
            btnEditUser.setOnClickListener { onEdit(user) }
            btnDeleteUser.setOnClickListener { onDelete(user) }
        }
    }

    override fun getItemCount() = users.size

    fun updateList(newList: List<User>) {
        users = newList
        notifyDataSetChanged()
    }
}