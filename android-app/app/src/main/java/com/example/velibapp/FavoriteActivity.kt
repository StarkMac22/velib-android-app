package com.example.velibapp

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.velibapp.data.api.RetrofitInstance
import kotlinx.coroutines.launch
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_favorite
        )

        val favorites =
            FavoriteManager.getFavorites(this)

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.recyclerFavorites
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        val adapter =
            FavoriteAdapter(
                favorites
            ) { favorite ->

                lifecycleScope.launch {

                    try {

                        val statusResponse =
                            RetrofitInstance.api
                                .getStationsStatus()

                        val status =
                            statusResponse.data.stations.find {

                                it.station_id ==
                                        favorite.stationId
                            }

                        if (status != null) {

                            AlertDialog.Builder(this@FavoriteActivity)
                                .setTitle(
                                    favorite.stationName
                                )
                                .setMessage(
                                    "🚲 Vélos disponibles : ${
                                        status.num_bikes_available
                                    }\n\n" +
                                            "🅿 Places disponibles : ${
                                                status.num_docks_available
                                            }"
                                )
                                .setPositiveButton(
                                    "Fermer",
                                    null
                                )
                                .setNegativeButton(
                                    "Supprimer"
                                ) { _, _ ->

                                    FavoriteManager.removeFavorite(
                                        this@FavoriteActivity,
                                        favorite.stationId
                                    )

                                    recreate()
                                }
                                .show()
                        }

                    } catch (e: Exception) {

                        AlertDialog.Builder(
                            this@FavoriteActivity
                        )
                            .setMessage(
                                "Erreur : ${e.message}"
                            )
                            .show()
                    }
                }
            }

        recyclerView.adapter = adapter



        findViewById<ImageButton>(R.id.btnBack)
            .setOnClickListener {
                finish()
            }
    }
}