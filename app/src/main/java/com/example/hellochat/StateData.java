package com.example.hellochat;

import com.google.gson.annotations.SerializedName;

public class StateData {
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

    @SerializedName("result")
    public String result;

    @Override
    public String toString() {
        return "StateData{" +
                "idx='" + idx + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", my_language='" + my_language + '\'' +
                ", study_language='" + study_language + '\'' +
                ", level='" + level + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
