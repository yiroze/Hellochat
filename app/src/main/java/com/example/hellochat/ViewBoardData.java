package com.example.hellochat;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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
