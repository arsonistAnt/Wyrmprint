package com.example.wyrmprint.data.remote.pager

import androidx.paging.DataSource
import com.example.wyrmprint.data.model.ComicThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Factory class that handles the creation of [ThumbnailComicDataSource] objects.
 */
class ThumbnailDataSourceFactory @Inject constructor(
    private val dragaliaApi: DragaliaLifeApi,
    private val disposables: CompositeDisposable
) : DataSource.Factory<Int, ComicThumbnailData>() {

    var dataSourceListener: DataSourceCallback? = null

    override fun create(): DataSource<Int, ComicThumbnailData> {
        return ThumbnailComicDataSource(dragaliaApi, disposables).apply {
            setDataSourceListener(dataSourceListener)
        }
    }
}

/**
 * A data source listener for the [ThumbnailComicDataSource] class.
 */
abstract class DataSourceCallback {
    abstract fun onLoadAfter()
    abstract fun onLoadInitial()
}

