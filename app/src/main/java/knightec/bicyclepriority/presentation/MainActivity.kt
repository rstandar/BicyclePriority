package knightec.bicyclepriority.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.*
import knightec.bicyclepriority.presentation.repository.LocationDetails
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.utilities.SoundPlayer
import knightec.bicyclepriority.presentation.view.HomeScreenView
import knightec.bicyclepriority.presentation.view.LocationView
import knightec.bicyclepriority.presentation.view.TrackingScreenView
import knightec.bicyclepriority.presentation.view.TrafficLightView
import knightec.bicyclepriority.presentation.viewmodel.TrafficLightViewModel


class MainActivity : ComponentActivity(){
    private lateinit var locationView : LocationView
    private lateinit var trafficLightView : TrafficLightView
    private lateinit var dataReceiver: DataReceiver
    private val locationDetailsState = mutableStateOf(LocationDetails("0","0","Locating"))
    private val statusState = mutableStateOf("")
    private val distanceState = mutableStateOf("")
    private lateinit var soundPlayer: SoundPlayer
    private val trackingOngoing = mutableStateOf(false)
    private val vibrationsEnabled = mutableStateOf(true)
    private val soundEnabled = mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            trackingOngoing.value = savedInstanceState.getBoolean("trackingOngoing")
            vibrationsEnabled.value = savedInstanceState.getBoolean("vibrationsEnabled")
            soundEnabled.value = savedInstanceState.getBoolean("soundEnabled")
        }

        val trafficLightViewModel = TrafficLightViewModel(this.application)
        val homeScreenView = HomeScreenView()
        val trackingScreenView = TrackingScreenView()
        soundPlayer = SoundPlayer(this)

        dataReceiver = DataReceiver()
        registerReceiver(dataReceiver, IntentFilter("GET_CURRENT_DATA"))

        trafficLightView = TrafficLightView(trafficLightViewModel)
        getLocationPermissions()
        locationView = LocationView()

        //val vibrations = Vibrations(this)

        //Testing view



        setContent {

            BicyclePriorityTheme {
                //val soundEnabled = remember{ mutableStateOf(true) }
                val setSoundEnabled = fun(soundBool: Boolean) {soundEnabled.value = soundBool}
                //val vibrationsEnabled = remember{ mutableStateOf(true) }
                val setVibrationEnabled = fun(vibrationBool: Boolean) {vibrationsEnabled.value = vibrationBool}
                //val trackingOngoing = remember { mutableStateOf(false)}
                val startTracking = fun(){trackingOngoing.value = true}
                val stopTracking = fun(){trackingOngoing.value = false}
                Scaffold(
                    timeText = { TimeText() }
                ) {
                    if(trackingOngoing.value){
                        trackingScreenView.TrackingScreen(
                            stopTracking = stopTracking,
                            location = locationDetailsState,
                            status = statusState,
                            distance = distanceState
                        )
                        Intent(applicationContext, MainService::class.java).apply {//Starts foreground service
                            action = MainService.ACTION_START
                            startService(this)
                        }
                    }else {
                        homeScreenView.HomeScreen(
                            soundEnabled = soundEnabled.value,
                            setSoundEnabled = setSoundEnabled,
                            vibrationsEnabled = vibrationsEnabled.value,
                            setVibrationEnabled = setVibrationEnabled,
                            startActivity = startTracking
                        )
                        Intent(applicationContext, MainService::class.java).apply {// Stops foreground service
                            action = MainService.ACTION_STOP
                            stopService(this)
                        }
                    }
                }
            }
        }
    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("trackingOngoing", trackingOngoing.value)
        savedInstanceState.putBoolean("vibrationsEnabled", vibrationsEnabled.value)
        savedInstanceState.putBoolean("soundEnabled", soundEnabled.value)
    }

    override fun onRestoreInstanceState( savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackingOngoing.value = savedInstanceState.getBoolean("trackingOngoing")
        vibrationsEnabled.value = savedInstanceState.getBoolean("vibrationsEnabled")
        soundEnabled.value = savedInstanceState.getBoolean("soundEnabled")
    }

    /*
    override fun onPause() {
        unregisterReceiver(dataReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

    }
    */


    /** Method for checking user permissions, if permissions are not granted this method launch permission settings for user.*/
    private fun getLocationPermissions() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            requestMultiplePermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION))
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


    inner class DataReceiver () : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "GET_CURRENT_DATA") {
                val lat = intent.getStringExtra("CURRENT_LOCATION_LAT")
                val long = intent.getStringExtra("CURRENT_LOCATION_LONG")
                val speed = intent.getStringExtra("CURRENT_LOCATION_SPEED")
                val status = intent.getStringExtra("CURRENT_STATUS")
                val distance = intent.getStringExtra("CURRENT_DISTANCE")
                if(lat != null && long != null && speed != null) {
                    locationDetailsState.value = LocationDetails(lat, long, speed)
                }
                if(status != null && distance != null) {
                    statusState.value = status
                    distanceState.value = distance
                }
            }
        }
    }
}








