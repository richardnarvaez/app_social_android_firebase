package com.richardnarvaez.up.Model;

/**
 * Created by RICHARD on 24/04/2017.
 */


public class ItemFavorite {

    private String id;
    private String title;
    private long price;
    private String thumbnailURL;
    private String description;

    public ItemFavorite(String title, String thumbnailUrl, String id, long price, String description) {
        this.id = id;
        this.title = title;
        this.thumbnailURL = thumbnailUrl;
        this.price = price;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public long getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}
