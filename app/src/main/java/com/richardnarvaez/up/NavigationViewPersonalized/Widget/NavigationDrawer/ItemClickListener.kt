package com.richardnarvaez.up.NavigationViewPersonalized.Widget.NavigationDrawer

/**
 * Created by Richard Narvaez on 30/12/2017.
 */
interface ItemClickListener {

    operator fun invoke(item: NavigationItem, position: Int)
}