package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReplyResult {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<ReplyList> body;

    @Override
    public String toString() {
        return "ReplyResult{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
