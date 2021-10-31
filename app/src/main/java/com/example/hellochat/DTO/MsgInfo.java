package com.example.hellochat.DTO;

public class MsgInfo {
    private String user_idx;
    private String message;

    public MsgInfo(String user_idx, String message) {
        this.user_idx = user_idx;
        this.message = message;
    }

    @Override
    public String toString() {
        return "MsgInfo [user_idx=" + user_idx + ", message=" + message+"]";
    }
}
