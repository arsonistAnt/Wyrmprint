package com.example.wyrmprint.ui.browse.viewholder

import android.view.View
import android.widget.Button
import com.example.wyrmprint.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.retry_item_view_holder.view.*

open class RetryItemView(private val action: () -> Unit) :
    AbstractItem<RetryItemView.RetryViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.retry_item_view_holder
    override val type: Int
        get() = R.id.retry_connection_button

    override fun getViewHolder(v: View): RetryViewHolder {
        return RetryViewHolder(v, action)
    }

    class RetryViewHolder(view: View, private val retryAction: () -> Unit) :
        FastAdapter.ViewHolder<RetryItemView>(view) {
        private val retryButton: Button? = view.findViewById(R.id.retry_connection_button)

        override fun bindView(item: RetryItemView, payloads: MutableList<Any>) {
            retryButton?.setOnClickListener {
                retryAction()
            }
        }

        override fun unbindView(item: RetryItemView) {
        }
    }
}