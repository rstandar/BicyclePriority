package knightec.bicyclepriority.presentation.view

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import knightec.bicyclepriority.presentation.theme.BicyclePriorityTheme
import knightec.bicyclepriority.presentation.viewmodel.TrafficLightViewModel
import org.json.JSONObject

class TrafficLightView (viewModel: TrafficLightViewModel){
    private val trafficLightViewModel = viewModel

    @Composable
    fun viewTrafficLight(){
        val result = trafficLightViewModel.getTrafficLightStatus().observeAsState()

        BicyclePriorityTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment= Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    text = trafficLightViewModel.getText(result as State<JSONObject>)
                )
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
    }

}