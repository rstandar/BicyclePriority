package knightec.bicyclepriority.presentation.repository

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import knightec.bicyclepriority.R

class SoundPlayer(private val context: Context) {

    fun testSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.leeroyjenkins)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
        Toast.makeText(context, "Sound played", Toast.LENGTH_SHORT).show()
    }

}