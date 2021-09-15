package com.example.hellochat;

import com.google.gson.annotations.SerializedName;

public class JoinData {
    @SerializedName("idx")
    public String idx;

    @SerializedName("email")
    public String email;

    @SerializedName("password")
    public String password;

    @SerializedName("name")
    public String name;

    @SerializedName("photo")
    public String photo;

    @SerializedName("my_language")
    public String my_language;

    @SerializedName("study_language")
    public String study_language;

    @SerializedName("level")
    public String level;

    @SerializedName("introduce")
    public String introduce;

    @SerializedName("msg")
    public String msg;

    @Override
    public String toString() {
        return "JoinData{" +
                "idx='" + idx + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", my_language='" + my_language + '\'' +
                ", study_language='" + study_language + '\'' +
                ", level='" + level + '\'' +
                ", introduce='" + introduce + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
