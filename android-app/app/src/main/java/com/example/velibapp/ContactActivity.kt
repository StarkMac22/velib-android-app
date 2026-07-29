package com.example.velibapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Tap email → ouvre le client mail
        findViewById<MaterialCardView>(R.id.cardEmail).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:yanwill@gmail.com")
            }
            startActivity(Intent.createChooser(intent, "Envoyer un email"))
        }

        // Tap téléphone → compose le numéro
        findViewById<MaterialCardView>(R.id.cardPhone).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:0753658869")
            }
            startActivity(intent)
        }

        // Bouton envoyer
        findViewById<MaterialButton>(R.id.btnSend).setOnClickListener {
            Toast.makeText(this, "Message envoyé !", Toast.LENGTH_SHORT).show()
        }
    }
}
