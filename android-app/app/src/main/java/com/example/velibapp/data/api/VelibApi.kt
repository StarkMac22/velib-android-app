package com.example.velibapp.data.api

import com.example.velibapp.data.model.StationResponse
import com.example.velibapp.data.model.StatusResponse
import retrofit2.http.GET

interface VelibApi {

    @GET("station_information.json")
    suspend fun getStations(): StationResponse

    @GET("station_status.json")
    suspend fun getStationsStatus(): StatusResponse
}