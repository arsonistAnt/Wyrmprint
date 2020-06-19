package com.example.wyrmprint.data.database.repository

import com.example.wyrmprint.data.database.ThumbnailCacheDao
import com.example.wyrmprint.data.database.ThumbnailFavoritesDao
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.ThumbnailFavorite
import com.example.wyrmprint.data.model.toFavoriteThumbnail
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.pager.ThumbnailDataSourceFactory
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class ComicRepository @Inject constructor(
    private var dragaliaApi: DragaliaLifeApi,
    private var compositeDisposable: CompositeDisposable,
    private var thumbnailCacheDao: ThumbnailCacheDao,
    private var favoritesDao: ThumbnailFavoritesDao
) {

    /**
     * Get the comic information from [DragaliaLifeApi].
     */
    fun getComicDetail(comicId: Int) = dragaliaApi.fetchComicStripDetails(comicId)


    /**
     * Return a [ThumbnailDataSourceFactory]
     */
    fun getThumbnailDataSourceFactory() =
        ThumbnailDataSourceFactory(
            dragaliaApi,
            compositeDisposable,
            thumbnailCacheDao,
            favoritesDao
        )

    /**
     * Insert the [ThumbnailData] into the favorites table in the database.
     *
     * @param thumbnailData the comic thumbnail data to insert.
     */
    fun saveFavoriteComic(thumbnailData: ThumbnailData) {
        favoritesDao.insertFavorites(listOf(thumbnailData.toFavoriteThumbnail()))
    }

    /**
     * Return a list of favorited comics from the thumbnails database.
     *
     * @return a list of favorited thumbnails.
     */
    fun getFavoriteComics() = favoritesDao.getFavorites()

    /**
     * Delete favorited comics from the database.
     *
     * @param favoriteComics a list of [ThumbnailFavorite] objects
     */
    fun removeFavoriteComics(favoriteComics: List<ThumbnailFavorite>) {
        favoritesDao.deleteFavoriteRecords(favoriteComics)
    }

    /**
     * Update the thumbnail data field in the [thumbnailCacheDao].
     */
    fun updateCachedThumbnail(thumbnailData: ThumbnailData) {
        thumbnailCacheDao.updateThumbnailData(thumbnailData)
    }

    /**
     * Update the favorite field of the [ThumbnailData] in the [thumbnailCacheDao]
     */
    fun updateThumbnailFavoritesField(comicIdList: List<Int>, isFavorite: Boolean) {
        thumbnailCacheDao.updateFavorites(comicIdList, isFavorite)
    }
}