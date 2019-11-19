package com.example.wyrmprint.util


/**
 * Contains convenience method's and properties for constructing Api request to Dragalia Life comics. The building
 * of api requests are based on Retrofit's style.
 */
class DragaliaLifeUtil {
    companion object {
        // A map of default form-data for the POST requests.
        val formDataMap = mapOf(
            "lang" to "en",
            "type" to "dragalialife"
        )
        // Base URL
        const val baseUrl = "https://comic.dragalialost.com"
        // Path Variables
        const val comicDetailPath = "comicId"
        const val thumbnailPath = "pageNum"
        // Api Routes
        const val apiThumbnailPage = "/api/thumbnail_list/{$thumbnailPath}"
        const val apiComicDetail = "/api/detail/{$comicDetailPath}"
    }
}