package com.example.hellochat.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.hellochat.R;

public class Activity_Setting extends AppCompatActivity {
    Button logout;
    private static final String TAG = "Activity_Setting";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        logout = (Button)findViewById(R.id.logout);


        logout.setOnClickListener(v -> {
            Intent intent = new Intent( Activity_Setting.this , Activity_Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

    }
}