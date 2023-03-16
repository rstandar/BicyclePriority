package knightec.bicyclepriority.presentation.utilities

import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class Vibrations (private val context: Context) {
    private var vibrator : Vibrator

    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // VibratorManager was only added on API level 31 release.
            val vibratorManager = this.context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            // backward compatibility for Android API < 31
            vibrator = this.context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun increasingVibration() {
        var timings: LongArray = longArrayOf(50, 50, 50, 50, 50, 50, 50)
        var amplitudes: IntArray = intArrayOf(0, 40, 80, 120, 160, 200, 240)
        var repeat = 1 // Repeat from the second entry, index = 1.
        val repeatingEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(repeatingEffect)
    }

    fun stopVibration () {
        vibrator.cancel()
    }
}