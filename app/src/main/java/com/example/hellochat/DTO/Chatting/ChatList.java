package com.example.hellochat.DTO.Chatting;

import com.google.gson.annotations.SerializedName;

public class ChatList {
    @SerializedName("profile")
    public String profile;

    @SerializedName("name")
    public String name;

    @SerializedName("content")
    public String content;

    @SerializedName("date")
    public long date;

    @SerializedName("friend_idx")
    public int friend_idx;

    @SerializedName("new_msg_cnt")
    public int new_msg_cnt;


    @SerializedName("content_type")
    public int content_type;


    @Override
    public String toString() {
        return "ChatList{" +
                "profile='" + profile + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", friend_idx=" + friend_idx +
                ", new_msg_cnt=" + new_msg_cnt +
                ", content_type=" + content_type +
                '}';
    }
}
