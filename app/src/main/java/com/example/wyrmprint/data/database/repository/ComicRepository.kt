package com.example.wyrmprint.data.database.repository

import com.example.wyrmprint.data.database.ThumbnailCacheDao
import com.example.wyrmprint.data.database.ThumbnailFavoritesDao
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.toFavoriteThumbnail
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.pager.ThumbnailDataSourceFactory
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import java.util.stream.Collectors.toList
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
        ThumbnailDataSourceFactory(dragaliaApi, compositeDisposable, thumbnailCacheDao)

    /**
     * Insert the [ThumbnailData] into the favorites table in the database.
     *
     * @param thumbnailData the comic thumbnail data to insert.
     */
    suspend fun saveFavoriteComic(thumbnailData: ThumbnailData) {
        favoritesDao.insertFavorites(listOf(thumbnailData.toFavoriteThumbnail()))
    }

    /**
     * Return a list of favorited comics from the thumbnails database.
     *
     * @return a list of favorited thumbnails.
     */
    fun getFavoriteComics() = favoritesDao.getFavorites()
}