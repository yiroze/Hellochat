package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommentData {
    @SerializedName("result")
    public String result;

    @SerializedName("accept_user")
    public int accept_user;

    @SerializedName("comment_idx")
    public int comment_idx;

    @Override
    public String toString() {
        return "CommentData{" +
                "result='" + result + '\'' +
                ", accept_user=" + accept_user +
                ", comment_idx=" + comment_idx +
                '}';
    }
}
