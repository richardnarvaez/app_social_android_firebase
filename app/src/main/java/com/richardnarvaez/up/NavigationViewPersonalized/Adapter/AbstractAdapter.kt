package com.richardnarvaez.up.NavigationViewPersonalized.Adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions.inflate

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

abstract class AbstractAdapter<ITEM> constructor(protected var itemList: List<ITEM>,
                                                 private val layoutResId: Int)
    : RecyclerView.Adapter<com.richardnarvaez.up.NavigationViewPersonalized.Adapter.AbstractAdapter.Holder>() {

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.richardnarvaez.up.NavigationViewPersonalized.Adapter.AbstractAdapter.Holder {
        val view = parent inflate layoutResId
        val viewHolder = com.richardnarvaez.up.NavigationViewPersonalized.Adapter.AbstractAdapter.Holder(view)
        val itemView = viewHolder.itemView
        itemView.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(itemView, adapterPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: com.richardnarvaez.up.NavigationViewPersonalized.Adapter.AbstractAdapter.Holder, position: Int) {
        val item = itemList[position]
        holder.itemView.bind(item)
    }

    fun update(items: List<ITEM>) {
        updateAdapterWithDiffResult(calculateDiff(items))
    }

    private fun updateAdapterWithDiffResult(result: DiffUtil.DiffResult) {
        result.dispatchUpdatesTo(this)
    }

    private fun calculateDiff(newItems: List<ITEM>) =
            DiffUtil.calculateDiff(com.richardnarvaez.up.NavigationViewPersonalized.Adapter.DiffUtilCallback(itemList, newItems))

    fun add(item: ITEM) {
        itemList.toMutableList().add(item)
        notifyItemInserted(itemList.size)
    }

    fun remove(position: Int) {
        itemList.toMutableList().removeAt(position)
        notifyItemRemoved(position)
    }

    final override fun onViewRecycled(holder: com.richardnarvaez.up.NavigationViewPersonalized.Adapter.AbstractAdapter.Holder) {
        super.onViewRecycled(holder)
        onViewRecycled(holder.itemView)
    }

    protected open fun onViewRecycled(itemView: View) {
    }

    protected open fun onItemClick(itemView: View, position: Int) {
        Log.e("TAG", "CLICK en : " + position);
    }

    protected open fun View.bind(item: ITEM) {

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}