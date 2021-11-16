package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

public class NotificationData {
    @SerializedName("feed_idx")
    public int feed_idx;

    @SerializedName("user_idx")
    public int user_idx;

    @SerializedName("date")
    public String date;

    @SerializedName("name")
    public String name;

    @SerializedName("profile")
    public String profile;

    @SerializedName("content")
    public String content;

    @SerializedName("image")
    public String image;

    @SerializedName("record")
    public String record;

    @SerializedName("comment")
    public String comment;

    @SerializedName("type")
    public int type;

    @Override
    public String toString() {
        return "NotificationData{" +
                "feed_idx=" + feed_idx +
                ", user_idx=" + user_idx +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", record='" + record + '\'' +
                ", comment='" + comment + '\'' +
                ", type=" + type +
                '}';
    }
}
