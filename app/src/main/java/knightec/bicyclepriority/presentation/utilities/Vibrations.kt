package knightec.bicyclepriority.presentation.utilities

import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

class Vibrations (private val context: Context) {
    private var vibrator : Vibrator
    private val l: Long = 300 //Time long vibration
    private val s: Long = 100 //Time short vibration
    private val b: Long = 300 // Time break between vibrations
    private val sb: Long = 100 // Time short break between vibrations
    private val h: Int = 255 //Amplitude high
    
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
        var timings: LongArray = longArrayOf(50, 50, 50, 50, 50, 50, 50, 50)
        var amplitudes: IntArray = intArrayOf(0, 40, 80, 120, 160, 200, 240, 255)
        var repeat = 1 // Repeat from the second entry, index = 1.
        val repeatingEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(repeatingEffect)
    }

    fun vibration1(){
        var timings: LongArray = longArrayOf(s,sb,s,sb,s,sb,s,sb,s,sb,s,sb,s,sb)
        var amplitudes: IntArray = intArrayOf(h,0,h,0,h,0,h,0,h,0,h,0,h,0)
        var repeat = -1 // Play once
        val simpleEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(simpleEffect)
    }
   
    fun vibration2(){
        var timings: LongArray = longArrayOf(s, sb, s, sb, s, sb, l, sb, s, sb, s, sb, s, sb, l, sb, s, sb, s, sb, s, sb, l, sb)
        var amplitudes: IntArray = intArrayOf(h,0,h,0,h,0,h,0,h,0,h,0,h,0,h,0,h,0,h,0,h,0,h,0)
        var repeat = -1 // Play once
        val simpleEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(simpleEffect)
    }
    fun vibration3(){
        var timings: LongArray = longArrayOf(l,b,l,b,l,b,l,b,l,b)
        var amplitudes: IntArray = intArrayOf(h,0,h,0,h,0,h,0,h,0)
        var repeat = -1 // Play once
        val simpleEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(simpleEffect)
    }

    fun vibration4(){
        var timings: LongArray = longArrayOf(l,b,s,b,l,b,s,b,l,b,s,b)
        var amplitudes: IntArray = intArrayOf(h,0,h,0,h,0,h,0,h,0,h,0)
        var repeat = -1 // Play once
        val simpleEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(simpleEffect)
    }

    fun vibration5(){
        var timings: LongArray = longArrayOf(sb,s,s,s,s,s,s,s,s,s,s,s,s,s,sb)
        var amplitudes: IntArray = intArrayOf(0,h/6,2*h/6,3*h/6,4*h/6,5*h/6,h,h,h,5*h/6,4*h/6,3*h/6,2*h/6,h/6,0)
        var repeat = -1 // Play once
        val simpleEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(simpleEffect)
    }


    fun vibration6(){
        var timings: LongArray = longArrayOf(l, b, l, b-50, l, b-100, l, b-150, l, b-200, l, b-250, l)
        var amplitudes: IntArray = intArrayOf(h,0,h,0,h,0,h,0,h,0,h,0,h)
        var repeat = -1 // Play once
        val simpleEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(simpleEffect)
    }

    fun simpleVibration() {
        var timings: LongArray = longArrayOf(50, 200)
        var amplitudes: IntArray = intArrayOf(0,h)
        var repeat = -1 // Play once
        val simpleEffect : VibrationEffect = VibrationEffect.createWaveform(timings,amplitudes,repeat)
        vibrator.vibrate(simpleEffect)
    }

    fun stopVibration () {
        vibrator.cancel()
    }

    @Composable
    fun VibrationTest(){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { vibration1() }) {Text(text = "1")}
            Button(onClick = { vibration2() }) {Text(text = "2")}
            Button(onClick = { vibration3() }) {Text(text = "3")}
            Button(onClick = { vibration4() }) {Text(text = "4")}
            Button(onClick = { vibration5() }) {Text(text = "5")}
            Button(onClick = { vibration6() }) {Text(text = "6")}
        }
    }
}