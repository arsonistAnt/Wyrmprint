package com.example.wyrmprint.data.remote

import com.example.wyrmprint.data.local.ComicStrip
import com.example.wyrmprint.data.local.ThumbnailItem
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * A class that retrieves comic strip information from Nintendo's Dragalia Lost API.
 */
class DragaliaLifeApi @Inject constructor(private var service: DragaliaLifeService) {
    /**
     * Get [ComicStrip] details from the api.
     *
     * @param comicId the id of [ComicStrip] given by the api.
     * @return [Observable] object that emits a [ComicStrip] item.
     */
    fun fetchComicStripDetails(comicId: Int): Observable<ComicStrip> =
        service.comicDetail(comicId)
            .subscribeOn(Schedulers.io())
            .concatMap {
                Observable.fromIterable(it)
            }

    /**
     * Get list of [ThumbnailItem] selections from a page.
     *
     * @param pageNum the page number of the thumbnail items.
     * @return [Maybe] that emits list of Thumbnail items.
     */
    fun fetchComicStripPage(pageNum: Int): Maybe<List<ThumbnailItem>> =
        service.thumbnailPage(pageNum)
            .subscribeOn(Schedulers.io())
}






