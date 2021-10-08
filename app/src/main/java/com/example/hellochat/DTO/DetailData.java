package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

public class DetailData {


    @SerializedName("user_idx")
    public int user_idx;

    @SerializedName("name")
    public String name;

    @SerializedName("profile")
    public String profile;

    @SerializedName("mylang")
    public String mylang;

    @SerializedName("mylang2")
    public String mylang2;

    @SerializedName("mylang3")
    public String mylang3;

    @SerializedName("studylang")
    public String studylang;

    @SerializedName("studylang2")
    public String studylang2;

    @SerializedName("studylang3")
    public String studylang3;

    @SerializedName("contents")
    public String contents;

    @SerializedName("date")
    public String date;

    @SerializedName("studylang_level")
    public int studylang_level;

    @SerializedName("studylang_level2")
    public int studylang_level2;

    @SerializedName("studylang_level3")
    public int studylang_level3;

    @SerializedName("heart")
    public int heart;

    @SerializedName("comment")
    public int comment;

    @SerializedName("view_type")
    public int view_type;
    //뷰타입  0 =헤더 1 =댓글 2= 대댓글

    @SerializedName("parents")
    public int parents;

    @SerializedName("comment_idx")
    public int comment_idx;

    @SerializedName("islike")
    public int islike;

    @SerializedName("like_cnt")
    public int like_cnt;

    @SerializedName("comment_cnt")
    public int comment_cnt;

    @SerializedName("image")
    public String image;

    @SerializedName("record")
    public String record;

    public int getView_type() {
        return view_type;
    }

    @Override
    public String toString() {
        return "DetailData{" +
                "user_idx=" + user_idx +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", mylang='" + mylang + '\'' +
                ", mylang2='" + mylang2 + '\'' +
                ", mylang3='" + mylang3 + '\'' +
                ", studylang='" + studylang + '\'' +
                ", studylang2='" + studylang2 + '\'' +
                ", studylang3='" + studylang3 + '\'' +
                ", contents='" + contents + '\'' +
                ", date='" + date + '\'' +
                ", studylang_level=" + studylang_level +
                ", studylang_level2=" + studylang_level2 +
                ", studylang_level3=" + studylang_level3 +
                ", heart=" + heart +
                ", comment=" + comment +
                ", view_type=" + view_type +
                ", parents=" + parents +
                ", comment_idx=" + comment_idx +
                ", islike=" + islike +
                ", like_cnt=" + like_cnt +
                ", comment_cnt=" + comment_cnt +
                ", image='" + image + '\'' +
                ", record='" + record + '\'' +
                '}';
    }
}
