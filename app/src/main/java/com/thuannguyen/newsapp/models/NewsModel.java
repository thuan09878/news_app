package com.thuannguyen.newsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsModel implements Parcelable {
    private String title;
    private String pubDate;
    private String description;
    private String link;
    private String guid;

    public NewsModel(String title, String pubDate, String description, String link, String guid) {
        this.title = title;
        this.pubDate = pubDate;
        this.description = description;
        this.link = link;
        this.guid = guid;
    }

    protected NewsModel(Parcel in) {
        title = in.readString();
        pubDate = in.readString();
        description = in.readString();
        link = in.readString();
        guid = in.readString();
    }

    public static final Creator<NewsModel> CREATOR = new Creator<NewsModel>() {
        @Override
        public NewsModel createFromParcel(Parcel in) {
            return new NewsModel(in);
        }

        @Override
        public NewsModel[] newArray(int size) {
            return new NewsModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(pubDate);
        dest.writeString(description);
        dest.writeString(link);
        dest.writeString(guid);
    }
}
