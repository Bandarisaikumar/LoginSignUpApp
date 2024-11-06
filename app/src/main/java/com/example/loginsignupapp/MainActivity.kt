package com.example.loginsignupapp

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var animatedSeparator: View
    private lateinit var viewPager: ViewPager2
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private val images = listOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4,
        R.drawable.image5,
        R.drawable.image6,
        R.drawable.image7,
        R.drawable.image8
    )

    private var currentImageIndex = 0
    private var autoSlideJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        imageSliderAdapter = ImageSliderAdapter(images)
        viewPager.adapter = imageSliderAdapter


        // Initially hide the ViewPager for the fade-in effect
        viewPager.visibility = View.INVISIBLE

        // Start auto sliding
        startAutoSliding()

        // Fade-in effect
        fadeInViewPager()
        animatedSeparator = findViewById(R.id.animated_separator)

        // Start the animation
        startSeparatorAnimation()
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    // A small animation for a separator
    private fun startSeparatorAnimation() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val animator = ValueAnimator.ofFloat(0f, screenWidth)
        animator.duration = 3000 // Duration for one complete cycle
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE // Make it reverse after reaching the end

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            // Move the separator across the screen
            animatedSeparator.translationX = animatedValue - animatedSeparator.width
        }

        animator.start()
    }
    // For auto sliding of images
    private fun startAutoSliding() {
        autoSlideJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(4000) // Delay for 5 seconds
                currentImageIndex = (currentImageIndex + 1) % images.size

                // Checking if we reached the first image after the last image
                if (currentImageIndex == 0) {
                    // Transition back to the first image without reverse
                    viewPager.setCurrentItem(currentImageIndex, false)
                } else {
                    viewPager.setCurrentItem(currentImageIndex, true)
                }
            }
        }
    }

    // Animation for viewpager
    private fun fadeInViewPager() {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 6000
            setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation) {
                    viewPager.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: android.view.animation.Animation) {}
                override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
            })
        }
        viewPager.startAnimation(fadeIn)
    }
    // User logout
    private fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()
        try {
            firebaseAuth.signOut()
            Toast.makeText(this, "User signed out.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        autoSlideJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (autoSlideJob == null || autoSlideJob?.isCompleted == true) {
            startAutoSliding()
        }
    }


}
