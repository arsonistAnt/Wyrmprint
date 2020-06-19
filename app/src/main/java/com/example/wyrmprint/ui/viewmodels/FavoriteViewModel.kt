package com.example.wyrmprint.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wyrmprint.data.database.repository.ComicRepository
import com.example.wyrmprint.data.model.ThumbnailFavorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(private val comicRepository: ComicRepository) :
    ViewModel() {

    // Live data of ThumbnailFavorite entities from the comic database.
    val favoriteList: LiveData<List<ThumbnailFavorite>> = comicRepository.getFavoriteComics()

    /**
     * Remove favorited comics from the comic database.
     *
     * @param favorites list of [ThumbnailFavorite] objects to remove.
     */
    fun removeFavorites(favorites: List<ThumbnailFavorite>) {
        viewModelScope.launch(Dispatchers.IO) {
            comicRepository.removeFavoriteComics(favorites)
            comicRepository.updateThumbnailFavoritesField(favorites.map { it.comicId }, false)
        }
    }
}