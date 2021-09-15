package com.example.hellochat;

import com.google.gson.annotations.SerializedName;

public class EditData {

    @SerializedName("result")
    public String result;

    @SerializedName("msg")
    public String msg;

    @Override
    public String toString() {
        return "EditData{" +
                "result='" + result + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
