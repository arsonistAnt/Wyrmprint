package com.example.wyrmprint.ui.browse.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.api.load
import com.example.wyrmprint.R
import com.example.wyrmprint.data.model.ThumbnailData
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

open class ThumbnailItemView(val thumbnailData: ThumbnailData?) :
    AbstractItem<ThumbnailItemView.ThumbnailCardHolder>() {
    override val layoutRes: Int
        get() = R.layout.thumbnail_item
    override val type: Int
        get() = R.id.thumbnail_item

    override fun getViewHolder(v: View): ThumbnailCardHolder = ThumbnailCardHolder(v)

    class ThumbnailCardHolder(view: View) : FastAdapter.ViewHolder<ThumbnailItemView>(view) {
        private val thumbnailImage: ImageView? = view.findViewById(R.id.thumbnail_item)
        private val thumbnailTitle: TextView? = view.findViewById(R.id.thumbnail_title)
        private val thumbnailComicNum: TextView? = view.findViewById(R.id.comic_number)

        override fun bindView(item: ThumbnailItemView, payloads: MutableList<Any>) {
            thumbnailImage?.load(item.thumbnailData?.thumbnailLarge) {
                placeholder(R.drawable.loading_placeholder)
            }
            thumbnailTitle?.text = item.thumbnailData?.comicTitle.toString()
            thumbnailComicNum?.text = item.thumbnailData?.comicNumber.toString()
        }

        override fun unbindView(item: ThumbnailItemView) {
            thumbnailTitle?.text = ""
            thumbnailComicNum?.text = ""
        }
    }
}