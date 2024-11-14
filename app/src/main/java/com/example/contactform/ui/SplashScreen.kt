// SplashActivity.kt
package com.example.contactform.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.contactform.R
import com.example.contactform.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and initialize binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the zoom-in animation
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)

        // Start the animation on the logo
        binding.logoImageView.startAnimation(zoomInAnimation)

        // Set up a listener to navigate to MainActivity after the animation ends
        zoomInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {
                // Do nothing
            }

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                // Navigate to MainActivity
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish() // Finish SplashActivity to prevent going back to it
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {
                // Do nothing
            }
        })
    }
}
