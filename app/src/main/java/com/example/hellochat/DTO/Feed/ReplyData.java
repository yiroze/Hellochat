package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReplyData {
    @SerializedName("result")
    public String result;

    @SerializedName("accept_user")
    public int accept_user;

    @SerializedName("comment_idx")
    public int comment_idx;

    @SerializedName("body")
    public ArrayList<ReplyList> body;

    @Override
    public String toString() {
        return "ReplyData{" +
                "result='" + result + '\'' +
                ", accept_user=" + accept_user +
                ", comment_idx=" + comment_idx +
                ", body=" + body +
                '}';
    }
}
