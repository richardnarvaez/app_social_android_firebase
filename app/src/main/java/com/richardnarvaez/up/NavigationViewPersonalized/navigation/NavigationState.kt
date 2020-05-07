package com.richardnarvaez.up.NavigationViewPersonalized.navigation

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

data class NavigationState constructor(
        var activeTag: String? = null,
        var firstTag: String? = null,
        var isCustomAnimationUsed: Boolean = false) {

    fun clear() {
        activeTag = null
        firstTag = null
    }
}