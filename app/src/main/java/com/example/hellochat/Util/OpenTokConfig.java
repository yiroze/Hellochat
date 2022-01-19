package com.example.hellochat.Util;

public class OpenTokConfig {
    public static final String API_KEY = "47415121";
    public String SESSION_ID;
    public String TOKEN;
    public OpenTokConfig(String SESSION_ID , String TOKEN){
        this.SESSION_ID = SESSION_ID;
        this.TOKEN = TOKEN;
    }
}