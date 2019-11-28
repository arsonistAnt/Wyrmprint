package com.example.wyrmprint.data.remote.pager

import androidx.paging.PageKeyedDataSource
import com.example.wyrmprint.data.model.ComicThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

/**
 *  Handles loading thumbnail page data from the [dragaliaApi] source.
 */
class ThumbnailComicDataSource @Inject constructor(
    private val dragaliaApi: DragaliaLifeApi,
    private val disposables: CompositeDisposable
) : PageKeyedDataSource<Int, ComicThumbnailData>() {
    // Track current page on api.
    private var currentPageNum = 0
    // A listener for the data source
    private var mListener: DataSourceCallback? = null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ComicThumbnailData>
    ) {
        // Fetch the thumbnail page.
        dragaliaApi.fetchComicStripPage(currentPageNum)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> Timber.e(e) }
            .subscribe { thumbnailList ->
                callback.onResult(thumbnailList, null, currentPageNum + 1)
                incrementPageNumSync()
                mListener?.onLoadInitial()
            }.addTo(disposables)
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ComicThumbnailData>
    ) {
        // The current thumbnail page number to load.
        val pageNumToLoad = params.key
        // Fetch the thumbnail page.
        dragaliaApi.fetchComicStripPage(pageNumToLoad)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { e -> Timber.e(e) }
            .subscribeBy( // onError, onComplete, onSuccess
                { error ->
                    Timber.e(error)
                },
                {
                    // Still apply callback after unsuccessful call.
                    mListener?.onLoadAfter()
                },
                { thumbnailList ->
                    callback.onResult(thumbnailList, pageNumToLoad + 1)
                    incrementPageNumSync()
                    mListener?.onLoadAfter()
                }
            )
            .addTo(disposables)
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ComicThumbnailData>
    ) {
    }

    override fun invalidate() {
        super.invalidate()
        disposables.clear()
    }


    /**
     * Increment [currentPageNum] synchronously.
     */
    @Synchronized
    private fun incrementPageNumSync() {
        currentPageNum++
    }

    /**
     * Sets the current [mListener] for this class.
     *
     * @param listener the [DataSourceCallback] listener.
     */
    fun setDataSourceListener(listener: DataSourceCallback?) {
        mListener = listener
    }
}