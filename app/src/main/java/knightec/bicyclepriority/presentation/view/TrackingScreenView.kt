package knightec.bicyclepriority.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import knightec.bicyclepriority.presentation.repository.LocationDetails
import knightec.bicyclepriority.presentation.theme.wearColorPalette

class TrackingScreenView {
    @Composable
    fun TrackingScreen(stopTracking: () -> Unit, location : MutableState<LocationDetails>, status : MutableState<String>, distance : MutableState<String> ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment= Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TrackingInformation(speed = location.value.speed, status = status.value, distance = distance.value)
                    StopButton (stopTracking = stopTracking)
                }
            }
        }
    }
    @Composable
    fun TrackingScreenDebugMode(stopTracking: () -> Unit, location : MutableState<LocationDetails>, status : MutableState<String>, distance : MutableState<String>, timeToStatusChange: MutableState<String>, timeToArrival: MutableState<String>) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment= Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TrackingInformationDebug(speed = location.value.speed, status = status.value, distance = distance.value, timeToStatusChange=timeToStatusChange.value,timeToArrival=timeToArrival.value)
                    StopButton (stopTracking = stopTracking)
                }
            }
        }
    }

    @Composable
    private fun TrackingInformation (speed: String, status: String, distance: String) {
        if(speed == "Locating"){
            Card(
                onClick = { },
                backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                    backgroundImagePainter = ColorPainter(wearColorPalette.primary)
                ),
                contentColor = wearColorPalette.primary,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(0.dp, 20.dp),
                enabled = false
            ) {
                TrackingText(text = "Locating...")
            }
        } else {
            Card(
                onClick = { },
                backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                    backgroundImagePainter = ColorPainter(wearColorPalette.primary)
                ),
                contentColor = wearColorPalette.primary,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(0.dp, 20.dp),
                enabled = false
            ) {
                TrackingText(text = "Speed: "+(speed.toFloat()*3.6).toString().substringBefore(".")+ " km/h")
                TrackingText(text = "Status: $status")
                TrackingText(text = "Distance: $distance m")
            }
        }
    }

    @Composable
    private fun TrackingInformationDebug (speed: String, status: String, distance: String, timeToStatusChange: String, timeToArrival: String) {
        if(speed == "Locating"){
            Card(
                onClick = { },
                backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                    backgroundImagePainter = ColorPainter(wearColorPalette.primary)
                ),
                contentColor = wearColorPalette.primary,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(0.dp, 20.dp),
                enabled = false
            ) {
                TrackingText(text = "Locating...")
            }
        } else {
            Card(
                onClick = { },
                backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                    backgroundImagePainter = ColorPainter(wearColorPalette.primary)
                ),
                contentColor = wearColorPalette.primary,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(0.dp, 20.dp),
                enabled = false
            ) {
                TrackingText(text = "Speed: "+(speed.toFloat()*3.6).toString().substringBefore(".")+ " km/h")
                TrackingText(text = "Status: $status")
                TrackingText(text = "State change: $timeToStatusChange s")
                TrackingText(text = "Arrival time: $timeToArrival s")
                TrackingText(text = "Distance: $distance m")
            }
        }
    }

    @Composable
    private fun TrackingText(text : String){
        Text(
            color = wearColorPalette.onPrimary,
            text = text,
            fontSize = 12.sp
        )
    }

    @Composable
    private fun StopButton (stopTracking: () -> Unit) {
        OutlinedButton(
            modifier = Modifier
                .padding(20.dp, 1.dp)
                .height(30.dp)
                .fillMaxWidth(0.6f)
            ,
            shape = RoundedCornerShape(50),
            onClick = {stopTracking()},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),

        ){
            Text(
                color = wearColorPalette.onPrimary,
                text = "Stop",
                fontSize = 11.sp
            )
        }
    }
}

