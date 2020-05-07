@file:JvmName("MathUtils")

package com.richardnarvaez.up.Utility

fun constrain(min: Float, max: Float, v: Float) = v.coerceIn(min, max)