package knightec.bicyclepriority.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import knightec.bicyclepriority.presentation.repository.LocationData_DEPRECATED


/**Deprecated class, replaced with LocationService and LocationClient */
class LocationViewModel_DEPRECATED(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationData_DEPRECATED(application)

    private var GPSOn = false

    /** Used to get location data after location updates has been started */
    fun getLocationData() = locationData

    /**Used to start polling user location. After this method has been called, location data will
     * available by calling and observing result of method "getLocationData" */
    fun startLocationUpdates(){
        locationData.startLocationUpdates()
        GPSOn = true
    }

    fun startLocationService() {

    }


    fun toggleGPS() {
        if(GPSOn) locationData.stopLocationUpdates()
        else locationData.startLocationUpdates()
        GPSOn = !GPSOn
    }

}