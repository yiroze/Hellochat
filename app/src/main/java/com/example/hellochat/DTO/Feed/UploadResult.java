package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

public class UploadResult  {
    @SerializedName("result")
    public String result;

    @SerializedName("image")
    public String image;

    @Override
    public String toString() {
        return "UploadResult{" +
                "result=" + result +
                ", image=" + image +
                '}';
    }
}
