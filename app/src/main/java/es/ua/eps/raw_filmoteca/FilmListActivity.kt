package es.ua.eps.raw_filmoteca

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.ua.eps.raw_filmoteca.databinding.ActivityFilmListBinding

class FilmListActivity : AppCompatActivity(), FilmListFragment.OnItemSelectedListener{
    private lateinit var bindings: ActivityFilmListBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityFilmListBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)

            if (findViewById<View?>(R.id.fragment_container)  != null) {
                if (savedInstanceState != null) return

                val fragment = FilmListFragment()
                fragment.arguments = intent.extras
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        if (!hasNotificationPermission()) {
            requestNotificationPermission()
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableNotificationServices()
            }
        }
    }

    private fun enableNotificationServices() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }

    override fun onItemSelected(position: Int, listView: ListView) {
        Log.d("", "Selected: $position")

        val intent = Intent(this, FilmDataActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        intent.putExtra(FilmDataActivity.EXTRA_FILM_ID, position)
        startActivity(intent)
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
