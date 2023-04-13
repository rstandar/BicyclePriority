package knightec.bicyclepriority.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import knightec.bicyclepriority.presentation.theme.wearColorPalette

class ActiveScreenView {
    @Composable
    fun ActiveScreen(stopTracking: () -> Unit) {
        ScalingLazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment= Alignment.CenterHorizontally
        ) {
            item { TrackingInformation(speed = "speed", status = "status", distance ="distance" ) }
            item {StopButton(stopTracking)}
        }
    }

    @Composable
    fun StopButton (stopTracking: () -> Unit) {
        OutlinedButton(
            modifier = Modifier
                .padding(20.dp, 1.dp)
                .height(40.dp)
                .fillMaxSize()
            ,
            shape = RoundedCornerShape(50),
            onClick = {stopTracking()},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ){
            Text(
                color = wearColorPalette.onPrimary,
                text = "Stop"
            )
        }
    }

    @Composable
    fun TrackingInformation (speed: String, status: String, distance: String) {
        Column() {
            Text(
                color = wearColorPalette.onPrimary,
                text = speed
            )
            Text(
                color = wearColorPalette.onPrimary,
                text = status
            )
            Text(
                color = wearColorPalette.onPrimary,
                text = distance
            )
        }
    }
}