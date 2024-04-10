package com.example.airasiatest.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (DateRangePickerState) -> Unit,
    startDate: Long?,
    endDate: Long?
) {
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDate,
        initialSelectedEndDateMillis = endDate
    )
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Box(modifier = Modifier.padding(vertical = 20.dp)) {
                Column {
                    DateRangePicker(
                        state = state,
                        dateFormatter = DatePickerFormatter("yy-MM-dd"),
                        dateValidator = dateValidator(),
                        showModeToggle = true,
                        modifier = Modifier
                            .height(500.dp)
                            .padding(bottom = 20.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 16.dp)
                        ) {
                            Text(text = "Cancel", color = Color.White)
                        }
                        Button(
                            onClick = {
                                onConfirm(state)
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 16.dp)
                        ) {
                            Text(text = "Confirm", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

fun dateValidator(): (Long) -> Boolean {
    return { timeInMillis ->
        val endCalenderDate = Calendar.getInstance()
        endCalenderDate.timeInMillis = timeInMillis
        endCalenderDate.set(Calendar.DATE, Calendar.DATE + 365)
        timeInMillis > Calendar.getInstance().timeInMillis && timeInMillis < endCalenderDate.timeInMillis
    }
}