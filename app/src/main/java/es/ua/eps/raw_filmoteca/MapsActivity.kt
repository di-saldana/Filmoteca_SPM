package es.ua.eps.raw_filmoteca

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.ua.eps.raw_filmoteca.data.FilmDataSource
import es.ua.eps.raw_filmoteca.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    object AppSettings {
        var GEOFENCE_RADIUS_METERS = 500
    }
    companion object {
        const val CHANNEL_ID : String = "My Channel"
        const val NOTIFICATION_ID = 100
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var notification: Notification
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var index = -1
    private var lat = -34.0 // Sydney
    private var lon = 151.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapFragment.arguments = intent.extras

        index = intent.getIntExtra(FilmDataActivity.EXTRA_FILM_ID, -1)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateGeofenceRadius()
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationServices()
            }
        }
    }

    private fun enableLocationServices() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (!hasLocationPermission()) {
            requestLocationPermission()
        }

        if (index != -1) {
            val film = FilmDataSource.films[index]

            lat = film.lat
            lon = film.lon

            val movieLoc = LatLng(lat, lon)
            mMap.addMarker(MarkerOptions().position(movieLoc).title(film.title).snippet("${film.director}, ${film.year}"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(movieLoc, 14f))

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location : Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    if (isInsideGeofence(currentLatLng, movieLoc, AppSettings.GEOFENCE_RADIUS_METERS)) {
                        Log.d("Geofence", "Pelicula cercana")
                        makeNotification()
                    }
                }
            }

            Log.d("GEO SWITCH VALUE", film.geocercado.toString())
            if (film.geocercado) {
                val circleOptions = CircleOptions()
                    .center(movieLoc)
                    .radius(AppSettings.GEOFENCE_RADIUS_METERS.toDouble())
                    .strokeWidth(2f)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.argb(70, 0, 0, 255))
                mMap.addCircle(circleOptions)
            }
        } else {
            val loc = LatLng(lat, lon)
            mMap.addMarker(MarkerOptions().position(loc).title("Default Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
        }
    }

    private fun isInsideGeofence(userLocation: LatLng, filmLocation: LatLng, radius: Int): Boolean {
        val distance = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            filmLocation.latitude, filmLocation.longitude,
            distance
        )
        return distance[0] <= radius
    }

    private fun makeNotification() {
        val notificationManager : NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (index != -1) {
            val film = FilmDataSource.films[index]

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.filmoteca)
                    .setContentText(film.title)
                    .setSubText("Film Location Near")
                    .setChannelId(CHANNEL_ID)
                    .build()

                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        "My notification channel",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )
            } else {
                notification = Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.filmoteca)
                    .setContentText(film.title)
                    .setSubText("Film Location Near")
                    .build()
            }
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun updateGeofenceRadius() {
        val prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedRadius = prefs.getInt("radius", AppSettings.GEOFENCE_RADIUS_METERS)
        AppSettings.GEOFENCE_RADIUS_METERS = savedRadius
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                val intent = Intent(this, AddFilm::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }
            R.id.close -> {
                mGoogleSignInClient.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }
            R.id.disconnect -> {
                mGoogleSignInClient.revokeAccess()
                finish()
                finishAffinity()
                return true
            }
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }
            R.id.geo -> {
                val intent = Intent(this, SettingsGeofenceActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}