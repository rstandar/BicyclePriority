package knightec.bicyclepriority.presentation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import knightec.bicyclepriority.R
import knightec.bicyclepriority.presentation.repository.LocationClient
import knightec.bicyclepriority.presentation.utilities.SoundPlayer
import knightec.bicyclepriority.presentation.utilities.Vibrations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient : LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClient(applicationContext, LocationServices.getFusedLocationProviderClient(applicationContext))
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
        val soundPlayer = SoundPlayer(applicationContext)
        val vibrations = Vibrations(applicationContext)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(1000L).catch { e -> e.printStackTrace() }.onEach {  location ->
            val lat = location.latitude.toString()
            val long = location.longitude.toString()
            val speed = location.speed.toString()
            val updatedNotification = notification.setContentText("Lat is ($lat), long is ($long) and speed is ($speed)")
            broadcastCurrentLocation(location)
            //soundPlayer.beepSound()
            //vibrations.simpleVibration()
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

    private fun decideAction (time: Double, distance: Double, status: Boolean) {
        if(distance>5f && distance < 250f){
            if(status){
                //Green light
                if(time > -5f && time < 5f){
                    increaseSpeed()
                } else if(time <= -5f && time > -15f){
                    increaseSpeedALot()
                } else if(time <= -15f && time > -30f){
                    decreaseSpeed()
                } else if(time <= -30f){
                    decreaseSpeedALot()
                }
            } else{
                //Red light
                if(time >= 20f && time < 40f){
                    decreaseSpeedALot()
                } else if(time >= -1f && time > 20f){
                    decreaseSpeed()
                } else if(time >= -10f && time > -3f){
                    increaseSpeed()
                } else if(time >= -20f && time > -10f){
                    increaseSpeedALot()
                }
            }
        }
    }

    private fun increaseSpeed(){
        //TODO: Give user indications to increase speed
    }

    private fun increaseSpeedALot(){
        //TODO: Give user indications to increase speed a lot
    }

    private fun decreaseSpeed(){
        //TODO: Give user indications to decrease speed
    }

    private fun decreaseSpeedALot(){
        //TODO: Give user indications to decrease speed a lot
    }
}