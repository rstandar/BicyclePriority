package knightec.bicyclepriority.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LocationViewModel (application: Application) : AndroidViewModel(application) {
    private val locationData = LocationData(application)
    fun getLocationData() = locationData
    fun startLocationUpdates(){
        locationData.startLocationUpdates()
    }

}