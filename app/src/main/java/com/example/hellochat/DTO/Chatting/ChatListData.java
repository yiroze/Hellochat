package com.example.hellochat.DTO.Chatting;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ChatListData {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<ChatList> body;
}
