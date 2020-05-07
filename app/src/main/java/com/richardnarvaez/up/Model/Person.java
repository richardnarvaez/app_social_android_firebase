package com.richardnarvaez.up.Model;


import java.util.Map;

/**
 * Created by richardnarvaez on 5/30/17.
 */

public class Person {
    private String displayName;
    private String photoUrl;
    private Map<String, Long> products;
    private Map<String, Object> following;

    public Person() {

    }

    public Person(String displayName, String profile_picture) {
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Map<String, Long> getProducts() {
        return products;
    }

    public Map<String, Object> getFollowing() {
        return following;
    }
}