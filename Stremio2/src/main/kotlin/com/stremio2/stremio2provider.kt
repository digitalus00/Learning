package com.stremio2

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class YTSProvider: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(stremio2())

    }
}
