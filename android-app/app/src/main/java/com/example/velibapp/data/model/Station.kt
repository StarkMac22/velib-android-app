package com.example.velibapp.data.model

data class Station(
    val station_id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int
)