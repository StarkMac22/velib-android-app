package com.example.velibapp

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val btnSend = findViewById<MaterialButton>(R.id.btnSendReset)
        val layoutConfirmation = findViewById<LinearLayout>(R.id.layoutConfirmation)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tvGoBack).setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            when {
                email.isBlank() -> {
                    etEmail.error = "Veuillez saisir votre adresse e-mail"
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = "Adresse e-mail invalide"
                }
                else -> {
                    // Affiche la confirmation et désactive le bouton
                    layoutConfirmation.visibility = View.VISIBLE
                    btnSend.isEnabled = false
                    btnSend.alpha = 0.6f
                    etEmail.isEnabled = false
                }
            }
        }
    }
}
