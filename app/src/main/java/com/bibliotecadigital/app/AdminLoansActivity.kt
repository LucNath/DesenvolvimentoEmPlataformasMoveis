package com.bibliotecadigital.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadigital.app.databinding.ActivityAdminLoansBinding
import com.bibliotecadigital.app.databinding.ItemAdminLoanBinding
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

data class AdminLoan(
    val id: String,
    val userName: String,
    val bookTitle: String,
    val dueDate: String,
    var status: String, // "Ativo", "Devolvido", "Atrasado"
    var fineAmount: Double = 0.0,
    var isFinePaid: Boolean = false
)

class AdminLoansActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoansBinding
    private lateinit var adapter: AdminLoanAdapter
    private var allLoans = mutableListOf<AdminLoan>()
    private var currentTab = 0 // 0: Empréstimos, 1: Multas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMockData()
        setupRecyclerView()
        setupFilters()
        setupSearch()
        
        binding.btnBack.setOnClickListener { finish() }
        
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                binding.chipGroupFilters.visibility = if (currentTab == 0) View.VISIBLE else View.GONE
                filterData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupMockData() {
        allLoans = mutableListOf(
            AdminLoan("1", "Maria Silva", "O Senhor dos Anéis", "10/10/2023", "Atrasado", 15.0),
            AdminLoan("2", "João Souza", "Dom Casmurro", "20/10/2023", "Ativo"),
            AdminLoan("3", "Ana Costa", "1984", "15/10/2023", "Devolvido"),
            AdminLoan("4", "Pedro Rocha", "O Pequeno Príncipe", "25/10/2023", "Ativo"),
            AdminLoan("5", "Carlos Lima", "Clean Code", "05/10/2023", "Devolvido", 5.0, true),
            AdminLoan("6", "Julia Mello", "Harry Potter", "01/10/2023", "Atrasado", 25.5)
        )
    }

    private fun setupRecyclerView() {
        adapter = AdminLoanAdapter(
            onActionClick = { loan ->
                if (currentTab == 0) {
                    handleReturn(loan)
                } else {
                    handlePayFine(loan)
                }
            }
        )
        binding.rvAdminLoans.layoutManager = LinearLayoutManager(this)
        binding.rvAdminLoans.adapter = adapter
        filterData()
    }

    private fun handleReturn(loan: AdminLoan) {
        loan.status = "Devolvido"
        // Check if overdue to generate fine
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val dueDate = sdf.parse(loan.dueDate)
            if (dueDate != null && dueDate.before(Date())) {
                loan.fineAmount = 10.0 // Valor fixo para o mock
            }
        } catch (e: Exception) {}
        
        filterData()
    }

    private fun handlePayFine(loan: AdminLoan) {
        loan.isFinePaid = true
        filterData()
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, _ ->
            filterData()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterData()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterData() {
        val query = binding.etSearch.text.toString().lowercase()
        val filtered = allLoans.filter {
            (it.userName.lowercase().contains(query) || it.bookTitle.lowercase().contains(query)) &&
            if (currentTab == 0) {
                when (binding.chipGroupFilters.checkedChipId) {
                    R.id.chipActive -> it.status == "Ativo"
                    R.id.chipOverdue -> it.status == "Atrasado"
                    R.id.chipReturned -> it.status == "Devolvido"
                    else -> true
                }
            } else {
                it.fineAmount > 0
            }
        }
        adapter.isFineMode = (currentTab == 1)
        adapter.submitList(filtered.toList())
    }

    class AdminLoanAdapter(private val onActionClick: (AdminLoan) -> Unit) :
        ListAdapter<AdminLoan, AdminLoanAdapter.ViewHolder>(DiffCallback()) {

        var isFineMode = false

        inner class ViewHolder(val binding: ItemAdminLoanBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemAdminLoanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val loan = getItem(position)
            val context = holder.binding.root.context
            
            holder.binding.tvUserName.text = loan.userName
            holder.binding.tvBookTitle.text = loan.bookTitle
            holder.binding.tvDueDate.text = context.getString(R.string.due_date_label, loan.dueDate)
            holder.binding.tvStatus.text = loan.status
            
            if (isFineMode) {
                holder.binding.tvFineInfo.visibility = View.VISIBLE
                holder.binding.tvFineInfo.text = context.getString(R.string.fine_amount_label, loan.fineAmount)
                holder.binding.btnAction.text = if (loan.isFinePaid) context.getString(R.string.btn_paid) else context.getString(R.string.btn_pay_fine)
                holder.binding.btnAction.isEnabled = !loan.isFinePaid
                holder.binding.tvStatus.visibility = View.GONE
            } else {
                holder.binding.tvFineInfo.visibility = if (loan.fineAmount > 0) View.VISIBLE else View.GONE
                holder.binding.tvFineInfo.text = context.getString(R.string.fine_amount_label, loan.fineAmount)
                holder.binding.tvStatus.visibility = View.VISIBLE
                holder.binding.btnAction.text = context.getString(R.string.btn_return)
                holder.binding.btnAction.isEnabled = loan.status != "Devolvido"
            }

            holder.binding.btnAction.setOnClickListener { onActionClick(loan) }

            // Status colors
            val statusBg = when (loan.status) {
                "Ativo" -> R.drawable.bg_status_green
                "Atrasado" -> R.drawable.bg_status_red
                else -> R.drawable.bg_status_gray
            }
            holder.binding.tvStatus.setBackgroundResource(statusBg)
        }

        class DiffCallback : DiffUtil.ItemCallback<AdminLoan>() {
            override fun areItemsTheSame(oldItem: AdminLoan, newItem: AdminLoan) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: AdminLoan, newItem: AdminLoan) = oldItem == newItem
        }
    }
}