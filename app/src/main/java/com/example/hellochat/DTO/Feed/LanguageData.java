package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

public class LanguageData {

    public String Language_en;

    public String Language_native;

    @Override
    public String toString() {
        return "LanguageData{" +
                "Language_en='" + Language_en + '\'' +
                ", Language_native='" + Language_native + '\'' +
                '}';
    }

    public LanguageData(String language_en , String language_native){
        Language_en = language_en;
        Language_native = language_native;
    }
}
