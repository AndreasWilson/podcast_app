package com.example.wilson.podcast_app.Objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wilson on 16.02.2017.
 */

public class Item implements Parcelable {
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

    public Item(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {

            return new Item[size];
        }

    };

    public void readFromParcel(Parcel in) {
        title = in.readString();
        desc = in.readString();
        img = in.readString();
        link = in.readString();

    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(img);
        dest.writeString(link);
    }
}
