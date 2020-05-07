package com.richardnarvaez.up.Model

/**
 * Created by macbookpro on 2/27/18.
 */

class Comment {
    var user_uid: String = ""
    var text: String = ""
    var timestamp: Any = ""

    constructor()

    constructor(_user_uid: String, text: String, timestamp: Any) {
        this.user_uid = _user_uid
        this.text = text
        this.timestamp = timestamp
    }
}
