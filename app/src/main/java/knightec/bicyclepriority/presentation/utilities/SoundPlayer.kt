package knightec.bicyclepriority.presentation.utilities

import android.content.Context
import android.media.MediaPlayer
import knightec.bicyclepriority.R

class SoundPlayer(private val context: Context) {

    fun slowDownSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.lower_speed_swedish)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

    fun speedUpSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.raise_speed_swedish
        )
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

    fun speedAchievedSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.speed_achieved_swedish)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

    fun outOfZone() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.speed_achieved)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

}