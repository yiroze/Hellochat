package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

public class ReplyList {

    @SerializedName("user_idx")
    public int user_idx;

    @SerializedName("name")
    public String name;

    @SerializedName("profile")
    public String profile;

    @SerializedName("contents")
    public String contents;

    @SerializedName("date")
    public String date;

    @SerializedName("comment_idx")
    public int comment_idx;

    @SerializedName("record")
    public String record;

    @Override
    public String toString() {
        return "ReplyList{" +
                "user_idx=" + user_idx +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", contents='" + contents + '\'' +
                ", date='" + date + '\'' +
                ", comment_idx=" + comment_idx +
                ", record='" + record + '\'' +
                '}';
    }

    public ReplyList(int user_idx, String name, String profile, String contents, String date, int comment_idx, String record) {
        this.user_idx = user_idx;
        this.name = name;
        this.profile = profile;
        this.contents = contents;
        this.date = date;
        this.comment_idx = comment_idx;
        this.record = record;
    }
}
