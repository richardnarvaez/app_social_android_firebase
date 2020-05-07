package com.richardnarvaez.up.domain.entity

import android.annotation.SuppressLint
import android.os.Parcelable
import com.richardnarvaez.up.NavigationViewPersonalized.Utils.Extensions.emptyString
import kotlinx.android.parcel.Parcelize

/**
 * Created by Richard Narvaez on 30/12/2017.
 */

@SuppressLint("ParcelCreator")
@Parcelize
data class User constructor(val name: String?,
                            val avatarUrl: String = emptyString,
                            val username: String?,
                            val location: String?) : Parcelable