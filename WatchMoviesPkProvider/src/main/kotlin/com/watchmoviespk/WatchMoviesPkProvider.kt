package recloudstream

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element
import kotlin.text.RegexOption

class demoTryProvider : MainAPI() {

    override var mainUrl = "https://erored.com/"
    override var name = "Ero Red"
    override val hasMainPage = true
    override var lang = "en"
    override val supportedTypes = setOf(
        TvType.NSFW
    )


    override val mainPage = mainPageOf(
        "/scandal" to "Scandal Videos",
        "/porn" to "Daily Videos",
        "/celebrities" to "Celebreties Videos",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get("$mainUrl/${request.data}/page/$page").document
        val home = document.select("div.content-loop clear div").mapNotNull { it.toSearchResult() }
        return newHomePageResponse(
            list = HomePageList(
                name = request.name,
                list = home,
                isHorizontalImages = false
            ),
            hasNext = true
        )
    }

    private fun Element.toSearchResult(): SearchResponse {
        val title = this.select("h2.entry-title a").text()
        val href = fixUrl(this.select("h2.entry-title a").attr("href"))
        val posterUrl = fixUrlNull(this.select("div.thumbnail-wrap img").attr("src"))
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl

        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("${mainUrl}?s=$query", timeout = 50L).document
        val results =document.select("div.content-loop clear div").mapNotNull { it.toSearchResult() }
        return results
    }


    override suspend fun load(url: String): LoadResponse? {
        val doc = app.get(url).document
        val title = doc.select(".page-body h2").text()
        val imageUrl = doc.select(".page-body img").attr("src")
        val info = doc.select(".page-body p:nth-of-type(1)").text()
        val story = ("(?<=Storyline,).*|(?<=Story : ).*|(?<=Storyline : ).*|(?<=Description : ).*|(?<=Description,).*(?<=Story,).*").toRegex().find(info)?.value
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = imageUrl
            if(story != null) {
                this.plot = story.trim()
            }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.post(data).document
        doc.select(".col-sm-8.col-sm-offset-2.well.view-well a").forEach {
            val link = it.attr("href")//.replace("/v/","/e/")
            loadExtractor(link, subtitleCallback, callback)
        }
        return true
    }
}
