package com.example.hellochat.Activity.Feed;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hellochat.R;
import com.example.hellochat.Util.TransThread;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity_Trans extends Activity {

    String content , targetLang;
    TextView result;
    TransHandler transHandler;
    Button copy , ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trans);
        result = findViewById(R.id.txtText);
        copy = findViewById(R.id.copy);
        ok = findViewById(R.id.ok);
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        targetLang = intent.getStringExtra("targetLang");
        transHandler = new TransHandler();

        TransThread transThread = new TransThread(content,targetLang,transHandler);
        transThread.start();

        copy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text",result.getText().toString()); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText( Activity_Trans.this , "복사되었습니다.",Toast.LENGTH_SHORT).show();
        });
        ok.setOnClickListener(v -> {
            finish();
        });
    }

    public class TransHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            try {
                JSONObject jsonObject = new JSONObject(msg.obj.toString());
                JSONObject jsonObject1 = new JSONObject(jsonObject.get("message").toString());
                JSONObject jsonObject2 = new JSONObject(jsonObject1.get("result").toString());
                String translatedText = String.valueOf(jsonObject2.get("translatedText"));
                result.setText(translatedText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}