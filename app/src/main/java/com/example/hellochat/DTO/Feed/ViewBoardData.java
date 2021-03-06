package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ViewBoardData {

    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<ViewData> body;

    @Override
    public String toString() {
        return "ViewBoardData{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
