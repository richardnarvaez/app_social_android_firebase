package com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions

import android.util.Log


/**
 * Created by Richard Narvaez on 30/12/2017.
 */

inline fun log(message: () -> Any?) {
    Log.e("TAG", "Mess: " + message())
}

inline fun <reified T> T.withLog(): T {
    log("${T::class.java.simpleName} $this")
    return this
}

fun log(vararg message: () -> Any?) {
    message.forEach {
        log(it())
    }
}

fun log(message: Any?) {
    Log.e("TAG", "Mess:$message")
}