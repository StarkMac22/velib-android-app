package com.example.velibapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HowToActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}
