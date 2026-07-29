package com.example.velibapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Lien "Se connecter" → retour vers AccountActivity
        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnRegister).setOnClickListener {
            val firstName = findViewById<TextInputEditText>(R.id.etFirstName).text.toString().trim()
            val lastName  = findViewById<TextInputEditText>(R.id.etLastName).text.toString().trim()
            val email     = findViewById<TextInputEditText>(R.id.etEmail).text.toString().trim()
            val password  = findViewById<TextInputEditText>(R.id.etPassword).text.toString()
            val confirm   = findViewById<TextInputEditText>(R.id.etPasswordConfirm).text.toString()

            when {
                firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() -> {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Adresse e-mail invalide", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
                }
                password != confirm -> {
                    Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Compte créé avec succès ! Bienvenue $firstName 🎉", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
}
