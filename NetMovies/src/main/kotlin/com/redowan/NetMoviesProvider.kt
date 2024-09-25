package com.netmovies

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addImdbUrl
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element

class NetMoviesProvider : MainAPI() {
    override var mainUrl = "https://web.netmovies.to"
    override var name = "NetMovies"
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )

    override val hasMainPage = true
    override val hasDownloadSupport = true

    override val mainPage = mainPageOf(
        "$mainUrl/home/" to "Latest Movies",
        "$mainUrl/popular/" to "Popular Movies",
        "$mainUrl/top-rated/" to "Top Rated Movies"
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val doc = app.get(
            "$mainUrl/category/${request.data}/$page.html", cacheTime = 60).document
        val homeResponse = doc.select("div.L")
        val home = homeResponse.mapNotNull { post ->
            toResult(post)
        }
        return newHomePageResponse(request.name, home, false)
    }

    private suspend fun toResult(post: Element): SearchResponse {
        var title = post.text()
        val size = "\\[\\d(.*?)B]".toRegex().find(title)?.value
        if (size != null) {
            val newTitle = title.replace(size, "")
            title = "$size $newTitle"
        }
        val url = mainUrl + post.select("a").attr("href")
        val doc = app.get(url, cacheTime = 60).document
        return newMovieSearchResponse(title, url, TvType.Movie) {
            this.posterUrl = doc.select(".movielist > img:nth-child(1)").attr("src")
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("$mainUrl/search.php?search=$query&cat=All").document
        val searchResponse = doc.select("div.L")
        return searchResponse.mapNotNull { post ->
            toResult(post)
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url, cacheTime = 60).document
        val title = doc.select("div.Robiul").first()!!.text()
        val year = "(?<=\\()\\d{4}(?=\\))".toRegex().find(title)?.value?.toIntOrNull()
        val imageUrl = doc.select(".movielist > img:nth-child(1)").attr("src")
        val plot = doc.select("div.Let:nth-child(8)").text()
        val links = doc.select(".Bolly")
        if (links.text().contains("Episode") || links.text().contains("720p Links")) {
            val episodesData = mutableListOf<Episode>()
            links.select("a").forEach {
                if (it.text() != "") episodesData.add(Episode(it.attr("href"), it.text()))
            }
            return newTvSeriesLoadResponse(title, url, TvType.TvSeries, episodesData) {
                this.posterUrl = imageUrl
                this.year = year
                this.plot = plot
            }
        } else {
            var link = ""
            val aLinks = links.select("a")
            for (i in aLinks.indices) {
                if (aLinks[i].text() != "") {
                    link += aLinks[i].attr("href") + " ; "
                    if (aLinks[i].attr("href")
                            .contains("howblogs")
                    ) break
                }
            }
            return newMovieLoadResponse(title, url, TvType.Movie, link) {
                this.posterUrl = imageUrl
                this.year = year
                this.plot = plot
            }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        data.split(" ; ").forEach { link ->
            if (link != "") postMan(link, subtitleCallback, callback)
        }
        return true
    }

    private suspend fun postMan(
        url: String,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        if (url.contains("howblogs")) howBlogs(url, subtitleCallback, callback)
        //else if (url.contains("fastxyz")) fastxyz(url, subtitleCallback, callback)
        else if (url.contains("hubcloud")) hubCloud(url, callback)
        else loadExtractor(url, subtitleCallback, callback)
    }

    private suspend fun howBlogs(
        url: String,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(url).document
        var link: String
        doc.select(".cotent-box > a").forEach {
            link = it.attr("href")
            if ("hubcloud" in link) hubCloud(link, callback)
            //else if ("fastxyz" in link) fastxyz(link, subtitleCallback, callback)
        }
    }

    private suspend fun fastxyz(
        data: String,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(data.replaceBefore("/drive/", "https://hubcloud.club")).document
        val url1 = doc.selectFirst("div.box:nth-child(1)  a")?.attr("href")
        if (url1 != null) loadExtractor(app.get(url1).url, subtitleCallback, callback)

        val url2 = doc.selectFirst("div.box:nth-child(2)  a")?.attr("href")
        if (url2 != null) {
            callback.invoke(
                ExtractorLink(
                    "Fastxyz",
                    "Fastxyz-Cloudflare",
                    url2,
                    "",
                    Qualities.Unknown.value,
                )
            )
        }
    }

    private suspend fun hubCloud(
        data: String,
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(data, allowRedirects = true).document
        val gamerLink: String

        if (data.contains("drive")) {
            val scriptTag = doc.selectFirst("script:containsData(url)")?.toString()
            gamerLink =
                scriptTag?.let { Regex("var url = '([^']*)'").find(it)?.groupValues?.get(1) }
                    ?: ""
        } else {
            gamerLink = doc.selectFirst("div.vd > center > a")?.attr("href") ?: ""
        }

        val document = app.get(gamerLink).document

        val div = document.selectFirst("div.card-body")
        val header = document.selectFirst("div.card-header")?.text()
        div?.select("a")?.forEach {
            val link = it.attr("href")
            val text = it.text()
            if (link.contains("pixeldra")) {
                val pixeldrainLink = link.replace("/u/", "/api/file/")
                callback.invoke(
                    ExtractorLink(
                        "Pixeldrain[Watch]",
                        "Pixeldrain",
                        pixeldrainLink,
                        link,
                        getVideoQuality(header),
                    )
                )
            } else if (text.contains("Download [Server : 10Gbps]")) {
                val response = app.get(link, allowRedirects = false)
                val downloadLink =
                    response.headers["location"].toString().split("link=").getOrNull(1)
                        ?: link
                callback.invoke(
                    ExtractorLink(
                        "Google[Download]",
                        "Google[Download]",
                        downloadLink,
                        "",
                        getVideoQuality(header),
                    )
                )
            } else if (link.contains("fastdl")) {
                callback.invoke(
                    ExtractorLink(
                        "Fastdl",
                        "Fastdl[Download]",
                        link,
                        "",
                        getVideoQuality(header),
                    )
                )
            }
        }
    }

    private fun getVideoQuality(str: String?): Int {
        return Regex("(\\d{3,4})[pP]").find(str ?: "")?.groupValues?.getOrNull(1)
            ?.toIntOrNull()
            ?: Qualities.Unknown.value
    }
}