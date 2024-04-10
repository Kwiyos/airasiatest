package com.example.airasiatest.model

import com.example.airasiatest.data.Flight

class FlightRepository(private val apiService: ApiService) {
    suspend fun getFlights(): Result<List<Flight>> {
        return try {
            val flightList = apiService.getFlights()
            Result.success(flightList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}