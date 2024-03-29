package com.mycompany.advioo.services


import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.firebase.firestore.GeoPoint
import com.mycompany.advioo.R
import com.mycompany.advioo.models.campaign.LatLngPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationTrackingService : LifecycleService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        Log.d("location tracking service","service started")
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    /*
            speed < 10 -> 9_000L
            speed < 30 -> 7_700L
            speed < 60 -> 7_700L
            speed < 90 -> 5_700L
            else -> 3_000L
     */

    /*      BEST ONE SO FAR
                return when {
            speed < 10 -> 9_000L
            speed < 30 -> 5_000L
            speed < 60 -> 5_000L
            speed < 90 -> 5_000L
            else -> 3_000L
        }

     */


    private fun updateIntervalBasedOnSpeed(speed: Float): Long {
        return when {
            speed < 10 -> 9_000L
            speed < 30 -> 8_000L
            speed < 60 -> 8_000L
            speed < 90 -> 5_000L
            else -> 3_000L
        }
    }


    private fun isLocationWithinBorders(location: Location, mapBorderPoints: List<LatLngPoint>): Boolean {
        var coordinates = mapBorderPoints.map { Coordinate(it.longitude, it.latitude) }.toTypedArray()
        // Add the first point to the end of the list to close the polygon.
        coordinates += coordinates.first()

        val geometryFactory = GeometryFactory()
        val polygon = geometryFactory.createPolygon(coordinates)

        val userLocation = geometryFactory.createPoint(Coordinate(location.longitude, location.latitude))

        return polygon.contains(userLocation)
    }




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> {
                val mapBorderLocationList = intent.getParcelableArrayListExtra<LatLngPoint>(EXTRA_LOCATION_LIST)
                if (mapBorderLocationList != null) {
                    start(mapBorderLocationList)
                } else {
                    // handle error, list is null
                }
            }
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun start(mapBorderLocationList: ArrayList<LatLngPoint>) {
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

        var lastUpdateTime = System.currentTimeMillis()

        locationClient
            .getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val currentTime = System.currentTimeMillis()
                val timeSinceLastUpdate = currentTime - lastUpdateTime
                val updateInterval = updateIntervalBasedOnSpeed(location.speed)
                val speedKmh = location.speed * 3.6f // Convert from m/s to km/h
                println("Current speed: $speedKmh KM/h")
                tripData.speed = speedKmh
                val speedUpdateIntent = Intent(ACTION_SPEED_UPDATE)
                speedUpdateIntent.putExtra("currentSpeed", speedKmh)
                LocalBroadcastManager.getInstance(this).sendBroadcast(speedUpdateIntent)

                if (timeSinceLastUpdate >= updateInterval && speedKmh >= 7) {
                    if (isLocationWithinBorders(location, mapBorderLocationList)) {
                        // Update distance and lastUpdateTime
                        isOutOfBounds.postValue(false)
                        //val distance = locationClient.previousLocation?.distanceTo(location)?.div(1000.0)
                        val distance = haversineDistance(locationClient.previousLocation, location)
                        val newLocation = LatLngPoint(location.latitude, location.longitude)
                        tripData.locations.add(newLocation)
                        Log.d("location tracking service","get location updates")
                        userLocations.postValue(tripData.locations)
                        if(locationClient.previousLocation!=null){
                            //TODO: ADD THE LOCATION POINTS HERE
                        }
                        val geoPoint = GeoPoint(location.latitude, location.longitude)

                        if (distance != null) {
                            tripData.distanceDriven += distance
                            val currentKm = distanceDriven.value
                            val newKm = currentKm?.plus(distance)!!
                            distanceDriven.postValue(newKm)
                        }
                        locationClient.previousLocation = location
                        tripData.speed = speedKmh
                        val locationUpdateIntent = Intent(ACTION_LOCATION_UPDATE)
                        locationUpdateIntent.putExtra("tripData", tripData)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(locationUpdateIntent)
                        val updatedNotification = notification.setContentText(
                            "Distance driven: ${String.format("%.2f", tripData.distanceDriven)} KM"
                        )
                        notificationManager.notify(1, updatedNotification.build())

                        lastUpdateTime = currentTime
                    }
                    else{
                        println("out of bounds")
                        isOutOfBounds.postValue(true)
                    }
                }
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

    private fun haversineDistance(loc1: Location?, loc2: Location?): Double? {
        if (loc1 == null || loc2 == null) {
            return null
        }

        val R = 6371.0
        val dLat = Math.toRadians(loc2.latitude - loc1.latitude)
        val dLon = Math.toRadians(loc2.longitude - loc1.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(loc1.latitude)) * cos(Math.toRadians(loc2.latitude)) *
                sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }



    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_LOCATION_UPDATE = "ACTION_LOCATION_UPDATE"
        const val ACTION_SPEED_UPDATE = "ACTION_SPEED_UPDATE"
        const val EXTRA_LOCATION_LIST = "EXTRA_LOCATION_LIST"
        val userLocations = MutableLiveData<List<LatLngPoint>>(ArrayList())
        val distanceDriven = MutableLiveData<Double>(0.0)
        val isOutOfBounds = MutableLiveData<Boolean>(false)
    }

}