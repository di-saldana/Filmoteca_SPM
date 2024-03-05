package es.ua.eps.raw_filmoteca

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.ua.eps.raw_filmoteca.data.FilmDataSource
import es.ua.eps.raw_filmoteca.data.FilmsArrayAdapter
import es.ua.eps.raw_filmoteca.databinding.ActivityFilmListBinding


//-------------------------------------
class FilmListActivity : BaseActivity()
    , AdapterView.OnItemClickListener {

    private lateinit var bindings : ActivityFilmListBinding
    private lateinit var filmAdapter: FilmsArrayAdapter
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val filmAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            filmAdapter.notifyDataSetChanged()
        }
    }

    private val filmRemovedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            filmAdapter.notifyDataSetChanged()
        }
    }

    //---------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        checkPermission(Manifest.permission.INTERNET, {
            filmAdapter.notifyDataSetChanged()
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        askNotificationPermission()

        val filter = IntentFilter("FILM_ADDED")
        registerReceiver(filmAddedReceiver, filter)

        val filterRemoved = IntentFilter("FILM_REMOVED")
        registerReceiver(filmRemovedReceiver, filterRemoved)
    }

    //---------------------------------
    override fun onRestart() {
        super.onRestart()
        filmAdapter.notifyDataSetChanged()
    }

    //---------------------------------
    override fun onDestroy() {
        unregisterReceiver(filmAddedReceiver)
        unregisterReceiver(filmRemovedReceiver)
        super.onDestroy()
    }

    //---------------------------------
    private fun initUI() {
        bindings = ActivityFilmListBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
            filmAdapter = FilmsArrayAdapter(this@FilmListActivity, android.R.layout.simple_list_item_1, FilmDataSource.films)
            list.onItemClickListener = this@FilmListActivity
            list.adapter = filmAdapter
        }
    }

    //---------------------------------
    // AdapterView.OnItemClickListener (ListView)
    //---------------------------------
    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, index: Int, l: Long) {
        val intent = Intent(this, FilmDataActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        intent.putExtra(FilmDataActivity.EXTRA_FILM_ID, index)
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

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun askNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if(!hasPermission){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

}
