package com.example.hellochat.Activity.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_SettingName extends AppCompatActivity {
    ImageView back;
    EditText name;
    TextView ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_name);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        InitView();
        Intent intent =  getIntent();
        name.setText(intent.getStringExtra("name"));
        back.setOnClickListener(v -> {
            finish();
        });
        ok.setOnClickListener(v -> {
            UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
            Call<ResultData> call = service.modify_name(GetUserID() , name.getText().toString());
            call.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    Intent intent = new Intent(Activity_SettingName.this, Activity_Setting.class);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SettingName.this);
                    builder.setTitle("????????? ????????? ???????????????.\n?????? ????????? ?????????");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                }
            });
        });

    }
    void InitView(){
        back = findViewById(R.id.back);
        name = findViewById(R.id.name);
        ok = findViewById(R.id.ok);
    }
    public int GetUserID() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int user = Integer.parseInt(pref.getString("Login_data", ""));
        return user;
    }
}