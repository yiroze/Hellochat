package com.example.hellochat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultData {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public String body;

    @Override
    public String toString() {
        return "ResultData{" +
                "result='" + result + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
