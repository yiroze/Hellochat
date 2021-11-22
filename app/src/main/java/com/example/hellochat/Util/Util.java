package com.example.hellochat.Util;

public class Util {
    int TEXTMessage = 1;
    int IMAGEMessage = 2;
    int CheckMessage = 3;
    int CallSignal = 4;//거는사람이 보냄
    int RefuseCall = 5; // 받는사람이 보냄
    int CancelCall = 6; //거는사람이 보냄
    int ConnectCall = 7; //받는사람이 보냄
    int DisconnectCall = 8;

    public String secToText(int value) {
        int min;
        int sec;
        String Text;
        if (value < 60) {
            sec = value;
            min = 0;
            Text = new StringBuilder().append(String.format("%02d", min)).append(":").append(String.format("%02d", sec)).toString();
            return Text;
        } else {
            min = value / 60;
            sec = value % 60;
            String minText = String.format("%02d", min);
            String secText = String.format("%02d", sec);
            Text = new StringBuilder().append(String.format("%02d", min)).append(":").append(String.format("%02d", sec)).toString();
            return Text;
        }
    }

    public String ReturnLangCode(String language) {
        switch (language) {
            case "Korean":
                return "ko";
            case "English":
                return "en";
            case "Chinese":
                return "zh-cn";
            case "Hindi":
                return "hi";
            case "French":
                return "fr";
            case "Bengali":
                return "hi";
            case "Spanish":
                return "es";
            case "Arabic":
                return "v";
            case "Russian":
                return "ru";
            case "German":
                return "de";
            case "Japanese":
                return "ja";
            case "Portuguese":
                return "pt";
        }
        return null;
    }
    public String ReturnLanguageName(String lang) {
        switch (lang) {
            case "KR":
                return "Korean";
            case "EN":
                return "English";
            case "CN":
                return "Chinese";
            case "FR":
                return "French";
            case "ES":
                return "Spanish";
            case "AR":
                return "Arabic";
            case "RU":
                return "Russian";
            case "DE":
                return "German";
            case "JP":
                return "Japanese";
            case "PT":
                return "Portuguese";
            case "HD":
                return "Hindi";
            case "BG":
                return "Bengali";
        }
        return null;
    }

    public String ReturnLanguageName2alpha(String lang) {
        switch (lang) {
            case "Korean":
                return "KR";
            case "English":
                return "EN";
            case "Chinese":
                return "CN";
            case "French":
                return "FR";
            case "Spanish":
                return "ES";
            case "Arabic":
                return "AR";
            case "Russian":
                return "RU";
            case "German":
                return "DE";
            case "Japanese":
                return "JP";
            case "Portuguese":
                return "PT";
            case "Hindi":
                return "HD";
            case "Bengali":
                return "BG";
        }
        return null;
    }
}
