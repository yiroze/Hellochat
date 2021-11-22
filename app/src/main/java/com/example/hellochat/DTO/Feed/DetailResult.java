package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailResult {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public List<DetailData> body;

    @Override
    public String toString() {
        return "DetailResult{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
