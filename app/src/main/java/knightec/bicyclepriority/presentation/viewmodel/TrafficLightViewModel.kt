package knightec.bicyclepriority.presentation.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import knightec.bicyclepriority.presentation.repository.TrafficLightHTTP
import org.json.JSONObject

class TrafficLightViewModel(application: Application) : AndroidViewModel(application) {
    val url = "https://rb09v6m375.execute-api.eu-north-1.amazonaws.com/default/isak-test-function"
    //val url = "https://5zuo7ssvj9.execute-api.eu-north-1.amazonaws.com/default/THESIS-bicyclePriority-trafficLights"

    private val trafficLight = TrafficLightHTTP(application.applicationContext)
    var result : MutableLiveData<JSONObject> = MutableLiveData()

    private val pollHandler = Handler(Looper.getMainLooper())
    private var running : Boolean = true;
    private val poll = object: Runnable {
        override fun run(){
            trafficLight.requestTrafficLightStatus(result, url)
            pollHandler.postDelayed(this, 1000)
        }
    }

    init{
        startPolling()
    }

    fun getTrafficLightStatus() = result

    fun updatePoll(){
        if(running) stopPolling()
        else startPolling()
        running = !running
    }

    private fun startPolling(){
        pollHandler.post(poll)
    }

    private fun stopPolling(){
        pollHandler.removeCallbacks(poll)
    }



}