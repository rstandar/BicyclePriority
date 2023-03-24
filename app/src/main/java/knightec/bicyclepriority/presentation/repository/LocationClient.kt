package knightec.bicyclepriority.presentation.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationClient(private val context: Context, private val client: FusedLocationProviderClient){
    private var currentLocation : LocationDetails = LocationDetails("0","0","0")
    fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context,"Permission required for application to work", Toast.LENGTH_LONG).show()
                throw LocationException("Missing permissions")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                throw LocationException("GPS is not available")
            }

            val request : com.google.android.gms.location.LocationRequest = com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                interval
            ).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(res: LocationResult) {
                    super.onLocationResult(res)
                    res.locations.lastOrNull()?.let { location ->
                        currentLocation = LocationDetails(location.latitude.toString(),location.longitude.toString(),location.speed.toString())
                        launch { send(location)}
                    }
                }
            }

            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }

    }

    class LocationException(message: String): Exception()

}

