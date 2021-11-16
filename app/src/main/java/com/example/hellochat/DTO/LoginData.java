package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("idx")
    public String idx;

    @SerializedName("email")
    public String email;

    @SerializedName("name")
    public String name;

    @SerializedName("my_language")
    public String my_language;

    @SerializedName("study_language")
    public String study_language;

    @SerializedName("level")
    public String level;
    @SerializedName("my_language2")
    public String my_language2;

    @SerializedName("study_language2")
    public String study_language2;

    @SerializedName("level2")
    public String level2;
    @SerializedName("my_language3")
    public String my_language3;

    @SerializedName("study_language3")
    public String study_language3;

    @SerializedName("level3")
    public String level3;

    @SerializedName("profile")
    public String profile;

    @SerializedName("result")
    public String result;

    @Override
    public String toString() {
        return "LoginData{" +
                "idx='" + idx + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", my_language='" + my_language + '\'' +
                ", study_language='" + study_language + '\'' +
                ", level='" + level + '\'' +
                ", my_language2='" + my_language2 + '\'' +
                ", study_language2='" + study_language2 + '\'' +
                ", level2='" + level2 + '\'' +
                ", my_language3='" + my_language3 + '\'' +
                ", study_language3='" + study_language3 + '\'' +
                ", level3='" + level3 + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
