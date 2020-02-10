package com.example.wyrmprint.data.remote.pager

import androidx.paging.DataSource
import com.example.wyrmprint.data.database.ThumbnailDao
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Factory class that handles the creation of [ThumbnailComicDataSource] objects.
 */
class ThumbnailDataSourceFactory @Inject constructor(
    private val dragaliaApi: DragaliaLifeApi,
    private val disposables: CompositeDisposable,
    private val thumbnailDao: ThumbnailDao
) : DataSource.Factory<Int, ThumbnailData>() {

    var dataSourceListener: DataSourceCallback? = null
    lateinit var mainDataSource : DataSource<Int, ThumbnailData>

    override fun create(): DataSource<Int, ThumbnailData> {
        mainDataSource = ThumbnailComicDataSource(dragaliaApi, disposables, thumbnailDao).apply {
            setDataSourceListener(dataSourceListener)
        }
        return mainDataSource
    }
}

/**
 * A data source listener for the [ThumbnailComicDataSource] class.
 */
abstract class DataSourceCallback {
    abstract fun onLoadAfter()
    abstract fun onLoadInitial()
}

