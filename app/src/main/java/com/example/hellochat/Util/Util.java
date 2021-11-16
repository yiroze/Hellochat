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

}
