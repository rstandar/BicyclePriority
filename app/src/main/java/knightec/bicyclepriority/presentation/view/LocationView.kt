package knightec.bicyclepriority.presentation.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import knightec.bicyclepriority.presentation.repository.LocationDetails


class LocationView() {
    /** Component for displaying gps coordinates of the user.*/
    @Composable
    fun GPS( locationDetails: MutableState<LocationDetails>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = locationDetails.value.latitude)
            Text(text = locationDetails.value.longitude)
            Text(text = locationDetails.value.speed)
        }
    }
}