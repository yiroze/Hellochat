package com.example.hellochat.DTO.UserPage;

import com.google.gson.annotations.SerializedName;

public class MypageData {


    @SerializedName("follower")
    public int follower;

    @SerializedName("following")
    public int following;

    @SerializedName("feed_idx")
    public int feed_idx;

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


    @SerializedName("like_cnt")
    public String like_cnt;

    @SerializedName("comment_cnt")
    public String comment_cnt;

    @SerializedName("islike")
    public int islike;

    @SerializedName("feed_cnt")
    public String feed_cnt;

    @SerializedName("all_like_cnt")
    public int all_like_cnt;

    @SerializedName("view_type")
    public int view_type;
    //뷰타입  0 =헤더 1 =게시글

    @SerializedName("record")
    public String record;

    @SerializedName("image")
    public String image;

    @Override
    public String toString() {
        return "MypageData{" +
                "follower=" + follower +
                ", following=" + following +
                ", feed_idx=" + feed_idx +
                ", user_idx=" + user_idx +
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
                ", like_cnt='" + like_cnt + '\'' +
                ", comment_cnt='" + comment_cnt + '\'' +
                ", islike=" + islike +
                ", feed_cnt='" + feed_cnt + '\'' +
                ", all_like_cnt=" + all_like_cnt +
                ", view_type=" + view_type +
                ", record='" + record + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public int getView_type() {
        return view_type;
    }


}
