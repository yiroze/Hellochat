package com.example.hellochat;

import com.google.gson.annotations.SerializedName;

public class CheckEmail {
    @SerializedName("response")
    public String response;

    @Override
    public String toString() {
        return "JoinData{" +
                "response='" + response + '\'' +
                '}';
    }
}
