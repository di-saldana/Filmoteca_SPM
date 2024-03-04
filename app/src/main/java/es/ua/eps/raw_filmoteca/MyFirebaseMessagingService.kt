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
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import es.ua.eps.raw_filmoteca.data.Film
import es.ua.eps.raw_filmoteca.data.FilmDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var token: String? = null
    private val onNewTokenListeners: MutableList<(String) -> Unit> = mutableListOf()

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    fun askToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                token = task.result
                Log.d(TAG, "FCM registration Token: $token")
            })
    }

    override fun onNewToken(newToken: String) {
        token = newToken
        Log.d(TAG, "Refreshed token: $token")
        // Run on main thread
        Handler(Looper.getMainLooper()).post {
            for (func in onNewTokenListeners) {
                func(newToken)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            getFirebaseMessage(it.title ?: "", it.body ?: "")
        }

        val title = message.data["title"]
        val dir = message.data["dir"]
        val year = message.data["year"]
        val genreValue = message.data["genre"]?.toIntOrNull()
        val formatValue = message.data["format"]?.toIntOrNull()
        val imdb = message.data["imdb"]
        val comments = message.data["comments"]

        if (title != null && dir != null && year != null && genreValue != null && formatValue != null && imdb != null && comments != null) {
            val genre = Film.Genre.fromValue(genreValue)
            val format = Film.Format.fromValue(formatValue)

            if (genre != null && format != null) {
                addFilm(title, dir, year.toInt(), genre, format, imdb, comments)

                GlobalScope.launch(Dispatchers.Main) {
//                    FilmListActivity.reloadTable() // TODO
                }
            } else {
                Log.e(TAG, "Invalid genre or format value received")
            }
        } else {
            Log.e(TAG, "Missing or invalid data in FCM message")
        }
    }

    private fun addFilm(title: String, director: String, year: Int, genre: Film.Genre,
                        format: Film.Format, imdbUrl: String, comments: String) {
        val newFilm = Film()

        newFilm.title = title
        newFilm.director = director
        newFilm.year = year
        newFilm.genre = genre
        newFilm.format = format
        newFilm.imdbUrl = imdbUrl
        newFilm.comments = comments
        newFilm.imageResId = es.ua.eps.raw_filmoteca.R.drawable.filmoteca // TODO

        FilmDataSource.add(newFilm)
    }

    private fun getFirebaseMessage(title: String, body: String) {
        val channelId = "notify"
        val notificationId = 102

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.google.firebase.R.drawable.notification_icon_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false) // true

        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
