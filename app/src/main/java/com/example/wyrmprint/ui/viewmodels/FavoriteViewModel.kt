package com.example.wyrmprint.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wyrmprint.data.database.repository.ComicRepository
import com.example.wyrmprint.data.model.ThumbnailFavorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(private val comicRepository: ComicRepository) :
    ViewModel() {

    val favoriteList: LiveData<List<ThumbnailFavorite>> = comicRepository.getFavoriteComics()
}