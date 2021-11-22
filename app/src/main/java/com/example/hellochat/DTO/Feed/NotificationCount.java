package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationCount {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public int body;

    @Override
    public String toString() {
        return "NotificationCount{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
