package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FollowResult {

    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<FollowData> body;

    @Override
    public String toString() {
        return "FollowResult{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
