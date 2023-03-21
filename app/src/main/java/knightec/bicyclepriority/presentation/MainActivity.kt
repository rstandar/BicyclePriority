package knightec.bicyclepriority.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.*
import knightec.bicyclepriority.presentation.repository.LocationDetails
import knightec.bicyclepriority.presentation.repository.LocationService
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.utilities.SoundPlayer
import knightec.bicyclepriority.presentation.utilities.Vibrations
import knightec.bicyclepriority.presentation.view.LocationView
import knightec.bicyclepriority.presentation.view.TrafficLightView
import knightec.bicyclepriority.presentation.viewmodel.LocationViewModel
import knightec.bicyclepriority.presentation.viewmodel.TrafficLightViewModel


class MainActivity : ComponentActivity(){

    private lateinit var locationView : LocationView
    private lateinit var trafficLightView : TrafficLightView
    private lateinit var locationReceiver: LocationReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trafficLightViewModel = TrafficLightViewModel(this.application)
        val soundPlayer = SoundPlayer(this)

        locationReceiver = LocationReceiver(LocationDetails("0","0","0"))
        registerReceiver(locationReceiver, IntentFilter("GET_CURRENT_LOCATION"))

        trafficLightView = TrafficLightView(trafficLightViewModel)
        getLocationPermissions()
        createLocationView()

        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }

        val vibrations = Vibrations(this)

        var vibrating : Boolean= false;

        setContent {
            BicyclePriorityTheme {
                val listState = rememberScalingLazyListState()
                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                    state = listState,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment= Alignment.CenterHorizontally
                ) {
                    item{ locationView.GPS(locationReceiver.getLocation()) }
                    /*item{ trafficLightView.viewTrafficLight() }
                    item{ Button(
                        onClick = {
                            vibrating = !vibrating
                            if(vibrating){
                                vibrations.increasingVibration()
                            }else {
                                vibrations.stopVibration()
                            }
                        }
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Start/stop vibrating",
                        )
                    }}
                    item{
                        Button(onClick = {
                            soundPlayer.testSound()
                        }) {
                            Text(text = "Play sound")
                        }
                    }*/
                }
            }
        }

    }


    private fun createLocationView() {
        if (locationIsEnabled()) {
            val locationViewModel = LocationViewModel(this.application)
            locationView = LocationView(locationViewModel)
        }
        else {
            Toast.makeText(
                this,
                "Location must be enabled for application to work",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            createLocationView()
        }
    }


    /** Method for checking user permissions, if permissions are not granted this method launch permission settings for user.*/
    private fun getLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            ///requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            requestMultiplePermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION))
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    /** In-line function for requesting permission for locations from user.*/
    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "GPS unavailable", Toast.LENGTH_SHORT)
            }
        }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            requestResults ->
                if(requestResults.get(Manifest.permission.ACCESS_FINE_LOCATION) == false){
                    Toast.makeText(this,"Location permissions needed for application to operate.",Toast.LENGTH_LONG).show()
                    getLocationPermissions()
                } else Toast.makeText(this,"Permissions granted.",Toast.LENGTH_SHORT).show()
        }

    /** Method for checking if location services are enabled on device. Return boolean value depending on result.*/
    private fun locationIsEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    internal class LocationReceiver (var locationDetails: LocationDetails) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "GET_CURRENT_LOCATION") {
                val lat = intent.getStringExtra("CURRENT_LOCATION_LAT")
                val long = intent.getStringExtra("CURRENT_LOCATION_LONG")
                val speed = intent.getStringExtra("CURRENT_LOCATION_SPEED")
                if(lat != null && long != null && speed != null) {
                    locationDetails = LocationDetails(lat, long, speed)
                }
            }
        }

        fun getLocation() : LocationDetails {
            return locationDetails
        }
    }

}








