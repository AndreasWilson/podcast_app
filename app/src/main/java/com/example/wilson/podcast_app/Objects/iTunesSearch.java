package com.example.wilson.podcast_app.Objects;

/**
 * Created by andre on 13.08.2017.
 */

public class iTunesSearch {
    private String name, podcastUrl, imageUrl, podcastID;

    public void setName(String name) {
        this.name = name;
    }

    public void setPodcastUrl(String podcastUrl) {
        this.podcastUrl = podcastUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPodcastID(String podcastID) {
        this.podcastID = podcastID;
    }

    public String getName() {
        return name;
    }

    public String getPodcastUrl() {
        return podcastUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPodcastID() {
        return podcastID;
    }
}
