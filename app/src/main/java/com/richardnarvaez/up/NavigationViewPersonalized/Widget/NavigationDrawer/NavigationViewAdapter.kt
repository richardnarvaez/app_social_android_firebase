package com.richardnarvaez.up.NavigationViewPersonalized.Widget.NavigationDrawer

import android.view.View
import com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions.C
import com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions.L
import com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions.takeColor
import com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions.tint
import kotlinx.android.synthetic.main.item_navigation_view.view.*

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

class NavigationViewAdapter constructor(navigationItemList: MutableList<NavigationItem>,
                                        private var itemClickListener: ItemClickListener?)
    : com.richardnarvaez.up.NavigationViewPersonalized.Adapter.AbstractAdapter<NavigationItem>(navigationItemList, L.item_navigation_view) {

    override fun onItemClick(itemView: View, position: Int) {
        itemClickListener?.let {
            it(itemList[position], position)
        }
    }

    override fun View.bind(item: NavigationItem) {
        itemText.text = item.name
        itemIcon.setImageResource(item.icon)
        itemIcon.tint(item.itemIconColor)
        if (item.isSelected) {
            itemText.setTextColor(context.takeColor(C.colorAccent))
        } else {
            itemText.setTextColor(context.takeColor(C.colorNoSelect))
        }
    }
}