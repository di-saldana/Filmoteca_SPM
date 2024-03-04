package es.ua.eps.raw_filmoteca

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.ua.eps.raw_filmoteca.data.Film
import es.ua.eps.raw_filmoteca.data.FilmDataSource
import es.ua.eps.raw_filmoteca.databinding.ActivityAddFilmBinding

class AddFilm : AppCompatActivity() {
    private lateinit var bindings: ActivityAddFilmBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityAddFilmBinding.inflate(layoutInflater)

        with(bindings) {
            setContentView(root)

            val genreSpinner: Spinner = genre
            val genres = arrayOf(
                Film.Genre.Action, Film.Genre.Drama, Film.Genre.Comedy,
                Film.Genre.Terror, Film.Genre.SciFi, Film.Genre.Fantasy
            )
            val adapter = ArrayAdapter(this@AddFilm, R.layout.simple_spinner_item, genres)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            genreSpinner.adapter = adapter

            val formatSpinner: Spinner = format
            val formats = arrayOf(Film.Format.DVD, Film.Format.BlueRay, Film.Format.Digital)
            val adapter2 = ArrayAdapter(this@AddFilm, R.layout.simple_spinner_item, formats)
            adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            formatSpinner.adapter = adapter2

            addFilmButton.setOnClickListener {
                val titleStr = title.text.toString()
                val directorStr = director.text.toString()
                val yearInt = year.text.toString().toIntOrNull() ?: 0
                val selectedGenre = genres[genreSpinner.selectedItemPosition]
                val selectedFormat = formats[formatSpinner.selectedItemPosition]
                val imdbUrlStr = imdbUrl.text.toString()
                val commentsStr = comments.text.toString()

                // TODO: Add Photo

                if(!titleStr.isEmpty() && !directorStr.isEmpty()) {
                    addFilm(titleStr, directorStr, yearInt, selectedGenre, selectedFormat, imdbUrlStr, commentsStr)

                    val toast = Toast.makeText(this@AddFilm, "Film Added Successfully", Toast.LENGTH_SHORT)
                    toast.show()

                    title.text.clear()
                    director.text.clear()
                    year.text.clear()
                    imdbUrl.text.clear()
                    comments.text.clear()
                } else {
                    val toast = Toast.makeText(this@AddFilm, "Complete the Fields", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
        newFilm.imageResId = es.ua.eps.raw_filmoteca.R.drawable.filmoteca

        FilmDataSource.add(newFilm)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(es.ua.eps.raw_filmoteca.R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            es.ua.eps.raw_filmoteca.R.id.add -> {
                val intent = Intent(this, AddFilm::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }
            es.ua.eps.raw_filmoteca.R.id.close -> {
                mGoogleSignInClient.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }
            es.ua.eps.raw_filmoteca.R.id.disconnect -> {
                mGoogleSignInClient.revokeAccess()
                finish()
                finishAffinity()
                return true
            }
            es.ua.eps.raw_filmoteca.R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}