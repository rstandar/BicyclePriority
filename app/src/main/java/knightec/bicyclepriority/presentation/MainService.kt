package knightec.bicyclepriority.presentation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
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
import java.lang.Math.pow
import kotlin.math.pow

class MainService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient : LocationClient
    private lateinit var queue : RequestQueue
    private lateinit var vibrations : Vibrations
    private lateinit var soundPlayer: SoundPlayer
    private var curLocation = LocationDetails("0","0","0")
    private var prevLocation = LocationDetails("0","0","0")
    private val maxSpeed = 6.94 //In m/s, corresponds to approximately 25km/h
    private var englishSelected = true
    private var lastDistance : Double = 0.0
    enum class States {ACCELERATING, DECELERATING, NEUTRAL }
    private var state : States = States.NEUTRAL
    private val pollHandler = Handler(Looper.getMainLooper())
    private val poll = object: Runnable {
        override fun run(){
            getTrafficLightInformation()
            pollHandler.postDelayed(this, 500)
        }
    }
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
            ACTION_START -> {
                start()
                englishSelected = intent.getBooleanExtra("Eng",true)
            }
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location").setContentTitle("Application is running").setContentText("").setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
        startHttpRequests()
        startLocationUpdates()
        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        pollHandler.removeCallbacks(poll)
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private fun broadcastData (location : LocationDetails, status : String, distance : String, timeToStatusChange: String, timeToArrival: String) {
        val sendCurrentData = Intent()
        sendCurrentData.action = "GET_CURRENT_DATA"
        sendCurrentData.putExtra("CURRENT_LOCATION_LAT",location.latitude)
        sendCurrentData.putExtra("CURRENT_LOCATION_LONG",location.longitude)
        sendCurrentData.putExtra("CURRENT_LOCATION_SPEED",location.speed)
        sendCurrentData.putExtra("TIME_STATUS_CHANGE",timeToStatusChange) //Debug
        sendCurrentData.putExtra("TIME_ARRIVAL",timeToArrival) //Debug
        sendCurrentData.putExtra("CURRENT_STATUS",status)
        sendCurrentData.putExtra("CURRENT_DISTANCE",distance)

        sendBroadcast(sendCurrentData)
    }

    private fun getTrafficLightInformation(){
        val url = "https://tubei213og.execute-api.eu-north-1.amazonaws.com/"
        val reqBody = JSONObject()
        reqBody.put("lat", curLocation.latitude)
        reqBody.put("lon", curLocation.longitude)
        reqBody.put("prev_lat", prevLocation.latitude)
        reqBody.put("prev_lon", prevLocation.longitude)

        val req = JsonObjectRequest(
            Request.Method.POST, url, reqBody,
            { response ->
                run {
                    val timeLeft = response["time_left"].toString()
                    val distance = response["distance"].toString().substringBefore(".")
                    val status = response["status"] as String
                    val speed =
                        if (curLocation.speed.toDouble() == 0.0) 0.001 else curLocation.speed.toDouble() //Used to avoid div by zero error
                    broadcastData(curLocation, status, distance, timeLeft,(distance.toDouble() / speed).toString().substringBefore(".")) //Debug last two parameters
                    decideAction(
                        timeLeft.toDouble(),
                        (distance.toDouble() / speed),
                        distance.toDouble(),
                        status,
                        curLocation.speed.toDouble()
                    )
                }
            },
            { error ->
                print("ERROR: $error")
            }
        )
        queue.add(req)
    }

    private fun decideAction (timeToStatusChange: Double, timeToArrival: Double, distance: Double, status: String, speed: Double) {
        if(distance > lastDistance){
            vibrations.stopVibration()
            state = States.NEUTRAL
        }
        var distanceLimit : Double
        var accelerationConstant : Double
        if(speed>6f){ //In m/s, represent ~21.6km/h
            distanceLimit = 225.0
            accelerationConstant = 0.15
        }
        else if(speed > 4f) { //In m/s, represent ~14.4km/h which is just below Rasmus's average biking speed
            distanceLimit = 150.0
            accelerationConstant = 0.3
        }
        else {
            distanceLimit = 75.0
            accelerationConstant = 0.4
        }
        if(distance>0.0 && distance < distanceLimit) {
            if(status == "green") { //Green light
                if (timeToStatusChange - timeToArrival < -15.0 || timeToStatusChange - timeToArrival > 0.5) {
                    if (state != States.NEUTRAL) {
                        vibrations.stopVibration()
                        speedAchievedSound()
                        state = States.NEUTRAL
                    }
                }
                else if(distance-distanceCalculationAccelerationCapped(timeToStatusChange, accelerationConstant,speed,maxSpeed) < 0){
                    increaseSpeed()
                }
                else {
                    decreaseSpeed()
                }
            }
            else { //Red light
                if(timeToStatusChange - timeToArrival > -15.0 && timeToStatusChange - timeToArrival < -0.5){
                    if (state != States.NEUTRAL) {
                        vibrations.stopVibration()
                        speedAchievedSound()
                        state = States.NEUTRAL
                    }
                }
                else if(timeToStatusChange - timeToArrival >= -0.5){ //Cyclist will arrive 0.5s after the light switches to green or earlier. Decrease speed to
                    decreaseSpeed()
                }
                else if(distance-distanceCalculationAccelerationCapped(timeToStatusChange+15, accelerationConstant,speed,maxSpeed) < 0 && timeToStatusChange - timeToArrival <= -15.0){
                    increaseSpeed()
                }
                else {
                    if (state != States.NEUTRAL) {
                        vibrations.stopVibration()
                        speedAchievedSound()
                        state = States.NEUTRAL
                    }
                }
            }
        }
        else {
            if(state != States.NEUTRAL) {
                if(state == States.DECELERATING) {
                    speedAchievedSound()
                }
                vibrations.stopVibration()
                state = States.NEUTRAL
            }
        }

        lastDistance = distance
    }

    private fun increaseSpeed(){
        if(state != States.ACCELERATING){
            state = States.ACCELERATING
            if(englishSelected) {
                soundPlayer.speedUpSoundEnglish()
            }
            else{
                soundPlayer.speedUpSoundSwedish()
            }
            vibrations.increaseSpeedRepeating() //Start vibration pattern, will repeat until terminated
        }
    }

    private fun decreaseSpeed(){
        if(state != States.DECELERATING){
            state = States.DECELERATING
            if(englishSelected) {
                soundPlayer.slowDownSoundEnglish()
            }
            else{
                soundPlayer.slowDownSoundSwedish()
            }
            vibrations.decreaseSpeedRepeating() //Start vibration pattern, will repeat until terminated
        }
    }

    private fun speedAchievedSound(){
        if(englishSelected) {
            soundPlayer.speedAchievedSoundEnglish()
        }
        else{
            soundPlayer.speedAchievedSoundSwedish()
        }
    }



    /**
     * Takes two location details as parameter, and returns the distance as a float as result
     * */
    private fun calculateDistance(location1: LocationDetails, location2: LocationDetails): Float{
        var res = FloatArray(2)
        Location.distanceBetween(location1.latitude.toDouble(),location1.longitude.toDouble(),location2.latitude.toDouble(),location2.longitude.toDouble(),res)
        return res[0]
    }

    private fun startHttpRequests(){
        pollHandler.post(poll)
    }

    private fun startLocationUpdates(){
        locationClient.getLocationUpdates(500).catch { e -> e.printStackTrace() }.onEach {  location ->
            val lat = location.latitude.toString()
            val long = location.longitude.toString()
            val speed = location.speed.toString()
            val tmpCurLocation = LocationDetails(lat,long,speed)

            curLocation.speed = speed
            var dist = calculateDistance(tmpCurLocation,prevLocation)
            if(dist > 1.5f) {
                prevLocation = curLocation
                curLocation = tmpCurLocation
            }
        }.launchIn(serviceScope)
    }

    private fun distanceCalculationAccelerationCapped(timeToStatusChange : Double, accelerationConstant: Double, speed: Double, maxSpeed: Double) : Double{
        var distance : Double
        if(speed+(accelerationConstant*timeToStatusChange) < maxSpeed){
            distance = speed*timeToStatusChange+(accelerationConstant*timeToStatusChange.pow(2.0))/2.0
        }
        else{
            val timeBreakPoint = ((maxSpeed - speed)/accelerationConstant)
            distance = speed*timeBreakPoint+(accelerationConstant*timeBreakPoint.pow(2.0))/2.0
            distance += maxSpeed * (timeToStatusChange - timeBreakPoint)
        }
        return distance
    }


}