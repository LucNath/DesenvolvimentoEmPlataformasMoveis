package com.bibliotecadigital.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.ActivityAdminUsersBinding
import com.bibliotecadigital.app.databinding.DialogAddUserBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUsersBinding
    private lateinit var adapter: AdminUserAdapter
    private var allUsers = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadMockUsers()
        setupRecyclerView()
        setupSearch()
        setupFab()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadMockUsers() {
        allUsers = mutableListOf(
            User("1", "Lucas Mendes", "lucas@email.com", "admin", "10/01/2024"),
            User("2", "Maria Silva", "maria@email.com", "user", "15/01/2024"),
            User("3", "João Souza", "joao@email.com", "user", "20/01/2024"),
            User("4", "Ana Costa", "ana@email.com", "user", "22/01/2024"),
            User("5", "Pedro Rocha", "pedro@email.com", "user", "25/01/2024")
        )
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(
            allUsers,
            onEdit = { user -> showUserDialog(user) },
            onDelete = { user -> confirmDelete(user) }
        )
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchUsers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filtered = allUsers.filter { 
                    it.name.lowercase().contains(query) || it.email.lowercase().contains(query) 
                }
                adapter.updateList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFab() {
        binding.fabAddUser.setOnClickListener { showUserDialog(null) }
    }

    private fun showUserDialog(user: User?) {
        val dialogBinding = DialogAddUserBinding.inflate(LayoutInflater.from(this))
        val roles = arrayOf("admin", "user")
        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        dialogBinding.spinnerRole.setAdapter(roleAdapter)

        if (user != null) {
            dialogBinding.etName.setText(user.name)
            dialogBinding.etEmail.setText(user.email)
            dialogBinding.spinnerRole.setText(user.role, false)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(if (user == null) "Adicionar Usuário" else "Editar Usuário")
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar") { _, _ ->
                val name = dialogBinding.etName.text.toString()
                val email = dialogBinding.etEmail.text.toString()
                val role = dialogBinding.spinnerRole.text.toString()

                if (name.isNotEmpty() && email.isNotEmpty()) {
                    if (user == null) {
                        val newUser = User(
                            (allUsers.size + 1).toString(),
                            name, email, role, "27/04/2026"
                        )
                        allUsers.add(newUser)
                        Toast.makeText(this, "Usuário adicionado", Toast.LENGTH_SHORT).show()
                    } else {
                        val index = allUsers.indexOfFirst { it.id == user.id }
                        if (index != -1) {
                            allUsers[index] = user.copy(name = name, email = email, role = role)
                            Toast.makeText(this, "Usuário atualizado", Toast.LENGTH_SHORT).show()
                        }
                    }
                    adapter.updateList(allUsers)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDelete(user: User) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Excluir Usuário")
            .setMessage("Tem certeza que deseja excluir ${user.name}?")
            .setPositiveButton("Excluir") { _, _ ->
                allUsers.removeIf { it.id == user.id }
                adapter.updateList(allUsers)
                Toast.makeText(this, "Usuário removido", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}