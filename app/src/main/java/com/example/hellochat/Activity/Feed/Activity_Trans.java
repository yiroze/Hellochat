package com.example.hellochat.Activity.Feed;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hellochat.R;
import com.example.hellochat.Util.TransThread;
import com.example.hellochat.Util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Activity_Trans extends Activity {

    String content , targetLang;
    TextView result;
    TransHandler transHandler;
    Button copy , ok;
    Spinner spinner;
    String[] items;
    ArrayList list;
    Util util= new Util();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trans);
        result = findViewById(R.id.txtText);
        copy = findViewById(R.id.copy);
        ok = findViewById(R.id.ok);
        spinner = findViewById(R.id.spinner);
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        targetLang = intent.getStringExtra("targetLang");
        transHandler = new TransHandler();
        TransThread transThread = new TransThread(content,getFirstTargetLangCode(),transHandler);
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
            InitSpinner();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TransThread transThread = new TransThread(content,getLangCode(position),transHandler);
                transThread.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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

    public String getFirstTargetLangCode(){
        SharedPreferences pref = getSharedPreferences("Translator", MODE_PRIVATE);
        JSONArray JSON = null;
        try {
            JSON = new JSONArray(pref.getString("targetlang", ""));
            return JSON.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void InitSpinner()  {
        SharedPreferences pref = getSharedPreferences("Translator", MODE_PRIVATE);
        JSONArray JSON = null;
        Log.d(TAG, "InitSpinner: "+pref.getString("targetlang", ""));
        try {
            JSON = new JSONArray(pref.getString("targetlang", ""));
            if(JSON.length()==1){
                items=new String[]{util.LangCodeToLanguage(JSON.get(0).toString())};
            }else if(JSON.length()==2){
                items=new String[]{util.LangCodeToLanguage(JSON.get(0).toString()),util.LangCodeToLanguage(JSON.get(1).toString())};
            }else if(JSON.length()==3){
                items=new String[]{util.LangCodeToLanguage(JSON.get(0).toString()),util.LangCodeToLanguage(JSON.get(1).toString()),util.LangCodeToLanguage(JSON.get(2).toString())};
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLangCode(int position)  {
        SharedPreferences pref = getSharedPreferences("Translator", MODE_PRIVATE);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(pref.getString("targetlang", ""));
            return jsonArray.get(position).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}