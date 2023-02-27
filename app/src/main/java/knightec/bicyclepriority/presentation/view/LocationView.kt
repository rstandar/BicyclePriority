package knightec.bicyclepriority.presentation.view

import android.Manifest
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

class LocationView(app: Application) : ComponentActivity() {

    /* Get the view model for locations and call method for checking/receiving permissions from user */
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private val appPriv : Application = app

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = MainActivityViewModel(appPriv)
        prepLocationUpdates()
    }
    init {

    }

    /** Method for checking user permissions, if permissions are not granted this method launch permission settings for user.*/
    private fun prepLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationUpdates()
        } else {
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /** In-line function for requesting permission for locations from user.*/
    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "GPS unavailable", Toast.LENGTH_SHORT)
            }
        }

    /** Method for starting location updates.*/
    private fun requestLocationUpdates() {
        mainActivityViewModel.startLocationUpdates()
    }

    /** Method for checking if location services are enabled on device. Return boolean value depending on result.*/
    private fun locationIsEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /** Component for displaying gps coordinates of the user.*/
    @Composable
    fun GPS() {
        val location by mainActivityViewModel.getLocationData().observeAsState()

        if (locationIsEnabled()) {
            BicyclePriorityTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    location?.let {
                        Text(text = location!!.latitude)
                        Text(text = location!!.longitude)
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                "Location must be enabled for application to work",
                Toast.LENGTH_LONG
            )
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            GPS()
        }
    }
}