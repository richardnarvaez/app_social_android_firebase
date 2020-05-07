package com.richardnarvaez.up.Model

/**
 * Created by RICHARD on 09/04/2017.
 */

open class Post {

    var ciudad: String? = null

    var user_uid: String? = null
    var full_url: String? = null
    var thumb_storage_uri: String? = null
    var thumbnail: String? = null
    var name: String? = null
    var description: String? = null
    var date: Any? = null
    var full_storage_uri: String? = null
    var uid_product: String? = null
    var price: Long? = null
    var category: String? = null
    var isSponsored: Boolean? = null


    constructor()

    constructor(_user_uid: String,
                full_url: String,
                full_storage_uri: String,
                thumbnail: String,
                price: Long,
                thumb_storage_uri: String,
                name: String,
                description: String,
                date: Any,
                uid_product: String,
                _video: String,
                _category: String,
                _isSponsored: Boolean,
                ciudad: String,
                pais: String) {

        this.user_uid = _user_uid
        this.full_url = full_url
        this.name = name
        this.description = description
        this.date = date
        this.thumb_storage_uri = thumb_storage_uri
        this.thumbnail = thumbnail
        this.full_storage_uri = full_storage_uri
        this.uid_product = uid_product
        this.price = price
        this.category = _category
        this.isSponsored = _isSponsored
        this.ciudad = ciudad

    }
}