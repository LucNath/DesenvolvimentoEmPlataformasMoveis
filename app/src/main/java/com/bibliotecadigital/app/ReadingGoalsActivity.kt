package com.bibliotecadigital.app

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReadingGoalsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_goals)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val fabAddGoal = findViewById<FloatingActionButton>(R.id.fabAddGoal)

        btnBack.setOnClickListener {
            finish()
        }

        fabAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun showAddGoalDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_goal, null)

        val etBooksQuantity = view.findViewById<EditText>(R.id.etBooksQuantity)
        val etPeriod = view.findViewById<EditText>(R.id.etPeriod)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Salvar") { _, _ ->
                val quantity = etBooksQuantity.text.toString().trim()
                val period = etPeriod.text.toString().trim()

                if (quantity.isEmpty() || period.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Meta criada: $quantity livros em $period",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}