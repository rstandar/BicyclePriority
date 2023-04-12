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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.*
import knightec.bicyclepriority.presentation.repository.LocationDetails
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.utilities.SoundPlayer
import knightec.bicyclepriority.presentation.view.ActiveScreenView
import knightec.bicyclepriority.presentation.view.HomeScreenView
import knightec.bicyclepriority.presentation.view.LocationView
import knightec.bicyclepriority.presentation.view.TrafficLightView
import knightec.bicyclepriority.presentation.viewmodel.TrafficLightViewModel


class MainActivity : ComponentActivity(){
    private lateinit var locationView : LocationView
    private lateinit var trafficLightView : TrafficLightView
    private lateinit var locationReceiver: LocationReceiver
    private val locationDetails = mutableStateOf(LocationDetails("0","0","0"))
    private lateinit var soundPlayer: SoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val trafficLightViewModel = TrafficLightViewModel(this.application)
        val homeScreenView = HomeScreenView()
        val activeScreenView = ActiveScreenView()
        soundPlayer = SoundPlayer(this)

        locationReceiver = LocationReceiver()
        registerReceiver(locationReceiver, IntentFilter("GET_CURRENT_LOCATION"))

        trafficLightView = TrafficLightView(trafficLightViewModel)
        getLocationPermissions()
        locationView = LocationView()

        Intent(applicationContext, MainService::class.java).apply {
            action = MainService.ACTION_START
            startService(this)
        }



        //val vibrations = Vibrations(this)

        //Testing view



        setContent {

            BicyclePriorityTheme {
                val listState = rememberScalingLazyListState()
                val soundEnabled = remember{ mutableStateOf(true) }
                val setSoundEnabled = fun(soundBool: Boolean) {soundEnabled.value = soundBool}
                val vibrationsEnabled = remember{ mutableStateOf(true) }
                val setVibrationEnabled = fun(vibrationBool: Boolean) {vibrationsEnabled.value = vibrationBool}
                val activityOngoing = remember { mutableStateOf(true)} //TODO: come up with better name for field
                val startActivity = fun(){activityOngoing.value = true} //TODO: come up with better name for function
                val stopActivity = fun(){activityOngoing.value = false} //TODO: come up with better name for function

                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                    state = listState,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment= Alignment.CenterHorizontally
                ) {
                    if(activityOngoing.value){
                        item {
                            activeScreenView.ActiveScreen(
                                stopActivity = stopActivity
                            )
                        }
                    }else {
                        item {
                            homeScreenView.HomeScreen(
                                soundEnabled = soundEnabled.value,
                                setSoundEnabled = setSoundEnabled,
                                vibrationsEnabled = vibrationsEnabled.value,
                                setVibrationEnabled = setVibrationEnabled,
                                startActivity = startActivity
                            )
                        }
                    }
                    //item{ locationView.GPS(locationDetails) }

                }
            }
        }
    }


    override fun onPause() {
        unregisterReceiver(locationReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(locationReceiver, IntentFilter("GET_CURRENT_LOCATION"))
    }


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


    inner class LocationReceiver () : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "GET_CURRENT_LOCATION") {
                val lat = intent.getStringExtra("CURRENT_LOCATION_LAT")
                val long = intent.getStringExtra("CURRENT_LOCATION_LONG")
                val speed = intent.getStringExtra("CURRENT_LOCATION_SPEED")
                if(lat != null && long != null && speed != null) {
                    locationDetails.value = LocationDetails(lat, long, speed)
                }
            }
        }
    }
}








