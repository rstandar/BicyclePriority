package knightec.bicyclepriority.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import knightec.bicyclepriority.presentation.repository.LocationData

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationData(application)

    /** Used to get location data after location updates has been started */
    fun getLocationData() = locationData

    /**Used to start polling user location. After this method has been called, location data will
     * available by calling and observing result of method "getLocationData" */
    fun startLocationUpdates(){
        locationData.startLocationUpdates()
    }

}