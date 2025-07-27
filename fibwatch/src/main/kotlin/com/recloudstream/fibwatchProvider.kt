package recloudstream

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.getQualityFromName
import org.jsoup.nodes.Element

class Fibwatch : MainAPI() {
    override var mainUrl = "https://fibwatch.art/"
    override var name = "FibWatch"
    override val hasMainPage = true
    override var lang = "hi"
    override val hasDownloadSupport = true

    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )
    override val mainPage = mainPageOf(
        "$mainUrl/videos/latest/" to "Latest Videos",
        "$mainUrl/videos/trending/" to "Trending Videos",
        "$mainUrl/videos/top/" to "Top Videos",
        "$mainUrl/videos/category/3" to "Web Series"
        )
    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {

        val document = app.get(request.data + page).document
        val home = document.select(".col-md-3 col-sm-6 col-xs-6 keep-padding").mapNotNull {
            it.toSearchResult()
        }
        return newHomePageResponse(request.name, home)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("p.hptag")?.text()?.trim() ?: return null
        val href = fixUrl(this.selectFirst(".video-list-image a")?.attr("href").toString())
        val posterUrl = fixUrlNull(this.selectFirst(".video-list-image img")?.attr("src"))
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=$query").document

        return document.select(".col-md-3 col-sm-6 col-xs-6 keep-padding").mapNotNull {
            it.toSearchResult()
        }
    }
//
//    override suspend fun load(url: String): LoadResponse {
//        val document = app.get(url).document
//
//        val title = document.selectFirst("h1.entry-title")?.text()?.trim()
//            ?: throw ErrorLoadingException("Could not load title")
//        val poster = document.selectFirst("""meta[property="og:image"]""")?.attr("content")
//        val description = document.selectFirst("div.video-description div.desc.more")?.text()?.trim()
//        val iframeUrl = document.selectFirst("div.responsive-player iframe")?.attr("src")
//            ?: throw ErrorLoadingException("Could not find video iframe")
//
//        // FIX: Thêm danh sách đề xuất (recommendations)
//        val recommendations = document.select("div.under-video-block article.loop-video.thumb-block").mapNotNull {
//            it.toSearchResponse()
//        }
//
//        return newMovieLoadResponse(
//            name = title,
//            url = url,
//            type = TvType.NSFW,
//            dataUrl = iframeUrl
//        ) {
//            this.posterUrl = poster
//            this.plot = description
//            this.tags = document.select("div.tags-list a[rel='tag']").map { it.text() }
//            this.recommendations = recommendations // <-- Thêm danh sách đề xuất vào đây
//        }
//    }
//
//    override suspend fun loadLinks(
//        data: String,
//        isCasting: Boolean,
//        subtitleCallback: (SubtitleFile) -> Unit,
//        callback: (ExtractorLink) -> Unit
//    ): Boolean {
//        val iframeDocument = app.get(data).document
//        val videoUrl = iframeDocument.selectFirst("video > source")?.attr("src")
//            ?: throw ErrorLoadingException("Could not extract video source from iframe")
//
//        callback(
//            ExtractorLink(
//                source = this.name,
//                name = this.name,
//                url = videoUrl,
//                referer = "$mainUrl/",
//                quality = getQualityFromName(""),
//                type = if (videoUrl.contains(".m3u8")) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO,
//            )
//        )
//
//        return true
//    }
}






