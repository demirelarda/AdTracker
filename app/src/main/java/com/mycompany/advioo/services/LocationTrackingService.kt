package com.mycompany.advioo.services


import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.firebase.firestore.GeoPoint
import com.mycompany.advioo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationTrackingService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "advioo:locationWakeLock")
        wakeLock.acquire()
        val tripData = TripData()
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking Location...")
            .setContentText("Distance: 0.0 KM")
            .setSmallIcon(R.drawable.car_icon_notification)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val distance = locationClient.previousLocation?.distanceTo(location)?.div(1000.0)
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                val currentTime = System.currentTimeMillis()

                if (distance != null) {
                    tripData.distanceDriven += distance
                }
                locationClient.previousLocation = location

                tripData.accuracyList.add(Pair(location.accuracy, geoPoint))
                tripData.locationPoints.add(geoPoint)
                tripData.distancePoints.add(Triple(geoPoint, tripData.distanceDriven, currentTime))

                if (tripData.startPoint == null) {
                    tripData.startPoint = geoPoint
                    tripData.startTime = currentTime
                }
                tripData.endPoint = geoPoint
                val locationUpdateIntent = Intent(ACTION_LOCATION_UPDATE)
                locationUpdateIntent.putExtra("tripData", tripData)
                LocalBroadcastManager.getInstance(this).sendBroadcast(locationUpdateIntent)
                val updatedNotification = notification.setContentText(
                    "Distance driven: ${String.format("%.2f", tripData.distanceDriven)} KM"
                )
                notificationManager.notify(1, updatedNotification.build())
            }

            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }


    private fun stop(){
        stopForeground(true)
        if (this::wakeLock.isInitialized && wakeLock.isHeld) {
            wakeLock.release()
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::wakeLock.isInitialized && wakeLock.isHeld) {
            wakeLock.release()
        }
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_LOCATION_UPDATE = "ACTION_LOCATION_UPDATE"
    }
}