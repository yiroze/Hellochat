package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChatData {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<Chatting> body;

    @Override
    public String toString() {
        return "ChatData{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
