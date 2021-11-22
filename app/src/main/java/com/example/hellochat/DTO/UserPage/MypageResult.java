package com.example.hellochat.DTO.UserPage;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MypageResult {

    @SerializedName("result")
    public String result;

    @SerializedName("body")
    public ArrayList<MypageData> body;

    @Override
    public String toString() {
        return "MypageResult{" +
                "result='" + result + '\'' +
                ", body=" + body +
                '}';
    }
}
