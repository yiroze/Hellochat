package com.example.hellochat.Util;

import android.os.Message;
import android.util.Log;

import com.example.hellochat.Activity.Feed.Activity_Trans;

public class TransThread extends Thread {
    private static final String TAG = "NetWork";
    String query;
    String target;
    Activity_Trans.TransHandler transHandler;

    public TransThread(String contents, String targetlang , Activity_Trans.TransHandler handler) {
        query = contents;
        target = targetlang;
        transHandler = handler;
    }

    @Override
    public void run() {
        super.run();
        Papago papago = new Papago();
        Message msg = Message.obtain();
        msg.obj = papago.trans(query , target);
        transHandler.sendMessage(msg);
        Log.d(TAG, "run: " + target);
        Log.d(TAG, "run: " + papago.trans(query, target));
    }
}
