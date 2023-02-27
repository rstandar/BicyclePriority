/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package knightec.bicyclepriority.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
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
import knightec.bicyclepriority.presentation.view.LocationView
import knightec.bicyclepriority.presentation.view.TrafficLightView
import knightec.bicyclepriority.presentation.viewmodel.MainActivityViewModel
import knightec.bicyclepriority.presentation.viewmodel.TrafficLightViewModel
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var locationView : LocationView
    private lateinit var trafficLightView : TrafficLightView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trafficLightViewModel = TrafficLightViewModel(this.application)
        trafficLightView = TrafficLightView(trafficLightViewModel)
        //prepLocationUpdates()
        //createLocationView()
        setContent {
            //locationView.GPS()
            trafficLightView.viewTrafficLight()
        }

    }

    private fun createLocationView() {
        if (locationIsEnabled()) {
            val mainActivityViewModel : MainActivityViewModel = MainActivityViewModel(this.application)
            locationView = LocationView(mainActivityViewModel)
        }
        else {
            Toast.makeText(
                this,
                "Location must be enabled for application to work",
                Toast.LENGTH_LONG
            )
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            createLocationView()
        }
    }


    /** Method for checking user permissions, if permissions are not granted this method launch permission settings for user.*/
    private fun prepLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /** In-line function for requesting permission for locations from user.*/
    private val requestSinglePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "GPS unavailable", Toast.LENGTH_SHORT)
            }
        }


    /** Method for checking if location services are enabled on device. Return boolean value depending on result.*/
    private fun locationIsEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}








