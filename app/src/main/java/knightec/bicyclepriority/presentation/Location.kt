package knightec.bicyclepriority.presentation

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
class Location {
    private lateinit var fusedLocationProvider : FusedLocationProviderClient
    private lateinit var latitude : TextView
    private lateinit var longitude : TextView
    private lateinit var currentLocation : Location



    /* Called to get location
        *
        */
    fun getLocation(ctx : Context){
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(ctx)
        if(checkPermissions(ctx)){
            if(locationIsEnabled())
        } else {
            requestPermission(ctx)
        }
    }

    private fun locationIsEnabled(): Boolean {
        val locationManager : LocationManager =
    }

    private fun requestPermission(ctx : Context) {
        ActivityCompat.requestPermissions(
            ctx as Activity, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    /* Checks if user has given permissions for usage of location.
    * Returns true if all necessary permissions allowed otherwise returns false.
    */
    private fun checkPermissions(ctx : Context) : Boolean {
        if(ActivityCompat.checkSelfPermission(ctx,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(ctx,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            return true
        }
        return false
    }


}



/*
var locationCallback = object : LocationCallback(){
    override fun onLocationResult(p0: LocationResult) {
        for(lo in p0.locations){
            currentLocation=LocationDetails
        }
    }
}
*/
