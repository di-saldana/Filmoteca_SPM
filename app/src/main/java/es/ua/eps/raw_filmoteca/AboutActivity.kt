package es.ua.eps.raw_filmoteca

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.picasso.Picasso
import es.ua.eps.raw_filmoteca.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityAboutBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityAboutBinding.inflate(layoutInflater)

        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)

        val account : GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)

        with(bindings) {
            setContentView(root)

            if (account != null) {
                Picasso.get().load(account.photoUrl).into(profileImage)
                id.text = "ID: " + account.id
                name.text = "Name: " + account.displayName
                email.text = "Email: " + account.email
            }

            websiteButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/"))

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }

            supportButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:dsl42@alu.ua.es"))

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }

            backButton.setOnClickListener {
                finish()
            }
        }
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