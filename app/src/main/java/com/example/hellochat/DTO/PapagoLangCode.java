package com.example.hellochat.DTO;

import com.google.gson.annotations.SerializedName;

public class PapagoLangCode {

    @SerializedName("langCode")
    public String langCode;

    @Override
    public String toString() {
        return "PapagoLangCode{" +
                "langCode='" + langCode + '\'' +
                '}';
    }
}
