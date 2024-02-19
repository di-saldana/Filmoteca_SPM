package es.ua.eps.raw_filmoteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.net.toUri
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import es.ua.eps.raw_filmoteca.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityAboutBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)
            profileImage.setImageURI(intent.getStringExtra("pic")?.toUri())
            id.text = "ID: " + intent.getStringExtra("id")
            name.text = "Name: " + intent.getStringExtra("name")
            email.text = "Email: " + intent.getStringExtra("email")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                return true
            }
            R.id.close -> {
                return true
            }
            R.id.disconnect -> {
                return true
            }
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}