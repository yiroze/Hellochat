package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

public class GetContents {

    @SerializedName("result")
    public String result;

    @SerializedName("contents")
    public String contents;

    @SerializedName("image")
    public String image;

    @SerializedName("Record")
    public String Record;

    @Override
    public String toString() {
        return "GetContents{" +
                "result='" + result + '\'' +
                ", contents='" + contents + '\'' +
                ", image='" + image + '\'' +
                ", Record='" + Record + '\'' +
                '}';
    }
}
