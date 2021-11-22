package com.example.hellochat.DTO.UserPage;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserPageResult {
    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<UserPageData> body;

    @Override
    public String toString() {
        return "UserPageResult{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
