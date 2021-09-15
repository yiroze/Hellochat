package com.example.hellochat;

public class NewsfeedList {

    String name , mylang , studylang , date , comment_count , heart_count , contents , profile ;
    int mylang_level , studylang_level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMylang() {
        return mylang;
    }

    public void setMylang(String mylang) {
        this.mylang = mylang;
    }

    public String getStudylang() {
        return studylang;
    }

    public void setStudylang(String studylang) {
        this.studylang = studylang;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getHeart_count() {
        return heart_count;
    }

    public void setHeart_count(String heart_count) {
        this.heart_count = heart_count;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getMylang_level() {
        return mylang_level;
    }

    public void setMylang_level(int mylang_level) {
        this.mylang_level = mylang_level;
    }

    public int getStudylang_level() {
        return studylang_level;
    }

    public void setStudylang_level(int studylang_level) {
        this.studylang_level = studylang_level;
    }

    public NewsfeedList(String name, String mylang, String studylang, String date, String comment_count, String heart_count, String contents, String profile, int mylang_level, int studylang_level) {
        this.name = name;
        this.mylang = mylang;
        this.studylang = studylang;
        this.date = date;
        this.comment_count = comment_count;
        this.heart_count = heart_count;
        this.contents = contents;
        this.profile = profile;
        this.mylang_level = mylang_level;
        this.studylang_level = studylang_level;
    }
}
