package knightec.bicyclepriority.presentation.utilities

import android.content.Context
import android.media.MediaPlayer
import knightec.bicyclepriority.R

class SoundPlayer(private val context: Context) {

    fun slowDownSoundSwedish() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.lower_speed_swedish)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

    fun speedUpSoundSwedish() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.raise_speed_swedish)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.release()
        }

        mediaPlayer.start()

    }

    fun speedAchievedSoundSwedish() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.speed_achieved_swedish)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

    fun slowDownSoundEnglish() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.lower_speed_english)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

    fun speedUpSoundEnglish() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.raise_speed_english)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

    fun speedAchievedSoundEnglish() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.speed_achieved_english)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

    fun outOfZone() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.out_of_zone_effect)
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.release()
        }
        mediaPlayer.start()
    }

}