package com.example.airasiatest.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airasiatest.data.Flight
import com.example.airasiatest.enum.PriceRange
import com.example.airasiatest.viewmodel.FlightViewModel

private lateinit var viewModel: FlightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(flightViewModel: FlightViewModel) {
    viewModel = flightViewModel
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            FlightNameDropdown()
            FlightTimeButtons()
        }
        FlightPriceDropdown()
        FlightListView(
            viewModel.filteredFlights,
        )
        if (viewModel.isTimePickerShown) {
            DateRangePickerDialog(
                onDismiss = viewModel::onDismissDateRangePicker,
                onConfirm = viewModel::onTimeSelected,
                startDate = viewModel.selectedStartDate.timeInMillis,
                endDate = viewModel.selectedEndDate.timeInMillis
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FlightListView(flights: List<Flight>) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isLoading,
        onRefresh = viewModel::refreshFlights
    )
    Box(
        modifier = Modifier.pullRefresh(pullRefreshState)
    ) {
        if (flights.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp)
            ) {
                Text(text = "No flights available")
            }
        } else {
            LazyColumn {
                items(flights) { flight ->
                    FlightListItem(flight)
                }
            }
        }
        PullRefreshIndicator(
            refreshing = viewModel.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlightListItem(flight: Flight) {
    val isDarkTheme = isSystemInDarkTheme()
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if(isDarkTheme) Color.White else Color.LightGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(
                onClick = { viewModel.onFlightClick(flight) }
            ),
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlightNameDropdown(
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clickable { expanded = true }
    ) {
        Text(
            text = if (viewModel.selectedAirline.isEmpty()) "Select Airlines" else viewModel.selectedAirline,
            modifier = Modifier.padding(16.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Clear selection option
            if (viewModel.selectedAirline.isNotEmpty())
                DropdownMenuItem(
                    onClick = {
                        viewModel.clearAirlineFilter()
                        expanded = false
                    }
                ) {
                    Text(text = "Clear Selection", color = Color.Black)
                }
            viewModel.airlinesOptions.forEach { name ->
                DropdownMenuItem(
                    onClick = {
                        viewModel.onAirlineSelected(name)
                        expanded = false
                    }
                ) {
                    Text(text = name, color = Color.Black)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlightPriceDropdown() {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clickable { expanded = true }
    ) {
        (if (viewModel.selectedPriceRange == PriceRange.DEFAULT) "Select Price Range" else viewModel.priceRangeList[viewModel.selectedPriceRange])?.let {
            Text(
                text = it,
                modifier = Modifier.padding(16.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Clear selection option
            if (viewModel.selectedPriceRange != PriceRange.DEFAULT)
                DropdownMenuItem(
                    onClick = {
                        viewModel.clearPriceRangeFilter()
                        expanded = false
                    }
                ) {
                    Text(text = "Clear Selection", color = Color.Black)
                }
            viewModel.priceRangeList.forEach { map ->
                DropdownMenuItem(
                    onClick = {
                        viewModel.onPriceRangeSelected(map)
                        expanded = false
                    }
                ) {
                    Text(text = map.value, color = Color.Black)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlightTimeButtons() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(all = 5.dp)
            .clickable(onClick = {
                viewModel.onTimePickerClick()
            })
    ) {
        Card(
            modifier = Modifier
                .padding(all = 5.dp)
                .width(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "From:", color = Color.Black)
                Text(
                    text = viewModel.getFormattedDate(viewModel.selectedStartDate),
                    color = Color.Black
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(all = 5.dp)
                .width(100.dp)
                .align(Alignment.CenterVertically),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "To:", color = Color.Black)
                Text(
                    text = viewModel.getFormattedDate(viewModel.selectedEndDate),
                    color = Color.Black
                )
            }
        }
    }
}