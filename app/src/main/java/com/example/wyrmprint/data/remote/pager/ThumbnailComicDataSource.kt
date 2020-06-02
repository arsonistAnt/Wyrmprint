package com.example.wyrmprint.data.remote.pager

import androidx.annotation.MainThread
import androidx.paging.PageKeyedDataSource
import com.example.wyrmprint.data.database.ThumbnailDao
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.toThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

/**
 *  Handles loading thumbnail page data from the [dragaliaApi] source.
 */
class ThumbnailComicDataSource @Inject constructor(
    private val dragaliaApi: DragaliaLifeApi,
    private val disposables: CompositeDisposable,
    private val thumbnailDao: ThumbnailDao
) : PageKeyedDataSource<Int, ThumbnailData>() {
    // A listener for the data source
    private var mListener: DataSourceCallback? = null


    @MainThread
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ThumbnailData>
    ) {
        // Create the callback for the initial load.
        val initialLoadCallback = { thumbnailList: List<ThumbnailData> ->
            mListener?.onLoadInitial()
            callback.onResult(
                thumbnailList,
                null,
                0
            )
        }
        // Load the initial data via Room or API request.
        loadData(0, initialLoadCallback)
    }

    @MainThread
    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ThumbnailData>
    ) {
        // The current thumbnail page number to load.
        val pageNumToLoad = params.key + 1

        val loadCallback = { thumbnailList: List<ThumbnailData> ->
            callback.onResult(thumbnailList, pageNumToLoad)
        }
        // Load thumbnail data for the current page number.
        loadData(pageNumToLoad, loadCallback)
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ThumbnailData>
    ) {
    }

    override fun invalidate() {
        thumbnailDao.clearThumbnailData()
        disposables.clear()
        super.invalidate()
    }


    /**
     * Sets the current [mListener] for this class.
     *
     * @param listener the [DataSourceCallback] listener.
     */
    fun setDataSourceListener(listener: DataSourceCallback?) {
        mListener = listener
    }

    /**
     * Handles loading data either from cache or from the API itself. This function will insert
     * the data fetched from the API into the cache if it's not in the database already.
     *
     * @param pageNum indicates which set of thumbnail data to fetch.
     * @param loadCallback a callback that execute the load callbacks from the data source functions.
     *
     * @see loadInitial
     * @see loadAfter
     */
    private fun loadData(pageNum: Int, loadCallback: (thumbnailList: List<ThumbnailData>) -> Unit) {
        // Fetch the cached thumbnail list.
        val cacheSource = thumbnailDao.getThumbnailPage(pageNum).doOnError { Timber.e(it) }
        // Check if cached list is empty, if it is then fetch source list from API.
        cacheSource.observeOn(AndroidSchedulers.mainThread())
            .flatMap { cachedList ->
                if (cachedList.isEmpty()) fetchThumbnailPage(pageNum)
                else Single.just(cachedList)
            }
            .subscribe(
                { thumbnailList ->
                    loadCallback(thumbnailList)
                }, {
                    Timber.e(it)
                }).addTo(disposables)
    }

    /**
     * Return an Observable that fetches the thumbnail page data and stores it into the cache database.
     *
     * @param pageNum the page number to fetch the thumbnail data from.
     *
     * @return an Observable that handles caching and fetching thumbnail page data.
     */
    private fun fetchThumbnailPage(pageNum: Int) = dragaliaApi.fetchComicStripPage(pageNum)
        .doOnError { Timber.e(it) }
        .map {
            val sourceList = it.toThumbnailData(pageNum)
            thumbnailDao.insertThumbnailData(sourceList)
            sourceList
        }


}