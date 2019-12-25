package com.example.wyrmprint.data.remote


import com.example.wyrmprint.data.local.ComicStrip
import com.example.wyrmprint.data.model.ThumbnailJson
import com.example.wyrmprint.util.DragaliaLifeUtil
import com.example.wyrmprint.util.DragaliaLifeUtil.Companion.formDataMap
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface DragaliaLifeService {

    @FormUrlEncoded
    @POST(DragaliaLifeUtil.apiComicDetail)
    fun comicDetail(
        @Path(DragaliaLifeUtil.comicDetailPath) comicId: Int,
        @FieldMap formData: Map<String, String> = formDataMap
    ): Observable<List<ComicStrip>>

    @FormUrlEncoded
    @POST(DragaliaLifeUtil.apiThumbnailPage)
    fun thumbnailPage(
        @Path(DragaliaLifeUtil.thumbnailPath) pageNumber: Int,
        @FieldMap formData: Map<String, String> = formDataMap
    ): Single<List<ThumbnailJson>>
}