package com.stremio2

//import android.util.Log
import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

open class stremio2provider : MainAPI() {
    override var mainUrl = "https://cinemeta-catalogs.strem.io"
    override var name = "CineStream"
    override val hasMainPage = true
    override var lang = "en"
    override val hasDownloadSupport = true
    val skipMap: MutableMap<String, Int> = mutableMapOf()
    val cinemeta_url = "https://v3-cinemeta.strem.io"
    //val cyberflix_url = "https://cyberflix.elfhosted.com/c/catalogs"
    val kitsu_url = "https://anime-kitsu.strem.fun"
    //val anime_catalogs_url = "https://1fe84bc728af-stremio-anime-catalogs.baby-beamup.club"
    val haglund_url = "https://arm.haglund.dev/api/v2"
    val jikanAPI = "https://api.jikan.moe/v4"
    val streamio_TMDB = "https://94c8cb9f702d-tmdb-addon.baby-beamup.club"
    companion object {
        const val malsyncAPI = "https://api.malsync.moe"
        const val vegaMoviesAPI = "https://vegamovies.si"
        const val rogMoviesAPI = "https://rogmovies.fun"
        const val MovieDrive_API="https://moviesdrive.world"
        const val topmoviesAPI = "https://topmovies.icu"
        const val MoviesmodAPI = "https://moviesmod.bid"
        const val Full4MoviesAPI = "https://www.full4movies.my"
        const val VadapavAPI = "https://vadapav.mov"
        const val stremifyAPI = "https://stremify.hayd.uk/stream"
        const val netflixAPI = "https://iosmirror.cc"
        const val W4UAPI = "https://world4ufree.joburg"
        const val WHVXSubsAPI = "https://subs.whvx.net"
        const val WYZIESubsAPI = "https://subs.wyzie.ru"
        const val AutoembedAPI = "https://autoembed.cc"
        //const val WHVXAPI = "https://api.whvx.net"
        const val uhdmoviesAPI = "https://uhdmovies.icu"
        const val myConsumetAPI = BuildConfig.CONSUMET_API
        const val moviesAPI = "https://moviesapi.club"
        const val TwoEmbedAPI = "https://2embed.wafflehacker.io"
        //const val FilmyxyAPI = "https://filmxy.wafflehacker.io"
        const val AutoembedDramaAPI = "https://asian-drama.autoembed.cc"
        const val RarAPI = "https://nepu.to"
        const val hianimeAPI = "https://hianime.to"
        const val animepaheAPI = "https://animepahe.ru"
        const val viteAPI = "https://viet.autoembed.cc"
        const val multimoviesAPI = "https://multimovies.bond"
        const val anitaku = "https://anitaku.pe"
        const val cinemaluxeAPI = "https://cinemaluxe.click"
        const val bollyflixAPI = "https://bollyflix.fi"
        const val TomAPI = "https://tom.autoembed.cc"
        const val torrentioAPI = "https://torrentio.strem.fun"
        const val torrentioCONFIG = "providers=yts,eztv,rarbg,1337x,thepiratebay,kickasstorrents,torrentgalaxy,magnetdl,horriblesubs,nyaasi,tokyotosho,anidex|sort=seeders|qualityfilter=threed,480p,other,scr,cam,unknown|limit=10"
    }
    val wpRedisInterceptor by lazy { CloudflareKiller() }
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries,
        TvType.AsianDrama,
        TvType.Anime
    )

    override val mainPage = mainPageOf(
        "$streamio_TMDB/catalog/movie/tmdb.trending/skip=###&genre=Day" to "Trending Movies Today",
        "$streamio_TMDB/catalog/series/tmdb.trending/skip=###&genre=Day" to "Trending Series Today",
        "$mainUrl/top/catalog/movie/top/skip=###" to "Top Movies",
        "$mainUrl/top/catalog/series/top/skip=###" to "Top Series",
        "$streamio_TMDB/catalog/movie/tmdb.language/skip=###&genre=Hindi" to "Trending Indian Movie",
        "$streamio_TMDB/catalog/series/tmdb.language/skip=###&genre=Hindi" to "Trending Indian Series",
        "$kitsu_url/catalog/anime/kitsu-anime-airing/skip=###" to "Top Airing Anime",
        "$kitsu_url/catalog/anime/kitsu-anime-trending/skip=###" to "Trending Anime",
        "$streamio_TMDB/catalog/series/tmdb.language/skip=###&genre=Korean" to "Trending Korean Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Action" to "Top Action Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Action" to "Top Action Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Comedy" to "Top Comedy Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Comedy" to "Top Comedy Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Romance" to "Top Romance Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Romance" to "Top Romance Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Horror" to "Top Horror Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Horror" to "Top Horror Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Thriller" to "Top Thriller Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Thriller" to "Top Thriller Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Sci-Fi" to "Top Sci-Fi Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Sci-Fi" to "Top Sci-Fi Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Fantasy" to "Top Fantasy Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Fantasy" to "Top Fantasy Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Mystery" to "Top Mystery Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Mystery" to "Top Mystery Series",
        "$mainUrl/imdbRating/catalog/movie/imdbRating/skip=###&genre=Crime" to "Top Crime Movies",
        "$mainUrl/imdbRating/catalog/series/imdbRating/skip=###&genre=Crime" to "Top Crime Series",
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val skip = skipMap[request.name] ?: 0
        val newRequestData = request.data.replace("###", skip.toString())
        val json = app.get("$newRequestData.json").text
        val movies = parseJson<Home>(json)
        val movieCount = movies.metas.size
        skipMap[request.name] = skip + movieCount
        val home = movies.metas.mapNotNull { movie ->
            newMovieSearchResponse(movie.name, PassData(movie.id, movie.type).toJson(), TvType.Movie) {
                this.posterUrl = movie.poster.toString()
            }
        }
        return newHomePageResponse(
            list = HomePageList(
                name = request.name,
                list = home
            ),
            hasNext = true
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchResponse = mutableListOf<SearchResponse>()
        val animeJson = app.get("$kitsu_url/catalog/anime/kitsu-anime-list/search=$query.json").text
        val animes = tryParseJson<SearchResult>(animeJson)
        animes?.metas ?.forEach {
            searchResponse.add(newMovieSearchResponse(it.name, PassData(it.id, it.type).toJson(), TvType.Movie) {
                this.posterUrl = it.poster.toString()
            })
        }
        val movieJson = app.get("$cinemeta_url/catalog/movie/top/search=$query.json").text
        val movies = parseJson<SearchResult>(movieJson)
        movies.metas.forEach {
            searchResponse.add(newMovieSearchResponse(it.name, PassData(it.id, it.type).toJson(), TvType.Movie) {
                this.posterUrl = it.poster.toString()
            })
        }

        val seriesJson = app.get("$cinemeta_url/catalog/series/top/search=$query.json").text
        val series = parseJson<SearchResult>(seriesJson)
        series.metas.forEach {
            searchResponse.add(newMovieSearchResponse(it.name, PassData(it.id, it.type).toJson(), TvType.Movie) {
                this.posterUrl = it.poster.toString()
            })
        }

        return searchResponse.sortedByDescending { response ->
            calculateRelevanceScore(response.name, query)
        }
    }
//trying new
val TorrentTrackers = """http://nyaa.tracker.wf:7777/announce,
            http://anidex.moe:6969/announce,http://tracker.anirena.com:80/announce,
            udp://tracker.uw0.xyz:6969/announce,
            http://share.camoe.cn:8080/announce,
            http://t.nyaatracker.com:80/announce,
            udp://47.ip-51-68-199.eu:6969/announce,
            udp://9.rarbg.me:2940,
            udp://9.rarbg.to:2820,
            udp://exodus.desync.com:6969/announce,
            udp://explodie.org:6969/announce,
            udp://ipv4.tracker.harry.lu:80/announce,
            udp://open.stealth.si:80/announce,
            udp://opentor.org:2710/announce,
            udp://opentracker.i2p.rocks:6969/announce,
            udp://retracker.lanta-net.ru:2710/announce,
            udp://tracker.cyberia.is:6969/announce,
            udp://tracker.dler.org:6969/announce,
            udp://tracker.ds.is:6969/announce,
            udp://tracker.internetwarriors.net:1337,
            udp://tracker.openbittorrent.com:6969/announce,
            udp://tracker.opentrackr.org:1337/announce,
            udp://tracker.tiny-vps.com:6969/announce,
            udp://tracker.torrent.eu.org:451/announce,
            udp://valakas.rollo.dnsabr.com:2710/announce,
            udp://www.torrent.eu.org:451/announce
        """.trimIndent()
suspend fun invokeTorrentio(
    id: String? = null,
    season: Int? = null,
    episode: Int? = null,
    callback: (ExtractorLink) -> Unit,
    subtitleCallback: (SubtitleFile) -> Unit
) {
    val url = if(season == null) {
        "$torrentioAPI/$torrentioCONFIG/stream/movie/$id.json"
    }
    else {
        "$torrentioAPI/$torrentioCONFIG/stream/series/$id:$season:$episode.json"
    }
    val res = app.get(url, timeout = 100L).parsedSafe<TorrentioResponse>()
    res?.streams?.forEach { stream ->
        val sourceTrackers = TorrentTrackers.split(",").map { it.trim() }.filter { it.isNotBlank() }.joinToString("&tr=")
        val magnet = "magnet:?xt=urn:btih:${stream.infoHash}&dn=${stream.infoHash}&tr=$sourceTrackers&index=${stream.fileIdx}"
        callback.invoke(
            ExtractorLink(
                "Torrentio",
                stream.title ?: stream.name ?: "",
                magnet,
                "",
                getIndexQuality(stream.name),
                INFER_TYPE,
            )
        )
    }
}
