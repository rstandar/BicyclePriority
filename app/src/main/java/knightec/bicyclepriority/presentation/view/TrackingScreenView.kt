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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import knightec.bicyclepriority.presentation.theme.wearColorPalette

class TrackingScreenView {
    @Composable
    fun TrackingScreen(stopTracking: () -> Unit) {
        ScalingLazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment= Alignment.CenterHorizontally
        ) {
            item { TrackingInformation(speed = "speed", status = "status", distance ="distance" ) }
            //item {StopButton(stopTracking)} //TODO: Remove
        }
    }

    @Composable
    private fun StopButton (stopTracking: () -> Unit) {
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
    private fun TrackingInformation (speed: String, status: String, distance: String) {

        Card(
            onClick = {  },
            backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
                backgroundImagePainter = ColorPainter(wearColorPalette.primary)
            ),
            contentColor = wearColorPalette.primary,
            modifier = Modifier.width(130.dp)
        ) {
            Text(
                color = wearColorPalette.onPrimary,
                text = speed,
                fontSize = 10.sp
            )
            Text(
                color = wearColorPalette.onPrimary,
                text = status,
                fontSize = 10.sp
            )
            Text(
                color = wearColorPalette.onPrimary,
                text = distance,
                fontSize = 10.sp
            )
        }
    }
}

