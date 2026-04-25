package com.bibliotecadigital.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bibliotecadigital.app.databinding.ActivityReadingBinding
import com.bibliotecadigital.app.databinding.ItemReadingPageBinding

class ReadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadingBinding
    private var currentFontSize = 18f
    private var bookId: String = ""
    private var bookTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId") ?: "default"
        bookTitle = intent.getStringExtra("bookTitle") ?: "Livro Digital"

        setupFullscreen()
        setupUI()
        setupViewPager()
    }

    private fun setupFullscreen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun setupUI() {
        binding.tvBookTitle.text = bookTitle
        binding.btnBack.setOnClickListener { finish() }

        binding.btnFontIncrease.setOnClickListener {
            if (currentFontSize < 32f) {
                currentFontSize += 2f
                updateFontSize()
            }
        }

        binding.btnFontDecrease.setOnClickListener {
            if (currentFontSize > 12f) {
                currentFontSize -= 2f
                updateFontSize()
            }
        }
    }

    private fun updateFontSize() {
        // Redraw current item to apply font size
        binding.viewPager.adapter?.notifyDataSetChanged()
    }

    private fun setupViewPager() {
        val pages = listOf(
            "Capítulo 1: O Início\n\nEra uma vez, em uma terra distante, uma pequena vila onde todos amavam ler. Os livros eram a maior riqueza daquele povo...",
            "As bibliotecas eram palácios, e os bibliotecários eram vistos como sábios guardiões do conhecimento. Cada página virada era uma nova aventura.",
            "Um dia, um jovem descobriu um livro antigo escondido em uma prateleira empoeirada. O título brilhava como ouro: 'A Jornada Digital'.",
            "Ao abrir o livro, uma luz azulada envolveu o ambiente. Ele percebeu que o conhecimento agora poderia viajar pelo ar, alcançando todos os cantos.",
            "E assim começou a era da Biblioteca Digital, onde a sabedoria do passado se uniu à tecnologia do futuro para iluminar o mundo."
        )

        val adapter = ReadingAdapter(pages)
        binding.viewPager.adapter = adapter
        
        binding.progressBar.max = pages.size
        
        // Restore progress
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedPage = prefs.getInt("reading_progress_$bookId", 0)
        binding.viewPager.setCurrentItem(savedPage, false)
        updateProgress(savedPage, pages.size)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateProgress(position, pages.size)
                // Save progress
                prefs.edit().putInt("reading_progress_$bookId", position).apply()
            }
        })
    }

    private fun updateProgress(position: Int, total: Int) {
        binding.progressBar.progress = position + 1
        binding.tvProgress.text = "Página ${position + 1} de $total"
    }

    inner class ReadingAdapter(private val pages: List<String>) :
        RecyclerView.Adapter<ReadingAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemReadingPageBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemReadingPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.tvPageContent.text = pages[position]
            holder.binding.tvPageContent.textSize = currentFontSize
        }

        override fun getItemCount() = pages.size
    }
}