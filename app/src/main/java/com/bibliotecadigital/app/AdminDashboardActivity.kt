package com.bibliotecadigital.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ActivityAdminDashboardBinding
import com.bibliotecadigital.app.databinding.ItemFineBinding // Reusing for similar layout or creating new

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Security check
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userRole = prefs.getString("user_role", "user")
        if (userRole != "admin") {
            Toast.makeText(this, getString(R.string.access_denied), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStats()
        setupButtons()
        setupRecentLoans()
        setupMostBorrowed()
    }

    private fun setupStats() {
        binding.tvTotalBooksCount.text = getString(R.string.total_books_count)
        binding.tvActiveLoansCount.text = getString(R.string.active_loans_count)
        binding.tvReservationsCount.text = getString(R.string.reservations_count)
        binding.tvPendingFinesCount.text = getString(R.string.pending_fines_amount)
    }

    private fun setupButtons() {
        binding.btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit().putBoolean("is_logged_in", false).apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.btnSwitchToUser.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddBook.setOnClickListener { 
            val intent = Intent(this, AdminBooksActivity::class.java)
            startActivity(intent)
        }
        binding.btnViewUsers.setOnClickListener { 
            val intent = Intent(this, AdminUsersActivity::class.java)
            startActivity(intent)
        }
        binding.btnViewFines.setOnClickListener { 
            val intent = Intent(this, AdminLoansActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecentLoans() {
        val recentLoans = listOf(
            LoanMock("O Senhor dos Anéis", "Maria Silva", "12/10"),
            LoanMock("Dom Casmurro", "João Souza", "11/10"),
            LoanMock("1984", "Ana Costa", "11/10"),
            LoanMock("O Pequeno Príncipe", "Pedro Rocha", "10/10"),
            LoanMock("Clean Code", "Carlos Lima", "09/10")
        )

        binding.rvRecentLoans.layoutManager = LinearLayoutManager(this)
        binding.rvRecentLoans.adapter = GenericAdminAdapter(recentLoans)
    }

    private fun setupMostBorrowed() {
        val mostBorrowed = listOf(
            LoanMock("Harry Potter", "32 empréstimos", "9.5 rating"),
            LoanMock("A Metafísica", "28 empréstimos", "8.2 rating"),
            LoanMock("Pai Rico, Pai Pobre", "25 empréstimos", "9.0 rating")
        )
        binding.rvMostBorrowed.layoutManager = LinearLayoutManager(this)
        binding.rvMostBorrowed.adapter = GenericAdminAdapter(mostBorrowed)
    }

    data class LoanMock(val title: String, val subtitle: String, val info: String)

    inner class GenericAdminAdapter(private val items: List<LoanMock>) :
        RecyclerView.Adapter<GenericAdminAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemFineBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemFineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.binding.tvBookTitle.text = item.title
            holder.binding.tvDetails.text = item.subtitle
            holder.binding.tvAmount.text = item.info
            holder.binding.tvStatus.text = "Ativo"
        }

        override fun getItemCount() = items.size
    }
}