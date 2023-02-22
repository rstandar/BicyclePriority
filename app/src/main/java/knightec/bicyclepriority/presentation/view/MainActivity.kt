/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package knightec.bicyclepriority.presentation.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.viewmodel.MainActivityViewModel
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /* Get the view model for locations and call method for checking/receiving permissions from user */
        mainActivityViewModel = MainActivityViewModel(this.application)
        prepLocationUpdates()

        setContent {
            //viewTrafficLight()
            GPS()
        }
    }

    /** Method for checking user permissions, if permissions are not granted this method launch permission settings for user.*/
    private fun prepLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates()
        } else{
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /** In-line function for requesting permission for locations from user.*/
    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted ->
        if(isGranted){
            requestLocationUpdates()
        } else {
            Toast.makeText(this,"GPS unavailable", Toast.LENGTH_SHORT)
        }
    }

    /** Method for starting location updates.*/
    private fun requestLocationUpdates(){
        mainActivityViewModel.startLocationUpdates()
    }

    /** Component for displaying gps coordinates of the user.*/
    @Composable
    private fun GPS() {
        val location by mainActivityViewModel.getLocationData().observeAsState()

        if(locationIsEnabled()){
            BicyclePriorityTheme {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment=Alignment.CenterHorizontally) {
                    location?.let{
                        Text(text = location!!.latitude)
                        Text(text = location!!.longitude)
                    }
                }
            }
        } else {
            Toast.makeText(this,"Location must be enabled for application to work",Toast.LENGTH_LONG)
            val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            GPS()
        }
    }

    /** Method for checking if location services are enabled on device. Return boolean value depending on result.*/
    private fun locationIsEnabled(): Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    //viewTrafficLight()
}


fun volleyJSONReq (ctx : Context, result: MutableState<JSONObject>, url: String){
    val queue = Volley.newRequestQueue(ctx)
    val req = JsonObjectRequest(Request.Method.GET, url, JSONObject(),
        {
                response -> result.value = response
        },
        {
                error -> print("ERROR: $error")
        }
    )
    queue.add(req)
}

@Composable
fun viewTrafficLight(){
    val context = LocalContext.current
    val result = remember {mutableStateOf(JSONObject())}
    //val url = "https://5zuo7ssvj9.execute-api.eu-north-1.amazonaws.com/default/THESIS-bicyclePriority-trafficLights"
    val url = "https://rb09v6m375.execute-api.eu-north-1.amazonaws.com/default/isak-test-function"
    val pollHandler = Handler(Looper.getMainLooper())
    var running : Boolean = true;

    val poll = object: Runnable {
        override fun run(){
            volleyJSONReq(context,result, url)
            pollHandler.postDelayed(this, 1000)
        }
    }

    pollHandler.post(poll)


    BicyclePriorityTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment=Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = getText(result)
            )
            Button(
                onClick = {
                    if(running) pollHandler.removeCallbacks(poll)
                    else pollHandler.post(poll)
                    running = !running
                }
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Start/stop",
                )
            }
        }
    }
}




fun getText(result: MutableState<JSONObject>) : String{
    val text =
        if(result.value.has("status") && result.value.has("time_left")){
            ""+result.value.get("status") + " light\n" + result.value.get("time_left") + " seconds left"
        } else if(result.value.has("status") && !result.value.has("time_left")){
            "Could not find time_left"
        } else if(!result.value.has("status") && result.value.has("time_left")){
            "Could not find status"
        } else{
            "Could not find traffic light"
        }
    return text
}

