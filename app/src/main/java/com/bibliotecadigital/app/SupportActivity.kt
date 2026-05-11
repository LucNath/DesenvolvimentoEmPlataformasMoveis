package com.bibliotecadigital.app

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadigital.app.databinding.ActivitySupportBinding
import com.google.android.material.snackbar.Snackbar

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding
    private val viewModel: SupportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        val categories = listOf(
            getString(R.string.support_cat_loan),
            getString(R.string.support_cat_reservation),
            getString(R.string.support_cat_access),
            getString(R.string.support_cat_suggestion),
            getString(R.string.support_cat_other)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)

        binding.btnSend.setOnClickListener {
            validateAndSend()
        }
    }

    private fun validateAndSend() {
        val category = binding.actvCategory.text.toString()
        val description = binding.etDescription.text.toString()

        var isValid = true

        if (category.isEmpty()) {
            binding.tilCategory.error = getString(R.string.support_error_category)
            isValid = false
        } else {
            binding.tilCategory.error = null
        }

        if (description.length < 20) {
            binding.tilDescription.error = getString(R.string.support_error_description)
            isValid = false
        } else {
            binding.tilDescription.error = null
        }

        if (isValid) {
            viewModel.sendFeedback(category, description)
        }
    }

    private fun observeViewModel() {
        viewModel.supportResult.observe(this) { success ->
            if (success) {
                Snackbar.make(binding.root, R.string.support_success_msg, Snackbar.LENGTH_LONG).show()
                clearFields()
            }
        }
    }

    private fun clearFields() {
        binding.actvCategory.setText("", false)
        binding.etDescription.setText("")
        binding.tilCategory.error = null
        binding.tilDescription.error = null
    }
}