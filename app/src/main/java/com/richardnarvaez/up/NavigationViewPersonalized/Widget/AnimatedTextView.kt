package com.richardnarvaez.up.NavigationViewPersonalized.Widget

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

class AnimatedTextView(context: Context, attrs: AttributeSet? = null)
    : AppCompatTextView(context, attrs), AnimatedView {

    fun setAnimatedText(text: CharSequence, startDelay: Long = 0L) {
        changeText(text, startDelay)
    }

    private fun changeText(newText: CharSequence, startDelay: Long) {
        if (text == newText)
            return
        animate(view = this, startDelay = startDelay) {
            text = newText
        }
    }
}