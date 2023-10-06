package com.app.synchealth.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RestartBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        /*if (!Utils.isServiceRunning(context, SyncHealthService::class.java)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, SyncHealthService::class.java))
            } else {
                context.startService(Intent(context, SyncHealthService::class.java))
            }
        }*/
    }
}