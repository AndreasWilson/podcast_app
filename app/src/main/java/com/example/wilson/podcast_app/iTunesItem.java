package com.example.wilson.podcast_app;

/**
 * Created by andre on 26.03.2017.
 */

public class iTunesItem {
    int id;
    String name, img, url;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getUrl() {
        return url;
    }
}
