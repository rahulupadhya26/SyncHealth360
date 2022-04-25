package com.app.synchealth.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.util.Log
import com.app.synchealth.MainActivity
import com.app.synchealth.R
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**

     * Called when message is received.

     *

     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.

     */

    // [START receive_message]

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // [START_EXCLUDE]

        // There are two types of messages data messages and notification messages. Data messages are handled

        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type

        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app

        // is in the foreground. When the app is in the background an automatically generated notification is displayed.

        // When the user taps on the notification they are returned to the app. Messages containing both notification

        // and data payloads are treated as notification messages. The Firebase console always sends notification

        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

        // [END_EXCLUDE]



        // TODO(developer): Handle FCM messages here.

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        Log.d(TAG, "From: ${remoteMessage.from}")


        // Check if message contains a notification payload.

        remoteMessage.notification?.let {

            //Log.d(TAG, "Message Notification Body: ${it.body}")
            try {
                var imageUrl = it.imageUrl
                if (imageUrl == null)
                    imageUrl = Uri.EMPTY
                sendNotification(it.title!!,it.body!!,imageUrl.toString())
            }catch (e:Exception){
                e.printStackTrace()
            }


        }

        // Check if message contains a data payload.

        remoteMessage.data.let {
            try {
                sendNotification(it["title"]!!,it["body"]!!,it["imageUrl"]!!)
            }catch (e:Exception){
                e.printStackTrace()
            }


        }



        // Also if you intend on generating your own notifications as a result of a received FCM

        // message, here is where that should be initiated. See sendNotification method below.

    }

    // [END receive_message]



    // [START on_new_token]

    /**

     * Called if InstanceID token is updated. This may occur if the security of

     * the previous token had been compromised. Note that this is called when the InstanceID token

     * is initially generated so this is where you would retrieve the token.

     */

    override fun onNewToken(token: String) {

        //Log.d(TAG, "Refreshed token: $token")



        // If you want to send messages to this application instance or

        // manage this apps subscriptions on the server side, send the

        // Instance ID token to your app server.

        sendRegistrationToServer(token)

    }

    // [END on_new_token]



    /**

     * Schedule async work using WorkManager.

     */

//    private fun scheduleJob() {
//
//        // [START dispatch_job]
//
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//
//        WorkManager.getInstance().beginWith(work).enqueue()
//
//        // [END dispatch_job]
//
//    }



    /**

     * Handle time allotted to BroadcastReceivers.

     */

//    private fun handleNow() {
//
//        //Log.d(TAG, "Short lived task is done.")
//
//    }



    /**

     * Persist token to third-party servers.

     *

     * Modify this method to associate the user's FCM InstanceID token with any server-side account

     * maintained by your application.

     *

     * @param token The new token.

     */

    private fun sendRegistrationToServer(token: String?) {

        try {
            TokenTracker(token!!, applicationContext as MainActivity)
        }catch (e:Exception){}
    }



    /**

     * Create and show a simple notification containing the received FCM message.

     *

     * @param messageBody FCM message body received.

     */

    private fun sendNotification(messageTitle:String, messageBody: String,imageUrl:String?) {

        val intent = Intent(this, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,

                PendingIntent.FLAG_ONE_SHOT)



        val channelId = getString(R.string.app_name)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)

                .setSmallIcon(R.mipmap.ic_action_notifications_none)

                .setColor(ContextCompat.getColor(applicationContext, R.color.colorMaroon))
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)

                .setContentIntent(pendingIntent)


        if(imageUrl!!.isNotEmpty()) {
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .submit()

            val bitmap = futureTarget.get()
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle().bigPicture(bitmap)).priority = NotificationCompat.PRIORITY_HIGH
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        // Since android Oreo notification channel is needed.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(channelId,

                    "Channel human readable title",

                    NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)

        }



        notificationManager.notify(System.currentTimeMillis().toInt() /* ID of notification */, notificationBuilder.build())

    }



    companion object {
        private const val TAG = "MyFirebaseMsgService"

    }

}