package com.example.airasiatest.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.airasiatest.data.Flight
import com.example.airasiatest.enum.PriceRange
import com.example.airasiatest.model.ApiClient
import com.example.airasiatest.model.FlightRepository
import com.example.airasiatest.util.Event
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections

@RequiresApi(Build.VERSION_CODES.O)
class FlightViewModel : ViewModel() {
    private val flightRepository = FlightRepository(ApiClient.apiService)
    private val fetchErrorMessage = "Unable to retrieve flights, please try again later"
    private val datePickerErrorMessage = "Select a valid date range"

    //Objects
    private var _flights: List<Flight> = emptyList()
    var filteredFlights: List<Flight> by mutableStateOf(listOf())
        private set
    var priceRangeList = mapOf(
        PriceRange.UNDER_200 to "Under 200",
        PriceRange.BETWEEN_200_AND_500 to "200 to 500",
        PriceRange.OVER_500 to "Over 500",
    )

    //Status and Trigger
    var isLoading: Boolean by mutableStateOf(false)
        private set
    var isTimePickerShown: Boolean by mutableStateOf(false)
        private set
    private val _statusMessage = MutableLiveData<Event<String>>()
    val statusMessage: LiveData<Event<String>>
        get() = _statusMessage
    private val _launchDetailActivity = MutableLiveData<Event<Flight>>()
    val launchDetailActivity: LiveData<Event<Flight>>
        get() = _launchDetailActivity
    var airlinesOptions: List<String> by mutableStateOf(listOf())
    var selectedAirline: String by mutableStateOf("")
    var selectedStartDate: Calendar by mutableStateOf(Calendar.getInstance())
    var selectedEndDate: Calendar by mutableStateOf(Calendar.getInstance())
    var selectedPriceRange: PriceRange by mutableStateOf(PriceRange.DEFAULT)

    init {
        viewModelScope.launch {
            fetchFlights()
            filteredFlights = _flights
            initFilterValue()
        }
    }

    fun refreshFlights() {
        viewModelScope.launch {
            isLoading = true
            fetchFlights()
            isLoading = false
            filterFlights()
        }
    }

    private suspend fun fetchFlights() {
        val response = flightRepository.getFlights()
        if (response.isSuccess) {
            _flights = response.getOrDefault(emptyList())
        } else {
            _statusMessage.value = Event(fetchErrorMessage)
        }
    }

    private fun initFilterValue() {
        val names = _flights.map { it.flightName }.distinct()
        airlinesOptions = names
        val depatureTimes = _flights.map { it.getDepartureDateObject() }
        val firstDepature = Collections.min(depatureTimes)
        val lastDepature = Collections.max(depatureTimes)
        selectedStartDate = Calendar.getInstance().apply { time = firstDepature }
        selectedEndDate = Calendar.getInstance().apply { time = lastDepature }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onTimeSelected(state: DateRangePickerState) {
        if (state.selectedStartDateMillis == null || state.selectedEndDateMillis == null) {
            _statusMessage.value = Event(datePickerErrorMessage)
            return
        }

        selectedStartDate = timestampToCalendar(state.selectedStartDateMillis!!)
        selectedEndDate =
            timestampToCalendar(state.selectedEndDateMillis!!).apply { add(Calendar.MINUTE, -1) }
                .apply { add(Calendar.DAY_OF_MONTH, 1) }
        filterFlights()
        isTimePickerShown = false
    }

    fun getFormattedDate(calender: Calendar): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd")
        return dateFormat.format(calender.timeInMillis)
    }

    fun onTimePickerClick() {
        isTimePickerShown = true
    }

    fun onDismissDateRangePicker() {
        isTimePickerShown = false
    }

    fun onAirlineSelected(name: String) {
        selectedAirline = name
        filterFlights()
    }

    fun onPriceRangeSelected(map: Map.Entry<PriceRange, String>) {
        selectedPriceRange = map.key
        filterFlights()
    }

    fun onFlightClick(flight: Flight) {
        _launchDetailActivity.value = Event(flight)
    }

    private fun filterFlights() {
        filteredFlights = _flights.filter { flight ->
            // Filter by selected flight name
            val matchName = selectedAirline.isEmpty() || flight.flightName == selectedAirline

            // Filter by date range
            val date = flight.getDepartureDateObject()
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }
            val matchTime = calendar.after(selectedStartDate) && calendar.before(selectedEndDate)

            // Filter by price range
            val matchPrice =
                (selectedPriceRange == PriceRange.DEFAULT) || (selectedPriceRange == getPriceRange(
                    flight.price
                ))

            matchName && matchTime && matchPrice
        }
    }

    private fun timestampToCalendar(timestamp: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    private fun getPriceRange(price: Double): PriceRange {
        return when {
            price < 200.0 -> PriceRange.UNDER_200
            price in 200.0..500.0 -> PriceRange.BETWEEN_200_AND_500
            else -> PriceRange.OVER_500
        }
    }

    fun clearPriceRangeFilter() {
        selectedPriceRange = PriceRange.DEFAULT
        filterFlights()
    }

    fun clearAirlineFilter() {
        selectedAirline = ""
        filterFlights()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
            ): T {
                return FlightViewModel() as T
            }
        }
    }
}