package com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

@SuppressLint("NewApi")
fun TextView.iconTint(colorId: Int) {
    MorAbove {
        compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorId))
    }
}

fun View.onClick(function: () -> Unit) {
    setOnClickListener {
        function()
    }
}

var View.scale: Float
    get() = Math.min(scaleX, scaleY)
    set(value) {
        scaleY = value
        scaleX = value
    }

infix fun ViewGroup.inflate(layoutResId: Int): View =
        LayoutInflater.from(context).inflate(layoutResId, this, false)

fun ImageView.tint(colorId: Int) {
    setColorFilter(context.takeColor(colorId), PorterDuff.Mode.SRC_IN)
}