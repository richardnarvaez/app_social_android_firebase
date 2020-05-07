package com.richardnarvaez.up.NavigationViewPersonalized.Widget.NavigationDrawer

import com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions.emptyString

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

sealed class NavigationId(val name: String = emptyString, val fullName: String = emptyString) {

    object SHOT : NavigationId("HOME")
    object TOP_PHOTO : NavigationId("INFLUENCER")
    object TOP_CHALLENGE : NavigationId("CHALLENGE")
    object USER_LIKES : NavigationId("MAP")
    object FOLLOWING : NavigationId("POST SAVED")
    object ABOUT : NavigationId("APP INFO")
    object LOG_OUT : NavigationId("LOG OUT")
    //object SHOT_DETAIL : NavigationId("SHOT DETAIL", ShotDetailFragment::class.java.name)
}