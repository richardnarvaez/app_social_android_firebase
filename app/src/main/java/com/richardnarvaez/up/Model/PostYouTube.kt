package com.richardnarvaez.up.Model

class PostYouTube : Post {

    var video: String? = null
    var credits: String? = null

    constructor()

    constructor(_video: String, _credits: String) {
        this.video = _video
        this.credits = _credits
    }
}