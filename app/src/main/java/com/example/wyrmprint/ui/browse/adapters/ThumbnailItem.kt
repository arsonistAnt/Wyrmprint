package com.example.wyrmprint.ui.browse.adapters

import android.view.View
import android.widget.ImageView
import coil.api.load
import com.example.wyrmprint.R
import com.example.wyrmprint.data.local.ComicThumbnail
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

open class ThumbnailItem(private val thumbnail: ComicThumbnail) :
    AbstractItem<ThumbnailItem.ThumbnailCardHolder>() {
    override val layoutRes: Int
        get() = R.layout.thumbnail_item
    override val type: Int
        get() = R.id.thumbnail_item

    override fun getViewHolder(v: View): ThumbnailCardHolder = ThumbnailCardHolder(v)

    class ThumbnailCardHolder(view: View) : FastAdapter.ViewHolder<ThumbnailItem>(view) {
        private val thumbnailImage: ImageView? = view.findViewById(R.id.thumbnail_item)

        override fun bindView(item: ThumbnailItem, payloads: MutableList<Any>) {
            thumbnailImage?.load(item.thumbnail.thumbnailLarge) {

            }
        }

        override fun unbindView(item: ThumbnailItem) {}
    }
}