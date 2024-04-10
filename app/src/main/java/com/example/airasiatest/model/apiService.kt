package com.example.airasiatest.model

import com.example.airasiatest.data.Flight
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("mobile/assignment/flightssample.json")
    suspend fun getFlights(): List<Flight>
}