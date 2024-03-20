package es.ua.eps.raw_filmoteca

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import androidx.core.net.toUri
import androidx.fragment.app.ListFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.RemoteMessage
import es.ua.eps.raw_filmoteca.data.Film
import es.ua.eps.raw_filmoteca.data.FilmDataSource
import es.ua.eps.raw_filmoteca.data.FilmsArrayAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class FilmListFragment : ListFragment(), MessageListener {
    private var callback: OnItemSelectedListener? = null
    private val selectedItems = mutableListOf<Int>()

    private lateinit var adapter: FilmsArrayAdapter
    private lateinit var gso : GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        adapter = FilmsArrayAdapter(activity, R.layout.item_film, FilmDataSource.films)
        listAdapter = adapter
        MyFirebaseMessagingService.registerListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as OnItemSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnItemSelectedListener")
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(context, gso)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        callback?.onItemSelected(position, listView)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMessageReceived(message: RemoteMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            launch(Dispatchers.Main) {
                when (message.data["type"]) {
                    "alta" -> handleFilmMessage(message, isNewFilm = true)
                    "baja" -> handleFilmMessage(message, isNewFilm = false)
                    else -> Log.e("Error", "Error receiving the message")
                }
            }
        }
    }

    private fun handleFilmMessage(message: RemoteMessage, isNewFilm: Boolean) {
        val photo = message.data["image"]?.toUri()
        val title = message.data["title"]
        val director = message.data["dir"]
        val year = message.data["year"]
        val genre = message.data["genre"]?.toInt()
        val format = message.data["format"]?.toInt()
        val imdb = message.data["imdb"]
        val comments = message.data["comments"]
        val latitude = message.data["lat"]?.toDouble()
        val longitude = message.data["lon"]?.toDouble()

        val filmExists = FilmDataSource.films.any { it.title.toString() == title }

        if (isNewFilm) {
            if (filmExists) {
                updateFilm(title, photo, director, year, genre, format, imdb,comments, latitude, longitude)
            } else {
                addFilm(title, photo, director, year, genre, format, imdb, comments, latitude, longitude)
            }
        } else {
            if (filmExists) {
                removeFilm(title)
            }
        }
    }

    private fun updateFilm(title: String?, imageUrl: Uri?, director: String?, year: String?,
                           genre: Int?, format: Int?, imdb: String?, comments: String?,
                           lat: Double?, lon: Double?) {
        val existingFilm = FilmDataSource.films.find { it.title.toString() == title }
        existingFilm?.apply {
            this.director = director
            if (year != null) {
                this.year = year.toInt()
            }
            if (genre != null) {
                this.genre = Film.Genre.fromValue(genre)!!
            }
            this.imdbUrl = imdb
            if (format != null) {
                this.format = Film.Format.fromValue(format)!!
            }
            if (lat != null) {
                this.lat = lat
            }
            if (lon != null) {
                this.lon = lon
            }
            this.comments = comments
            imageUrl?.let { this.imageUrl = it.toString() }
        }

        (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
    }

    private fun addFilm(title: String?, imageUrl: Uri?, director: String?, year: String?,
                        genre: Int?, format: Int?, imdb: String?, comments: String?,
                        lat: Double?, lon: Double?) {
        val film = Film().apply {
            this.title = title
            this.director = director
            if (year != null) {
                this.year = year.toInt()
            }
            if (genre != null) {
                this.genre = Film.Genre.fromValue(genre)!!
            }
            this.imdbUrl = imdb
            if (format != null) {
                this.format = Film.Format.fromValue(format)!!
            }
            if (lat != null) {
                this.lat = lat
            }
            if (lon != null) {
                this.lon = lon
            }
            this.comments = comments
            imageUrl?.let {film -> this.imageUrl = film.toString() }
        }
        FilmDataSource.films.add(film)
        (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
    }

    private fun removeFilm(title: String?) {
        val existingFilm = FilmDataSource.films.find { film -> film.title.toString() == title }
        existingFilm?.let { film->
            FilmDataSource.films.remove(film)
            (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
        }
    }

    private fun removeSelectedItems() {
        val selectedItems = ArrayList<Film>()
        for (i in 0 until listView.count) {
            if (listView.isItemChecked(i)) {
                val film = listView.getItemAtPosition(i) as Film
                selectedItems.add(film)
            }
        }
        for (item in selectedItems) {
            FilmDataSource.films.remove(item)
        }

        (listView.adapter as FilmsArrayAdapter).notifyDataSetChanged()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        listView.setMultiChoiceModeListener(
            object : AbsListView.MultiChoiceModeListener {

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    val inflater = mode.menuInflater
                    inflater.inflate(R.menu.delete_menu, menu)
                    selectedItems.clear()
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode,
                                                 item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.delete -> {
                            removeSelectedItems()
                            mode.finish()
                            true
                        }
                        else -> false
                    }
                }

                override fun onDestroyActionMode(mode: ActionMode) {}

                override fun onItemCheckedStateChanged(mode: ActionMode,
                                                       position: Int, id: Long, checked: Boolean) {
                    val count = listView.checkedItemCount
                    mode.title = "$count selected"

                    if (checked) {
                        val selectedView = listView.getChildAt(position)
                        selectedView?.setBackgroundResource(R.drawable.list_selector)
                    } else {
                        val selectedView = listView.getChildAt(position)
                        selectedView?.setBackgroundResource(android.R.color.transparent)
                    }
                }
            }
        )
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int, listView: ListView)
    }

    override fun onDestroy() {
        super.onDestroy()
        MyFirebaseMessagingService.unregisterListener(this)
    }
}