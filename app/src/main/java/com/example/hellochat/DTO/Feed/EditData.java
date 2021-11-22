package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

public class EditData {

    @SerializedName("result")
    public String result;

    @SerializedName("msg")
    public String msg;

    @SerializedName("idx")
    public int idx;

    @Override
    public String toString() {
        return "EditData{" +
                "result='" + result + '\'' +
                ", msg='" + msg + '\'' +
                ", idx='" + idx + '\'' +
                '}';
    }
}
