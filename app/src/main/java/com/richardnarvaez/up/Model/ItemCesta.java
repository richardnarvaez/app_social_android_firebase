package com.richardnarvaez.up.Model;

import java.io.Serializable;

/**
 * Created by RICHARD on 24/04/2017.
 */

public class ItemCesta implements Serializable {
    private String id;
    private String title;
    private float price;
    private String thumbnailURL;
    private float duration;
    private String viewCount;

    public ItemCesta(String itemId, String title, String thumbnailUrl, String price, String description) {
        this.id = "";
        this.title = "";
        this.thumbnailURL = "";
        this.duration = 0;
        this.viewCount = "";
    }

    public ItemCesta(ItemCesta newVideo) {
        this.id = newVideo.id;
        this.title = newVideo.title;
        this.thumbnailURL = newVideo.thumbnailURL;
        this.duration = newVideo.duration;
        this.viewCount = newVideo.viewCount;
    }

    public ItemCesta(String id, String title, String thumbnailURL, float duration, String viewCount) {
        this.id = id;
        this.title = title;
        this.thumbnailURL = thumbnailURL;
        this.price = duration;
        this.viewCount = viewCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public float getPrice() {
        return price;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public String toString() {
        return "YouTubeVideo {" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

}

