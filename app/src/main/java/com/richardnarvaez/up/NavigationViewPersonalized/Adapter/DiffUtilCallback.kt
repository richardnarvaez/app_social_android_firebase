package com.richardnarvaez.up.NavigationViewPersonalized.Adapter


import android.support.v7.util.DiffUtil

/**
 * Created by Richard Narvaez on 30/12/2017.
 */


internal class DiffUtilCallback<ITEM>(private val oldItems: List<ITEM>,
                                      private val newItems: List<ITEM>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition] == newItems[newItemPosition]
}