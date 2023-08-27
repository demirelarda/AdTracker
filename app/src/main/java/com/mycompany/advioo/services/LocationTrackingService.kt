package com.mycompany.advioo.services


import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.PowerManager
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
import org.locationtech.jts.geom.PrecisionModel


class LocationTrackingService : LifecycleService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var borderList: ArrayList<LatLngPoint>

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    private fun updateIntervalBasedOnSpeed(speed: Float): Long {
        return when {
            speed < 10 -> 9_000L
            speed < 30 -> 7_700L
            speed < 60 -> 7_700L
            speed < 90 -> 5_700L
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
                        val distance = locationClient.previousLocation?.distanceTo(location)?.div(1000.0)
                        if(locationClient.previousLocation!=null){
                            val locationPointPair = Pair<Double,Double>(locationClient.previousLocation!!.latitude.toString().toDouble(),location.longitude.toString().toDouble())
                            val locationPairArray = ArrayList<Pair<Double,Double>>()
                            locationPairArray.add(locationPointPair)
                            roadPoints.postValue(locationPairArray)
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

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_LOCATION_UPDATE = "ACTION_LOCATION_UPDATE"
        const val ACTION_SPEED_UPDATE = "ACTION_SPEED_UPDATE"
        const val EXTRA_LOCATION_LIST = "EXTRA_LOCATION_LIST"
        val isTracking = MutableLiveData<Boolean>()
        val roadPoints = MutableLiveData<MutableList<Pair<Double,Double>>>()
        val distanceDriven = MutableLiveData<Double>(0.0)
        val isOutOfBounds = MutableLiveData<Boolean>(false)
    }

}