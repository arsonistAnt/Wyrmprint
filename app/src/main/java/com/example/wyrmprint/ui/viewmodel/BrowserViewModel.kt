package com.example.wyrmprint.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.remote.repository.ComicRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class BrowserViewModel @Inject constructor(private val comicRepo: ComicRepository) : ViewModel() {
    // PagedList to load thumbnail comic items.
    private val _thumbnailPageList = comicRepo.getThumbnailDataSourceFactory()
    val thumbnailDataItemPageList: LiveData<PagedList<ThumbnailData>>
        get() = _thumbnailPageList

    /**
     * Refresh the data by removing all thumbnail records in the thumbnail_data table.
     */
    fun refreshAsync() {
        viewModelScope.launch {
            comicRepo.initRefresh()
        }
    }
}