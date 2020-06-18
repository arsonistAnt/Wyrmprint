package com.example.wyrmprint.ui.viewmodels

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.wyrmprint.data.database.repository.ComicRepository
import com.example.wyrmprint.data.model.NetworkStatus
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.remote.pager.ThumbnailComicDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class BrowserViewModel @Inject constructor(private val comicRepo: ComicRepository) : ViewModel() {
    // PagedList to load thumbnail comic items.
    private val thumbnailDataSource: MutableLiveData<ThumbnailComicDataSource>
    private val thumbnailPagedListConfig: PagedList.Config =
        PagedList.Config.Builder().setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(10)
            .build()
    val thumbnailNetworkStatus: LiveData<NetworkStatus>
    val thumbnailDataItemPageList: LiveData<PagedList<ThumbnailData>>

    init {
        // Create thumbnail data source factory and use the paged list builder to create a paged list.
        val thumbnailDataSourceFactory = comicRepo.getThumbnailDataSourceFactory()
        thumbnailDataSource = thumbnailDataSourceFactory.currThumbnailDataSource
        // Get the thumbnail network state LiveData and assign it to the ViewModel.
        thumbnailNetworkStatus = Transformations.switchMap(thumbnailDataSource) {
            it.thumbnailNetworkState
        }
        thumbnailDataItemPageList = LivePagedListBuilder(
            thumbnailDataSourceFactory,
            thumbnailPagedListConfig
        ).build()
    }

    /**
     * Invalidate the thumbnail data source and start reloading new data.
     */
    fun invalidateThumbnailData() {
        viewModelScope.launch(Dispatchers.IO) {
            thumbnailDataSource.value?.invalidate()
        }
    }

    /**
     * Add this thumbnail to the favorites section in the comic repository.
     */
    fun addToFavorites(thumbnailData: ThumbnailData) {
        viewModelScope.launch(Dispatchers.IO) {
            comicRepo.saveFavoriteComic(thumbnailData)
        }
    }
}