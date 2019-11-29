package com.example.wyrmprint.ui.browse.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.api.load
import com.example.wyrmprint.R
import com.example.wyrmprint.data.model.ComicThumbnailData
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

open class ThumbnailItemView(private val thumbnailData: ComicThumbnailData?) :
    AbstractItem<ThumbnailItemView.ThumbnailCardHolder>() {
    override val layoutRes: Int
        get() = R.layout.thumbnail_item
    override val type: Int
        get() = R.id.thumbnail_item

    override fun getViewHolder(v: View): ThumbnailCardHolder = ThumbnailCardHolder(v)

    class ThumbnailCardHolder(view: View) : FastAdapter.ViewHolder<ThumbnailItemView>(view) {
        private val thumbnailImage: ImageView? = view.findViewById(R.id.thumbnail_item)
        private val thumnailTitle: TextView? = view.findViewById(R.id.thumbnail_title)

        override fun bindView(item: ThumbnailItemView, payloads: MutableList<Any>) {
            thumbnailImage?.load(item.thumbnailData?.thumbnailLarge) {
                placeholder(R.drawable.loading_placeholder)
            }
            thumnailTitle?.text = item.thumbnailData?.title
        }

        override fun unbindView(item: ThumbnailItemView) {}
    }
}