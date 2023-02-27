package knightec.bicyclepriority.presentation.view

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.viewmodel.MainActivityViewModel

class LocationView(viewmodel: MainActivityViewModel) {

    /* Get the view model for locations and call method for checking/receiving permissions from user */
    private val mainActivityViewModel: MainActivityViewModel = viewmodel

    init {
        mainActivityViewModel.startLocationUpdates()
    }


    /** Component for displaying gps coordinates of the user.*/
    @Composable
    fun GPS() {
        //val location by mainActivityViewModel.getLocationData().observeAsState()

            BicyclePriorityTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    /*location?.let {
                        Text(text = location!!.latitude)
                        Text(text = location!!.longitude)
                    }*/
                    Text(text = "TEST")
                }
            }
        
    }
}