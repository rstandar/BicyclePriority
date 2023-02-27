package knightec.bicyclepriority.presentation.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.viewmodel.MainActivityViewModel

class LocationView(viewModel: MainActivityViewModel) {

    /* Get the view model for locations and call method for checking/receiving permissions from user */
    private val mainActivityViewModel: MainActivityViewModel = viewModel

    init {
        mainActivityViewModel.startLocationUpdates()
    }


    /** Component for displaying gps coordinates of the user.*/
    @Composable
    fun GPS() {
        val location by mainActivityViewModel.getLocationData().observeAsState()

        BicyclePriorityTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                location?.let {
                    Text(text = location!!.latitude)
                    Text(text = location!!.longitude)
                   }
            }
        }
        
    }
}