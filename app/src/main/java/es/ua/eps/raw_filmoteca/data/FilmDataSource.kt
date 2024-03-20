package es.ua.eps.raw_filmoteca.data

import es.ua.eps.raw_filmoteca.R

//-------------------------------------
object FilmDataSource {
    val films: MutableList<Film> = mutableListOf<Film>()

    //---------------------------------
    init {
        var f = Film()

        // Añade tantas películas como quieras!
        f = Film()
        f.title = "Little Miss Sunshine"
        f.director = "Jonathan Dayton, Valerie Faris"
        f.imageUrl = "https://m.media-amazon.com/images/M/MV5BMTgzNTgzODU0NV5BMl5BanBnXkFtZTcwMjEyMjMzMQ@@._V1_.jpg"
        f.comments = ""
        f.format = Film.Format.Digital
        f.genre = Film.Genre.Comedy
        f.imdbUrl = "https://www.imdb.com/title/tt0449059/"
        f.year = 2006
        f.lat = 35.1971681432131
        f.lon = -111.65086992604324
        films.add(f)

        f = Film()
        f.title = "Quiz Lady"
        f.director = "Jessica Yu"
        f.imageUrl = "https://upload.wikimedia.org/wikipedia/en/8/84/Quiz_lady_poster.png"
        f.comments = ""
        f.format = Film.Format.Digital
        f.genre = Film.Genre.Comedy
        f.imdbUrl = "https://www.imdb.com/title/tt13405810/?ref_=fn_al_tt_1"
        f.year = 2023
        f.lat = 30.045327497689453
        f.lon = -89.93199851892925
        films.add(f)

        f = Film()
        f.title = "Ferris Bueller's Day Off"
        f.director = "John Hughes"
        f.imageUrl = "https://m.media-amazon.com/images/M/MV5BMDA0NjZhZWUtNmI2NC00MmFjLTgwZDYtYzVjZmNhMDVmOTBkXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg"
        f.comments = ""
        f.format = Film.Format.DVD
        f.genre = Film.Genre.Comedy
        f.imdbUrl = "https://www.imdb.com/title/tt0091042/?ref_=fn_al_tt_2"
        f.year = 1986
        f.lat = 41.87975219259205
        f.lon = -87.62365964919222
        films.add(f)

        f = Film()
        f.title      = "Regreso al futuro"
        f.director   = "Robert Zemeckis"
        f.imageResId = R.drawable.regresoalfuturo
        f.comments   = ""
        f.format     = Film.Format.Digital
        f.genre      = Film.Genre.SciFi
        f.imdbUrl    = "http://www.imdb.com/title/tt0088763"
        f.year       = 1985
        f.lat        = 33.98050176719493
        f.lon        = -118.04421385176465
        films.add(f)

        f = Film()
        f.title = "Los Cazafantasmas"
        f.director = "Ivan Reitman"
        f.imageResId = R.drawable.loscazafantasmas
        f.comments = ""
        f.format = Film.Format.BlueRay
        f.genre = Film.Genre.Comedy
        f.imdbUrl = "https://www.imdb.com/title/tt0087332"
        f.year = 1984
        f.lat = 34.05482897813782
        f.lon = -118.24733129839981
        films.add(f)

        f = Film()
        f.title = "La princesa prometida"
        f.director = "Rob Reiner"
        f.imageUrl = "https://es.web.img2.acsta.net/pictures/19/07/03/16/08/2300654.jpg"
        f.comments = ""
        f.format = Film.Format.Digital
        f.genre = Film.Genre.Fantasy
        f.imdbUrl = "https://www.imdb.com/title/tt0093779/"
        f.year = 1987
        f.lat = 52.972052255160385
        f.lon = -9.431011033859534
        films.add(f)

        f = Film()
        f.title = "Picando Alante"
        f.director = "Israel Lugo"
        f.imageUrl = "https://m.media-amazon.com/images/M/MV5BYzJiNmFlMzItYmQyYS00ODQxLWFhNTAtYjZiMmFkYzVkNmUwXkEyXkFqcGdeQXVyMTUwMzE2ODQy._V1_.jpg"
        f.comments = ""
        f.format = Film.Format.Digital
        f.genre = Film.Genre.Comedy
        f.imdbUrl = "https://www.imdb.com/title/tt18380842/?ref_=fn_al_tt_1"
        f.year = 2022
        f.lat = 18.468063797421593
        f.lon = -66.11095700028409
        films.add(f)
    }

    //---------------------------------
    fun add() {
        films.add(Film())
    }

    //---------------------------------
    fun add(f: Film) {
        films.add(f)
    }

    //---------------------------------
    fun getFilmByTitle(title: String): Film? {
        for (film in films) {
            if (film.title.equals(title, ignoreCase = true)) {
                return film
            }
        }
        return null
    }

    //---------------------------------
    fun remove(existingFilm: Film) {
        films.remove(existingFilm)
    }
}