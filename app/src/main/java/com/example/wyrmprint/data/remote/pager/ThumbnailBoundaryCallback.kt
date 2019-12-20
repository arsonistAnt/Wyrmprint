package com.example.wyrmprint.data.remote.pager

import androidx.paging.PagedList
import com.example.wyrmprint.data.model.ThumbnailDao
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.util.toThumbnailData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class ThumbnailBoundaryCallback @Inject constructor(
    private val dragaliaApi: DragaliaLifeApi,
    private val disposables: CompositeDisposable,
    private val thumbnailDao: ThumbnailDao
) : PagedList.BoundaryCallback<ThumbnailData>() {
    private var lastRequestedPage: Int = 0
    private var requestingPage: Boolean = false
    private val MAXIMUM_PAGES: Int = 9

    override fun onZeroItemsLoaded() {
        // Set the requested page back to the beginning.
        lastRequestedPage = 0
        fetchData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ThumbnailData) {
        if (lastRequestedPage <= MAXIMUM_PAGES) {
            fetchData()
        }
    }

    /**
     * Fetch thumbnail data and insert it into the database.
     */
    private fun fetchData() {
        if (!requestingPage) {
            // Fetch the thumbnail page.
            dragaliaApi.fetchComicStripPage(lastRequestedPage)
                .observeOn(Schedulers.io())
                .doOnSubscribe { requestingPage = true }
                .doOnError { e -> Timber.e(e) }
                .subscribeBy( // onError, onComplete, onSuccess
                    { error ->
                        Timber.e(error)
                    },
                    {
                        requestingPage = false
                    },
                    { thumbnailList ->
                        thumbnailDao.insertAll(thumbnailList.toThumbnailData())
                        requestingPage = false
                        lastRequestedPage++
                    }
                ).addTo(disposables)
        }
    }


}