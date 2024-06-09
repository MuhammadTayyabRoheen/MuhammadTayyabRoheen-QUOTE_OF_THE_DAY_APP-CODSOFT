package com.example.quoteofthedayapp

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val btnStart: Button = findViewById(R.id.btnStart)

        // Load the animation
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.button_bounce)
        btnStart.startAnimation(bounceAnimation)

        btnStart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the SplashActivity
        }
    }
}
