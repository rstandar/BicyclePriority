package knightec.bicyclepriority.presentation.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*


/**Deprecated class, replaced with LocationClient and LocationService */
class LocationData_DEPRECATED (var context : Context) : MutableLiveData<LocationDetails>() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /*

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
            Toast.makeText(context,"Permission required for application to work",Toast.LENGTH_LONG).show()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            location : Location?->
                location.also {
                    location ->
                        setLocationData(location)
            }
        }
    } */

    /** Method used to activate location polling, given that required permissions are granted.*/
    internal fun startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context,"Permission required for application to work",Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(context, "Location requesting updates.",Toast.LENGTH_LONG).show()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location?->
            Toast.makeText(context,location?.toString(),Toast.LENGTH_LONG).show()
            location.also {
               location -> setLocationData(location)
            }
        }
    }

    internal fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Toast.makeText(context, "Location updates stopped.",Toast.LENGTH_LONG).show()
    }


    /** Method used to set the resulting location data, when this is activated observers are informed. */
    private fun setLocationData(location: Location?) {
        location?.let {
            location ->
            value = LocationDetails(location.longitude.toString(), location.latitude.toString(), location.speed.toString())
        }
    }

    /** When application become inactive this method cancels location updates. */
    /*
    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    */

    /** Setup of callback object used to construct location requests. This object is used to describe how to
     * handle results from locationUpdates. */
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if(locationResult == null) {return}
            for(location in locationResult.locations){
                setLocationData(location)
            }
        }
    }

    /** Companion object containing time interval for location updates: UPDATE_INTERVAL, by changing this const update frequency is updated.
     * Also contain a builder for setting up location requests. */
    companion object {
        private const val UPDATE_INTERVAL : Long = 1000
        val locationRequest : LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL).setMaxUpdates(Integer.MAX_VALUE).build()
    }
}



