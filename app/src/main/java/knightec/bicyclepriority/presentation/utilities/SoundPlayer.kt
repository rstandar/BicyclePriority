package knightec.bicyclepriority.presentation.utilities

import android.content.Context
import android.media.MediaPlayer
import knightec.bicyclepriority.R

class SoundPlayer(private val context: Context) {

    fun testSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.slowdown)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

}