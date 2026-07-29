package com.example.velibapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnLogin).setOnClickListener {
            val email = findViewById<TextInputEditText>(R.id.etEmail).text.toString()
            val password = findViewById<TextInputEditText>(R.id.etPassword).text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Connexion en cours...", Toast.LENGTH_SHORT).show()
            }
        }

        // Mot de passe oublié
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Créer un compte
        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
