package com.example.wyrmprint.data.remote.pager

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.wyrmprint.data.database.ThumbnailCacheDao
import com.example.wyrmprint.data.database.ThumbnailFavoritesDao
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
    private val thumbnailCacheDao: ThumbnailCacheDao,
    private val favoritesDao: ThumbnailFavoritesDao
) : DataSource.Factory<Int, ThumbnailData>() {

    // The current thumbnail data source that has been created.
    var currThumbnailDataSource = MutableLiveData<ThumbnailComicDataSource>()

    override fun create(): DataSource<Int, ThumbnailData> {
        return ThumbnailComicDataSource(
            dragaliaApi,
            disposables,
            thumbnailCacheDao,
            favoritesDao
        ).apply {
            currThumbnailDataSource.postValue(this)
        }
    }
}

