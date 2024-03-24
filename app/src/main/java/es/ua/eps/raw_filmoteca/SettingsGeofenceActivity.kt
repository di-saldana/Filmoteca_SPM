package es.ua.eps.raw_filmoteca

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.ua.eps.raw_filmoteca.databinding.ActivitySettingsGeofenceBinding


class SettingsGeofenceActivity : AppCompatActivity() {
    private lateinit var bindings: ActivitySettingsGeofenceBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivitySettingsGeofenceBinding.inflate(layoutInflater)
        setContentView(bindings.root)

        // Set the initial value for textViewRadiusValue
        prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val currentValue = prefs.getInt("radius", 500)
        bindings.currentValue.setText(currentValue.toString())

        bindings.buttonSave.setOnClickListener {
            val newValue = bindings.currentValue.text.toString().toIntOrNull() ?: currentValue
            saveSharedPreferences(newValue)
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun saveSharedPreferences(radius: Int) {
        val editor = prefs.edit()
        editor.putInt("radius", radius)
        editor.apply()
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