package knightec.bicyclepriority.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.location.*

class LocationData (var context : Context) : LiveData<LocationDetails>() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override fun onActive() {
        super.onActive()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context,"Permission not granted onActive",Toast.LENGTH_LONG).show()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location -> location.also {
                location ->
                setLocationData(location)
            }
        }
    }

    internal fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context,"StartLocationUpdates permission check failed",Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(context,"StartLocationUpdates started requesting locations",Toast.LENGTH_LONG).show()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun setLocationData(location: Location?) {
        location?.let {
            location ->
            value = LocationDetails(location.longitude.toString(), location.latitude.toString())
            Toast.makeText(context,"Location set",Toast.LENGTH_LONG).show()
        }
    }

    override fun onInactive() {
        super.onInactive()
        Toast.makeText(context,"Inactive activated",Toast.LENGTH_LONG).show()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if(locationResult == null) {return}
            for(location in locationResult.locations){
                setLocationData(location)
            }
        }
    }
    companion object {
        private const val UPDATE_INTERVAL : Long = 60000

        /*
        val locationRequest : LocationRequest =  LocationRequest.create().apply {
            interval = ONE_MINUTE
            fastestInterval = ONE_MINUTE/4
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } */

        val locationRequest : LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL).build()

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


 */ /*
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
            {}
        } else {
            requestPermission(ctx)
        }
    }

    private fun locationIsEnabled(): Boolean {
        //val locationManager : LocationManager =
        return true;
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
*/
