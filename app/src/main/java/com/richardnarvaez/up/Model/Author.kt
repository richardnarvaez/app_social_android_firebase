package com.richardnarvaez.up.Model

/**
 * Created by RICHARD on 07/04/2017.
 */


class Author {

    var name: String = ""
    var user_name: String = ""
    var profile_picture: String = ""
    var email: String = ""
    var uid: String = ""
    var cover: String = ""

    constructor()

    constructor(_full_name: String, _profile_picture: String, _email: String, _uid: String, _user_name: String, _cover: String) {
        this.name = _full_name
        this.profile_picture = _profile_picture
        this.email = _email
        this.uid = _uid
        this.user_name = _user_name
        this.cover = _cover
    }

}
