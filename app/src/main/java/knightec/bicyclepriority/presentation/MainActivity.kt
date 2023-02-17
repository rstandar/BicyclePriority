/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package knightec.bicyclepriority.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.audiofx.Equalizer.Settings
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import knightec.bicyclepriority.R
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationViewModel : LocationViewModel = LocationViewModel(this.application)
        locationViewModel.startLocationUpdates()

        setContent {
            //viewTrafficLight()
            GPS(locationViewModel)
        }
    }



    @Composable
    private fun GPS(locationViewModel : LocationViewModel) {
        val location by locationViewModel.getLocationData().observeAsState()
        print(location)
        BicyclePriorityTheme {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment=Alignment.CenterHorizontally) {
                location?.let{
                    Text(text = "latitude")
                    Text(text = "longitude")
                }
            }
        }

    }

    /*

    private fun getLocation(){
        //todo integrate with locationData
        if(locationIsEnabled()){
            if (ActivityCompat.checkSelfPermission(
                    this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission(this)
                } else{
                    /*
                    fusedLocationProvider.lastLocation.addOnCompleteListener(this){
                        task->
                        val location : android.location.Location ?= task.result
                        if (location == null){
                            Toast.makeText(this,"Null received",Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(this,"Get success",Toast.LENGTH_SHORT).show()
                            latitude = ""+location.latitude
                            longitude = ""+location.longitude
                        }
                    }*/
                }
            } else{
                Toast.makeText(this,"Turn on location",Toast.LENGTH_SHORT).show()
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                getLocation()
            }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Granted",Toast.LENGTH_SHORT).show()
                getLocation()
            } else{
                Toast.makeText(applicationContext,"Denied",Toast.LENGTH_SHORT).show()
                getLocation()
            }
        }
    }
    private fun locationIsEnabled(): Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission(ctx : Context) {
        ActivityCompat.requestPermissions(
            ctx as Activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION)

    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
*/

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

