package knightec.bicyclepriority.presentation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import knightec.bicyclepriority.R
import knightec.bicyclepriority.presentation.repository.LocationClient
import knightec.bicyclepriority.presentation.repository.LocationDetails
import knightec.bicyclepriority.presentation.utilities.SoundPlayer
import knightec.bicyclepriority.presentation.utilities.Vibrations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject

class MainService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient : LocationClient
    private lateinit var queue : RequestQueue
    private lateinit var vibrations : Vibrations
    private lateinit var soundPlayer: SoundPlayer
    enum class States {ACCELERATING, DECELERATING, NEUTRAL }
    private var state : States = States.NEUTRAL
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClient(applicationContext, LocationServices.getFusedLocationProviderClient(applicationContext))
        queue = Volley.newRequestQueue(applicationContext)
        vibrations = Vibrations(applicationContext)
        soundPlayer = SoundPlayer(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location").setContentTitle("Tracking...").setContentText("Location = null").setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(1000L).catch { e -> e.printStackTrace() }.onEach {  location ->
            val lat = location.latitude.toString()
            val long = location.longitude.toString()
            val speed = location.speed.toString()
            val updatedNotification = notification.setContentText("Lat is ($lat), long is ($long) and speed is ($speed)")
            broadcastCurrentLocation(location)
            getTrafficLightInformation(location)
            notificationManager.notify(1, updatedNotification.build())
        }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private fun broadcastCurrentLocation (location : Location) {
        val sendCurrentLocation = Intent()
        sendCurrentLocation.action = "GET_CURRENT_LOCATION"
        sendCurrentLocation.putExtra("CURRENT_LOCATION_LAT",location.latitude.toString())
        sendCurrentLocation.putExtra("CURRENT_LOCATION_LONG",location.longitude.toString())
        sendCurrentLocation.putExtra("CURRENT_LOCATION_SPEED",location.speed.toString())
        sendBroadcast(sendCurrentLocation)
    }

    private fun decideAction (time: Double, distance: Double, status: String) {
        //TODO: add a timer to check if a action has been communicated to user recently
        if(distance>5f && distance < 250f){
            if(status=="green"){ //Green light
                if(time >= -15f && time<=2f){ //if cyclist will arrive at traffic light 15s after red light to 2 seconds before red light, increase speed to make sure they make it.
                    increaseSpeed()
                }
                else if(time <-15f){
                    decreaseSpeed()
                }
                else{
                    if(state != States.NEUTRAL) {
                        vibrations.stopVibration()
                        state = States.NEUTRAL
                    }
                }
            }
            else{ //Red light
                if(time <= -3f && time >= -15f){
                    if(state != States.NEUTRAL) {
                        vibrations.stopVibration()
                        state = States.NEUTRAL
                    }
                }
                else if(time > -3f){
                    decreaseSpeed()
                }
                else{ //Cyclist will arrive too late to the traffic light.
                    increaseSpeed()
                }
            }
        }
        else{
            if(state != States.NEUTRAL) {
                vibrations.stopVibration()
                state = States.NEUTRAL
            }
        }
    }

    private fun increaseSpeed(){
        //TODO: Give user indications to increase speed
        if(state != States.ACCELERATING){
            state = States.ACCELERATING
            //TODO Play sound once
            //TODO Start repeating vibration pattern for increasing speed
        }
    }

    private fun decreaseSpeed(){
        //TODO: Give user indications to decrease speed
        if(state != States.DECELERATING){
            state = States.DECELERATING
            //TODO Play sound once
            //TODO Start repeating vibration pattern for decreasing speed
        }
    }


    private fun getTrafficLightInformation(location: Location){
        val url = "https://tubei213og.execute-api.eu-north-1.amazonaws.com/"

        val reqBody = JSONObject()
        reqBody.put("lat",location.latitude.toString())
        reqBody.put("lon",location.longitude.toString())


        val req = JsonObjectRequest(
            Request.Method.POST,url,reqBody,
            {
                response ->
                run {
                    val time_left = response["time_left"].toString()
                    val distance = response["distance"].toString()
                    val status = response["status"] as String
                    val speed = if(location.speed == 0.0f) 0.001f else location.speed
                    decideAction(time_left.toDouble()-(distance.toDouble()/speed), distance.toDouble(), status)
                }
            },
            {
                    error -> print("ERROR: $error")
            }
        )
        queue.add(req)
    }
}