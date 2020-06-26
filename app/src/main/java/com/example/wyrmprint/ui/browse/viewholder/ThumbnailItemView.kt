package com.example.wyrmprint.ui.browse.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.api.load
import com.example.wyrmprint.R
import com.example.wyrmprint.data.model.ThumbnailData
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem

open class ThumbnailItemView(
    thumbnailData: ThumbnailData,
    autoHighlight: Boolean = false
) :
    ModelAbstractItem<ThumbnailData, ThumbnailItemView.ThumbnailCardHolder>(thumbnailData) {
    override val layoutRes: Int
        get() = R.layout.thumbnail_item
    override val type: Int
        get() = R.id.thumbnail_image_container

    init {
        // If the thumbnailData is favorited then automatically "select" the item view.
        if (autoHighlight)
            shouldSelectAsFavorite()
    }

    override fun getViewHolder(v: View): ThumbnailCardHolder = ThumbnailCardHolder(v)

    /**
     * Checks if the [ThumbnailItemView] status should be set as selected.
     */
    private fun shouldSelectAsFavorite() {
        model.apply {
            isSelected = model.isFavorite
        }
    }

    class ThumbnailCardHolder(view: View) : FastAdapter.ViewHolder<ThumbnailItemView>(view) {
        private val thumbnailImage: ImageView? = view.findViewById(R.id.thumbnail_image)
        private val thumbnailTitle: TextView? = view.findViewById(R.id.thumbnail_title)
        private val thumbnailComicNum: TextView? = view.findViewById(R.id.comic_number)

        override fun bindView(item: ThumbnailItemView, payloads: MutableList<Any>) {
            thumbnailImage?.load(item.model.thumbnailLarge) {
                placeholder(R.drawable.loading_placeholder)
                error(R.drawable.error_connection)
            }
            thumbnailTitle?.text = item.model.comicTitle
            thumbnailComicNum?.text = item.model.comicNumber.toString()
        }

        override fun unbindView(item: ThumbnailItemView) {
            thumbnailTitle?.text = ""
            thumbnailComicNum?.text = ""
        }
    }
}