package com.bibliotecadigital.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bibliotecadigital.app.databinding.ActivityOnboardingBinding
import com.bibliotecadigital.app.databinding.ItemOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        val slides = listOf(
            OnboardingSlide(
                "Bem-vindo à Biblioteca",
                "Explore milhares de livros e eventos culturais na palma da sua mão.",
                R.drawable.logo
            ),
            OnboardingSlide(
                "Funcionalidades Principais",
                "Faça reservas, gerencie empréstimos e receba notificações em tempo real.",
                R.drawable.ic_library_books
            ),
            OnboardingSlide(
                "Comece Agora",
                "Sua jornada literária começa aqui. Vamos explorar juntos!",
                R.drawable.logo
            )
        )

        binding.viewPager.adapter = OnboardingAdapter(slides)
        setupIndicators(slides.size)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
                if (position == slides.size - 1) {
                    binding.btnNext.text = "Começar"
                } else {
                    binding.btnNext.text = "Próximo"
                }
            }
        })
    }

    private fun setupIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(20, 20).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_tag_gray))
            indicators[i]?.layoutParams = layoutParams
            binding.layoutIndicators.addView(indicators[i])
        }
    }

    private fun updateIndicators(position: Int) {
        val childCount = binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_tag_orange))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_tag_gray))
            }
        }
    }

    private fun setupButtons() {
        binding.btnSkip.setOnClickListener { finishOnboarding() }
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < 2) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }
    }

    private fun finishOnboarding() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_finished", true).apply()
        
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    data class OnboardingSlide(val title: String, val description: String, val icon: Int)

    inner class OnboardingAdapter(private val slides: List<OnboardingSlide>) :
        RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemOnboardingBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val slide = slides[position]
            holder.binding.tvTitle.text = slide.title
            holder.binding.tvDescription.text = slide.description
            holder.binding.ivOnboarding.setImageResource(slide.icon)
        }

        override fun getItemCount() = slides.size
    }
}