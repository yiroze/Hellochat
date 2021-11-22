package com.example.hellochat.DTO.UserPage;

import com.google.gson.annotations.SerializedName;

public class FollowData {

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

    @SerializedName("studylang_level")
    public int studylang_level;

    @SerializedName("studylang_level2")
    public int studylang_level2;

    @SerializedName("studylang_level3")
    public int studylang_level3;

    @SerializedName("followed")
    public int followed;

    @Override
    public String toString() {
        return "FollowData{" +
                "user_idx=" + user_idx +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", mylang='" + mylang + '\'' +
                ", mylang2='" + mylang2 + '\'' +
                ", mylang3='" + mylang3 + '\'' +
                ", studylang='" + studylang + '\'' +
                ", studylang2='" + studylang2 + '\'' +
                ", studylang3='" + studylang3 + '\'' +
                ", studylang_level=" + studylang_level +
                ", studylang_level2=" + studylang_level2 +
                ", studylang_level3=" + studylang_level3 +
                ", followed=" + followed +
                '}';
    }
}
