package knightec.bicyclepriority.presentation.utilities

import android.content.Context
import android.media.MediaPlayer
import knightec.bicyclepriority.R

class SoundPlayer(private val context: Context) {

    val beepPlayer = MediaPlayer.create(context, R.raw.beep)
    fun beepSound() {
        beepPlayer.start()
    }

    fun slowDownSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.decrease_your_speed_male)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

    fun speedUpSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.increase_your_speed_male)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

    fun speedAchievedSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.cancel_acceleration)
        mediaPlayer.setOnCompletionListener {
            @Override
            fun onCompletion(mediaPlayer : MediaPlayer) {
                mediaPlayer.release()
            }
        }
        mediaPlayer.start()
    }

}