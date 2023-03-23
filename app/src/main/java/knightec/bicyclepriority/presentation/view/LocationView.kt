package knightec.bicyclepriority.presentation.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import knightec.bicyclepriority.presentation.repository.LocationDetails
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.viewmodel.LocationViewModel

/*Earlier param: viewModel: LocationViewModel
* */
class LocationView() {

    /* Get the view model for locations and call method for checking/receiving permissions from user */
    //private val locationViewModel: LocationViewModel = viewModel
/*
    init {
        locationViewModel.startLocationUpdates()
    }
*/

    /** Component for displaying gps coordinates of the user.*/
    @Composable
    fun GPS( locationDetails: MutableState<LocationDetails>) {
        /* Not used with current implementation
        val location by locationViewModel.getLocationData().observeAsState()
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
                //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*
            location?.let {

                Text(text = location!!.latitude)
                Text(text = location!!.longitude)
                Text(text = location!!.speed)
                /* Button(onClick = { locationViewModel.toggleGPS() }) {
                    Text(text = "Start/Stop")
                } */
                Button(onClick = {
                    locationViewModel.startLocationService()
                }) {

                }
            }

             */
            Text(text = locationDetails.value.latitude)
            Text(text = locationDetails.value.longitude)
            Text(text = locationDetails.value.speed)
        }
        
    }
}