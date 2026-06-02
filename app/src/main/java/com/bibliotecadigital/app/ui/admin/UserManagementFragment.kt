package com.bibliotecadigital.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.FragmentUserManagementBinding
import com.bibliotecadigital.app.databinding.ItemUserBinding
import com.bibliotecadigital.app.entity.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class UserManagementFragment : Fragment() {

    private var _binding: FragmentUserManagementBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            showRoleChangeDialog(user)
        }
        binding.rvUsers.adapter = userAdapter
    }

    private fun loadUsers() {
        db.collection("users").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Toast.makeText(requireContext(), "Erro ao carregar usuários", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val users = snapshot?.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(uid = doc.id)
            } ?: emptyList()

            userAdapter.submitList(users)
        }
    }

    private fun showRoleChangeDialog(user: User) {
        val roles = arrayOf("student", "admin")
        val checkedItem = roles.indexOf(user.role)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Alterar papel de ${user.name}")
            .setSingleChoiceItems(roles, checkedItem) { dialog, which ->
                val newRole = roles[which]
                updateUserRole(user.uid, newRole)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateUserRole(userId: String, newRole: String) {
        db.collection("users").document(userId)
            .update("role", newRole)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Papel atualizado com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao atualizar papel", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class UserAdapter(private val onUserClick: (User) -> Unit) :
        ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return UserViewHolder(binding)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = getItem(position)
            holder.bind(user)
            holder.itemView.setOnClickListener { onUserClick(user) }
        }

        class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(user: User) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = user.email
                binding.chipRole.text = user.role.uppercase()
                
                val context = binding.root.context
                if (user.role == "admin") {
                    binding.chipRole.setChipBackgroundColorResource(com.bibliotecadigital.app.R.color.blue_royal)
                    binding.chipRole.setTextColor(android.graphics.Color.WHITE)
                } else {
                    binding.chipRole.setChipBackgroundColorResource(android.R.color.transparent)
                    binding.chipRole.setTextColor(context.getColor(com.bibliotecadigital.app.R.color.text_secondary_light))
                }
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.uid == newItem.uid
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}
