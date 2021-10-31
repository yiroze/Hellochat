package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

public class Chatting {

    @SerializedName("send_idx")
    public int send_idx;

    @SerializedName("accept_idx")
    public int accept_idx;

    @SerializedName("profile")
    public String profile;

    @SerializedName("name")
    public String name;

    @SerializedName("content")
    public String content;

    @SerializedName("date")
    public String date;

    @SerializedName("content_type")
    public int content_type;

    @SerializedName("checked")
    public int checked;
}
