package knightec.bicyclepriority.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.MaterialTheme

import knightec.bicyclepriority.presentation.theme.wearColorPalette

class HomeScreenView {

    @Composable
    fun HomeScreen (soundEnabled: Boolean, setSoundEnabled: (Boolean) -> Unit, vibrationsEnabled: Boolean, setVibrationEnabled: (Boolean) -> Unit, startActivity: ()->Unit) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment= Alignment.CenterHorizontally
        ) {
            item { StartButton(text = "Start",startActivity) }
            item { HomeScreenToggleSwitch(soundEnabled,setSoundEnabled,"Sound") }
            item { HomeScreenToggleSwitch(vibrationsEnabled,setVibrationEnabled,"Vibrations") }
        }
    }


    @Composable
    private fun StartButton(text : String, startActivity: () -> Unit){
        OutlinedButton(
            modifier = Modifier
                .padding(20.dp, 1.dp)
                .height(40.dp)
                .fillMaxSize()
            ,
            shape = RoundedCornerShape(50),
            onClick = {startActivity()},
            colors = ButtonDefaults.buttonColors(
                backgroundColor = wearColorPalette.primary
            )
        ){
            Text(
                color = wearColorPalette.onPrimary,
                text = text
            )
        }
    }

    @Composable
    private fun HomeScreenToggleSwitch (status : Boolean, updateStatus: (Boolean)-> Unit, text: String){
        ToggleChip(
            checked = status,
            onCheckedChange = {updateStatus(it)},
            modifier = Modifier
                .padding(20.dp, 1.dp)
                .height(40.dp)
                .fillMaxSize(),
            label = {
                Text(
                    color = wearColorPalette.onPrimary,
                    text = text,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = ToggleChipDefaults.toggleChipColors(
                checkedStartBackgroundColor = wearColorPalette.primary,
                checkedEndBackgroundColor = wearColorPalette.primary,
                uncheckedStartBackgroundColor = wearColorPalette.primaryVariant,
                uncheckedEndBackgroundColor = wearColorPalette.primaryVariant
                ),
            toggleControl = {
                Switch(
                    checked = status,
                    onCheckedChange = {updateStatus(it)},
                    enabled = true,
                    colors = SwitchDefaults.colors(
                        uncheckedTrackColor = wearColorPalette.primary
                    )
                )
            }
        )
    }
}


