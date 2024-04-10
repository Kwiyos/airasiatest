package com.example.airasiatest.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Flight(
    @SerializedName("flight_name") val flightName: String,
    @SerializedName("dept_station") val departureStation: String,
    @SerializedName("arvl_station") val arrivalStation: String,
    val stops: Int,
    val price: Double,
    val currency: String,
    @SerializedName("departureTimeUtc") val departureTimeUtc: String,
    @SerializedName("arrivalTimeUtc") val arrivalTimeUtc: String
) : Serializable {
    fun getDepartureDateTime(): String {
        return getFormattedDate(this.departureTimeUtc)
    }

    fun getArrivalDateTime(): String {
        return getFormattedDate(this.arrivalTimeUtc)
    }

    fun getDepartureDateObject(): Date? {
        return getDateObject(this.departureTimeUtc)
    }

    private fun getFormattedDate(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error occurred while parsing date"
        }
    }

    private fun getDateObject(dateTime: String): Date? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return try {
            inputFormat.parse(dateTime)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
