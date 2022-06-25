package com.ashish.tailnodeassignment

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.ashish.tailnodeassignment.utils.getCurrentDateTime
import com.ashish.tailnodeassignment.utils.writeToFile
import com.google.android.gms.location.*

class LocationService : Service() {

    private lateinit var client: FusedLocationProviderClient
    private lateinit var request: LocationRequest
    private lateinit var timer: CountDownTimer
    var firsttime = true

    companion object {
        var location = MutableLiveData<Location>()
    }

    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(this)
        request = LocationRequest.create()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            createNotificationChanel()
        startLocationUpdates()


        timer = object : CountDownTimer(300000, 300000) {
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                writeToFile(getCurrentDateTime() + " :: Latitude : ${location.value?.latitude} Longitude : ${location.value?.longitude}")
                timer.start()
            }

        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID"
        val channelName = "CHANNEL_NAME"
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)

        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        startForeground(2, notification)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val mLocation = locationResult.lastLocation
            location.value = mLocation

            if (firsttime) {
                firsttime = false
                writeToFile(getCurrentDateTime() + " :: Latitude : ${location.value?.latitude} Longitude : ${location.value?.longitude}")
            }
        }
    }

    private fun startLocationUpdates() {
        request.interval = 10000
//        request?.fastestInterval = 1000
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, locationCallback, null)
        }
    }

    private fun stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        client?.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}