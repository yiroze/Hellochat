package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationResult {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<NotificationData> body;

    @Override
    public String toString() {
        return "NotificationResult{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
