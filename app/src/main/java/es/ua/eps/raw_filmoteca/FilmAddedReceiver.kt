package es.ua.eps.raw_filmoteca

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import es.ua.eps.raw_filmoteca.data.FilmsArrayAdapter

class FilmAddedReceiver(private val filmAdapter: FilmsArrayAdapter) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        filmAdapter.notifyDataSetChanged()
    }
}
