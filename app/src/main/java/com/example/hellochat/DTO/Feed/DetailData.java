package com.example.hellochat.DTO.Feed;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DetailData {

    @SerializedName("user_idx")
    public int user_idx;

    @SerializedName("name")
    public String name;

    @SerializedName("profile")
    public String profile;

    @SerializedName("mylang")
    public String mylang;

    @SerializedName("mylang2")
    public String mylang2;

    @SerializedName("mylang3")
    public String mylang3;

    @SerializedName("studylang")
    public String studylang;

    @SerializedName("studylang2")
    public String studylang2;

    @SerializedName("studylang3")
    public String studylang3;

    @SerializedName("contents")
    public String contents;

    @SerializedName("date")
    public String date;

    @SerializedName("studylang_level")
    public int studylang_level;

    @SerializedName("studylang_level2")
    public int studylang_level2;

    @SerializedName("studylang_level3")
    public int studylang_level3;

    @SerializedName("heart")
    public int heart;

    @SerializedName("comment")
    public int comment;

    @SerializedName("view_type")
    public int view_type;
    //뷰타입  0 =헤더 1 =댓글 2= 대댓글

    @SerializedName("parents")
    public int parents;

    @SerializedName("comment_idx")
    public int comment_idx;

    @SerializedName("islike")
    public int islike;

    @SerializedName("like_cnt")
    public int like_cnt;

    @SerializedName("comment_cnt")
    public int comment_cnt;

    @SerializedName("image")
    public String image;

    @SerializedName("record")
    public String record;


    @SerializedName("reply")
    public ArrayList<ReplyList> reply;


    @SerializedName("reply_cnt")
    public int reply_cnt;


    @SerializedName("isOpened")
    public boolean isOpened;


    @Override
    public String toString() {
        return "DetailData{" +
                "user_idx=" + user_idx +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", mylang='" + mylang + '\'' +
                ", mylang2='" + mylang2 + '\'' +
                ", mylang3='" + mylang3 + '\'' +
                ", studylang='" + studylang + '\'' +
                ", studylang2='" + studylang2 + '\'' +
                ", studylang3='" + studylang3 + '\'' +
                ", contents='" + contents + '\'' +
                ", date='" + date + '\'' +
                ", studylang_level=" + studylang_level +
                ", studylang_level2=" + studylang_level2 +
                ", studylang_level3=" + studylang_level3 +
                ", heart=" + heart +
                ", comment=" + comment +
                ", view_type=" + view_type +
                ", parents=" + parents +
                ", comment_idx=" + comment_idx +
                ", islike=" + islike +
                ", like_cnt=" + like_cnt +
                ", comment_cnt=" + comment_cnt +
                ", image='" + image + '\'' +
                ", record='" + record + '\'' +
                ", reply=" + reply +
                ", reply_cnt=" + reply_cnt +
                ", isOpened=" + isOpened +
                '}';
    }

    public DetailData update_reply(ArrayList<ReplyList> reply, String contents) {
        this.reply = reply;

        return new DetailData(user_idx , name , profile , mylang , mylang2 , mylang3,
                studylang , studylang2, studylang3 , contents , date ,studylang_level, studylang_level2 , studylang_level3,
                heart , comment , view_type , parents , comment_idx , islike , like_cnt , comment_cnt , image, record , reply , isOpened , reply_cnt);
    }

    public DetailData(int user_idx, String name, String profile, String mylang, String mylang2,
                      String mylang3, String studylang, String studylang2, String studylang3, String contents,
                      String date, int studylang_level, int studylang_level2, int studylang_level3, int heart,
                      int comment, int view_type, int parents, int comment_idx,
                      int islike, int like_cnt, int comment_cnt, String image, String record, ArrayList<ReplyList> reply, boolean isOpened , int reply_cnt) {
        this.user_idx = user_idx;
        this.name = name;
        this.profile = profile;
        this.mylang = mylang;
        this.mylang2 = mylang2;
        this.mylang3 = mylang3;
        this.studylang = studylang;
        this.studylang2 = studylang2;
        this.studylang3 = studylang3;
        this.contents = contents;
        this.date = date;
        this.studylang_level = studylang_level;
        this.studylang_level2 = studylang_level2;
        this.studylang_level3 = studylang_level3;
        this.heart = heart;
        this.comment = comment;
        this.view_type = view_type;
        this.parents = parents;
        this.comment_idx = comment_idx;
        this.islike = islike;
        this.like_cnt = like_cnt;
        this.comment_cnt = comment_cnt;
        this.image = image;
        this.record = record;
        this.reply = reply;
        this.isOpened = isOpened;
        this.reply_cnt = reply_cnt;
    }

    public int getUser_idx() {
        return user_idx;
    }

    public void setUser_idx(int user_idx) {
        this.user_idx = user_idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getMylang() {
        return mylang;
    }

    public void setMylang(String mylang) {
        this.mylang = mylang;
    }

    public String getMylang2() {
        return mylang2;
    }

    public void setMylang2(String mylang2) {
        this.mylang2 = mylang2;
    }

    public String getMylang3() {
        return mylang3;
    }

    public void setMylang3(String mylang3) {
        this.mylang3 = mylang3;
    }

    public String getStudylang() {
        return studylang;
    }

    public void setStudylang(String studylang) {
        this.studylang = studylang;
    }

    public String getStudylang2() {
        return studylang2;
    }

    public void setStudylang2(String studylang2) {
        this.studylang2 = studylang2;
    }

    public String getStudylang3() {
        return studylang3;
    }

    public void setStudylang3(String studylang3) {
        this.studylang3 = studylang3;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStudylang_level() {
        return studylang_level;
    }

    public void setStudylang_level(int studylang_level) {
        this.studylang_level = studylang_level;
    }

    public int getStudylang_level2() {
        return studylang_level2;
    }

    public void setStudylang_level2(int studylang_level2) {
        this.studylang_level2 = studylang_level2;
    }

    public int getStudylang_level3() {
        return studylang_level3;
    }

    public void setStudylang_level3(int studylang_level3) {
        this.studylang_level3 = studylang_level3;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public void setView_type(int view_type) {
        this.view_type = view_type;
    }

    public int getParents() {
        return parents;
    }

    public void setParents(int parents) {
        this.parents = parents;
    }

    public int getComment_idx() {
        return comment_idx;
    }

    public void setComment_idx(int comment_idx) {
        this.comment_idx = comment_idx;
    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public int getLike_cnt() {
        return like_cnt;
    }

    public void setLike_cnt(int like_cnt) {
        this.like_cnt = like_cnt;
    }

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public ArrayList<ReplyList> getReply() {
        return reply;
    }

    public void setReply(ArrayList<ReplyList> reply) {
        this.reply = reply;
    }

    public int getReply_cnt() {
        return reply_cnt;
    }

    public void setReply_cnt(int reply_cnt) {
        this.reply_cnt = reply_cnt;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public int getView_type() {
        return view_type;
    }

}
