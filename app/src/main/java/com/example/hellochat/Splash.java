package com.example.hellochat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.hellochat.Activity.Login.Activity_Login;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        int sec = 1000; //1초
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String Login =  pref.getString("Login_data" , "");

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(Login.equals("")){
                    intent = new Intent(Splash.this, Activity_Login.class);
                }else {
                    intent = new Intent(Splash.this, MainActivity.class);
                    intent.putExtra("idx" ,Login);
                }
                startActivity(intent);
                finish();
            }
        }, sec);


    }
}