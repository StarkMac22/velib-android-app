package com.example.velibapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoriteManager {

    private const val PREF_NAME = "velib_favorites"
    private const val FAVORITES_KEY = "favorites"

    private val gson = Gson()

    fun addFavorite(
        context: Context,
        stationId: String,
        stationName: String
    ) {

        val favorites = getFavorites(context).toMutableList()

        if (favorites.none { it.stationId == stationId }) {

            favorites.add(
                FavoriteStation(
                    stationId,
                    stationName
                )
            )

            saveFavorites(
                context,
                favorites
            )
        }
    }

    fun getFavorites(
        context: Context
    ): List<FavoriteStation> {

        val prefs =
            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        val json =
            prefs.getString(
                FAVORITES_KEY,
                null
            ) ?: return emptyList()

        val type =
            object : TypeToken<List<FavoriteStation>>() {}.type

        return gson.fromJson(
            json,
            type
        )
    }

    private fun saveFavorites(
        context: Context,
        favorites: List<FavoriteStation>
    ) {

        val prefs =
            context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putString(
                FAVORITES_KEY,
                gson.toJson(favorites)
            )
            .apply()
    }

    fun removeFavorite(
        context: Context,
        stationId: String
    ) {

        val favorites =
            getFavorites(context)
                .toMutableList()

        favorites.removeAll {
            it.stationId == stationId
        }

        saveFavorites(
            context,
            favorites
        )
    }
}