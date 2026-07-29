package com.example.velibapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.velibapp.data.api.RetrofitInstance
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        textView.text = "Chargement des stations Vélib..."

        setContentView(textView)

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitInstance.api.getStations()

                val stations =
                    response.data.stations

                textView.text =
                    "Stations Vélib trouvées : ${stations.size}"

            } catch (e: Exception) {

                textView.text =
                    "Erreur : ${e.message}"
            }
        }
    }
}