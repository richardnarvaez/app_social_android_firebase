package com.richardnarvaez.up.NavigationViewPersonalized.navigation

/**
 * Created by Richard Narvaez on 30/12/2017.
 */
@com.richardnarvaez.up.NavigationViewPersonalized.Utils.Experimental
sealed class BackStrategy {

    object KEEP : BackStrategy()
    object DESTROY : BackStrategy()
}