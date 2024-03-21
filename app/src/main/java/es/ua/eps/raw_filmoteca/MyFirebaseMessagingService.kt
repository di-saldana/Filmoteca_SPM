package es.ua.eps.raw_filmoteca

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

interface MessageListener {
    fun onMessageReceived(message: RemoteMessage)
}

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object{
        lateinit var token: String
        private val listeners = mutableListOf<MessageListener>()

        fun registerListener(listener: MessageListener) {
            listeners.add(listener)
        }
        fun unregisterListener(listener: MessageListener) {
            listeners.remove(listener)
        }
    }

    override fun onNewToken(newToken: String) {
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        listeners.forEach { it.onMessageReceived(message) }

        message.notification?.let {
            getFirebaseMessage(it.title ?: "", it.body ?: "")
        }

    }

    private fun getFirebaseMessage(title: String, body: String) {
        val channelId = "notify"
        val notificationId = 102

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.filmoteca)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)

        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
