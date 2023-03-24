package knightec.bicyclepriority.presentation.view

import android.content.Context
import android.os.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat.getSystemService
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.viewmodel.TrafficLightViewModel
import org.json.JSONObject

class TrafficLightView (viewModel: TrafficLightViewModel){
    private val trafficLightViewModel = viewModel
    private var vibrating: Boolean = false;

    @Composable
    fun viewTrafficLight(){
        val result = trafficLightViewModel.getTrafficLightStatus().observeAsState()
        val trafficLightStatus = result.value?.let { trafficLightStatus -> getText(trafficLightStatus) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment= Alignment.CenterHorizontally
        ) {
            if (trafficLightStatus != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    text = trafficLightStatus
                )
            }
            Button(
                onClick = {
                    trafficLightViewModel.updatePoll()
                }
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Start/stop",
                )
            }
        }
    }

    fun getText(result : JSONObject) : String{
        val text =
            if (result.has("status") && result.has("time_left")) {
                "" + result.get("status") + " light\n" + result.get("time_left") + " seconds left"
            } else if (result.has("status") && !result.has("time_left")) {
                "Could not find time_left"
            } else if (!result.has("status") && result.has("time_left")) {
                "Could not find status"
            } else {
                "Could not find traffic light"
            }
        return text
    }

}