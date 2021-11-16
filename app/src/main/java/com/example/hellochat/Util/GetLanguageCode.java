package com.example.hellochat.Util;

import android.content.Context;
import android.content.Intent;

import com.example.hellochat.Activity.Feed.Activity_TTS;
import com.example.hellochat.Papago;

public class GetLanguageCode extends Thread {
    String text;
    Context c;
    public GetLanguageCode(String content , Context context){
        text = content;
        c =context;
    }

    @Override
    public void run() {
        super.run();
        Papago papago = new Papago();

        Intent intent = new Intent(c , Activity_TTS.class);
        intent.putExtra("content" ,  text);
        intent.putExtra("targetLang" , papago.getLanguageCode(text));
        c.startActivity(intent);

    }
}
