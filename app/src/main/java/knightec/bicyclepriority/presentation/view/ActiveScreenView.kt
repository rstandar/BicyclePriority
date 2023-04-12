package knightec.bicyclepriority.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text

class ActiveScreenView {
    @Composable
    fun ActiveScreen(stopActivity: () -> Unit) {
        Column() {
            Text(text = "Activity ongoing")
            Button(onClick = stopActivity) {
                Text(text = "Stop")
            }
        }
    }
}