package com.example.airasiatest.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airasiatest.data.Flight
import com.example.airasiatest.ui.theme.AirAsiaTestTheme
import com.example.airasiatest.util.getSerializable

class FlightDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flight = intent.getSerializable("flight", Flight::class.java)
        setContent {
            AirAsiaTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlightDetailScreen(flight)
                }
            }
        }
    }
}

@Composable
fun FlightDetailScreen(flight: Flight?) {
    val isDarkTheme = isSystemInDarkTheme()
    flight?.let {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if(isDarkTheme) Color.White else Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = flight.flightName,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Station",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Text(text = "Depature: ${flight.departureStation}", color = Color.Black)
                        Text(text = flight.getDepartureDateTime(), color = Color.Black)
                    }
                    Column {
                        Text(text = "Arrival: ${flight.arrivalStation}", color = Color.Black)
                        Text(text = flight.getArrivalDateTime(), color = Color.Black)
                    }
                }
                Text(text = "Price: ${flight.price} ${flight.currency}", color = Color.Black)
                Text(text = "Stops: ${flight.stops}", color = Color.Black)
            }
        }
    }
}