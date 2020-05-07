package com.richardnarvaez.up.NavigationViewPersonalized.Widget.NavigationDrawer

import com.richardnarvaez.up.R

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

data class NavigationItem(val item: NavigationId,
                          val icon: Int,
                          var isSelected: Boolean = false,
                          val itemIconColor: Int = R.color.navigation_item_color) {

    val name: String
        get() = item.name

    val id: NavigationId
        get() = item
}