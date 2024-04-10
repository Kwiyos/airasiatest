package com.example.airasiatest.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import com.example.airasiatest.ui.theme.AirAsiaTestTheme
import com.example.airasiatest.viewmodel.FlightViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    private val viewModel: FlightViewModel by viewModels { FlightViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AirAsiaTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
        setupObservable()
    }

    private fun setupObservable() {
        viewModel.statusMessage.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.launchDetailActivity.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                val intent = Intent(this, FlightDetailActivity::class.java)
                intent.putExtra("flight", it)
                this.startActivity(intent)
            }
        })
    }
}