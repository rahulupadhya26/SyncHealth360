package com.app.synchealth.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.synchealth.R
import java.util.*

class SyncHealthService : Service() {

    var interval: Long = 1000 * 60 * 5
    var delay: Long = 5000
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    val TAG: String = "SyncHealthService"

    override fun onCreate() {
        super.onCreate()
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(resources.getString(R.string.app_name))
            .build()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer();
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
        stopTimerTask()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartBroadcastReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    companion object {
        var isServiceStarted = false
    }

    private fun startTimer() {
        isServiceStarted = true
        timer = Timer()
        //initialize the TimerTask's job
        initialiseTimerTask()
        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, delay, interval)
    }

    private fun initialiseTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                //Log.i(TAG, "Timer is running " + counter++)

            }
        }
    }

    private fun stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }
}