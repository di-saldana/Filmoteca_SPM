package es.ua.eps.raw_filmoteca

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.ua.eps.raw_filmoteca.data.FilmDataSource
import es.ua.eps.raw_filmoteca.databinding.ActivityFilmDataBinding
import es.ua.eps.raw_filmoteca.tools.OnTaskCompleted
import es.ua.eps.raw_filmoteca.tools.TaskRunner

class FilmDataActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_FILM_ID = "EXTRA_FILM_ID"
    }
    private lateinit var bindings : ActivityFilmDataBinding
    private var index = -1
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()

        index = intent.getIntExtra(EXTRA_FILM_ID, -1)
        loadMovieData(index)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private fun initUI() {
        bindings = ActivityFilmDataBinding.inflate(layoutInflater)
        with(bindings) {
            setContentView(root)
        }
    }

    private fun loadMovieData(index: Int) {
        if (index != -1) {
            val film = FilmDataSource.films[index]

            when {
                film.image != null -> {
                    bindings.imgCover.setImageBitmap(film.image)
                }

                film.imageResId != 0 -> {
                    bindings.imgCover.setImageResource(film.imageResId)
                }

                film.imageUrl != null -> {
                    TaskRunner.doInBackground(bindings.imgCover, film.imageUrl, object : OnTaskCompleted {
                        override fun onTaskCompleted(result: Bitmap?) {
                            film.image = result
                        }
                    })
                }
            }

            bindings.movieTitle.text = film.title
            bindings.tvDirector.text = film.director
            bindings.tvYear.text     = film.year.toString()
            //bindings.tvFormat.text   = "${resources.getStringArray(R.array.formats)[film.format]}, ${resources.getStringArray(R.array.genres)[film.genre]}"
            bindings.tvComments.text = film.comments
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

            else -> return super.onOptionsItemSelected(item)
        }
    }
}