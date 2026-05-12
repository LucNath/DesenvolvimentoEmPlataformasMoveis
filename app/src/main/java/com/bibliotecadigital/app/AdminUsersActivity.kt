package com.bibliotecadigital.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadigital.app.databinding.ActivityAdminUsersBinding
import com.bibliotecadigital.app.databinding.DialogAddUserBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUsersBinding
    private val viewModel: AdminUsersViewModel by viewModels()
    private lateinit var adapter: AdminUserAdapter
    private var allUsers = listOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(
            emptyList(),
            onEdit = { user -> showUserDialog(user) },
            onDelete = { user -> confirmDelete(user) }
        )
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is AdminUsersState.Loading -> {
                        // Mostrar progress se necessário
                    }
                    is AdminUsersState.Success -> {
                        allUsers = state.users
                        adapter.updateList(allUsers)
                    }
                    is AdminUsersState.Error -> {
                        Toast.makeText(this@AdminUsersActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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
        val roles = arrayOf("admin", "student")
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
                    val userToSave = user?.copy(name = name, email = email, role = role)
                        ?: User(uid = java.util.UUID.randomUUID().toString(), name = name, email = email, role = role)
                    
                    viewModel.saveUser(userToSave)
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
                viewModel.deleteUser(user.uid)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}