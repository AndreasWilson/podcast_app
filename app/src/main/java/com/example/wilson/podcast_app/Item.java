package com.example.wilson.podcast_app;

/**
 * Created by wilson on 16.02.2017.
 */

public class Item {
    public String title = null;
    public String desc = null;
    public String img = null;
    public String link = null;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }
    public String getDesc() {
        return desc;
    }
    public String getImg() {
        return img;
    }
    public String getLink() {
        return link;
    }
}
