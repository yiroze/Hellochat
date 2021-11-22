package com.example.hellochat.Activity.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import static android.content.ContentValues.TAG;

public class Activity_SettingPlaceOfBirth extends AppCompatActivity {

    String POB;
    EditText PlaceOfBirth;
    TextView ok;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_placeof_birth);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ok = findViewById(R.id.ok);
        back = findViewById(R.id.back);
        PlaceOfBirth = findViewById(R.id.PlaceOfBirth);
        Intent getIntent = getIntent();
        POB = getIntent.getStringExtra("place");
        if(!POB.equals("미설정")){
            PlaceOfBirth.setText(POB);
        }else{
            POB = null;
        }
        back.setOnClickListener(v -> {
            finish();
        });
        ok.setOnClickListener(v -> {
            UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
            Call<ResultData> call = service.modify_pob(GetUserID() , PlaceOfBirth.getText().toString() );
            call.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    Log.d(TAG, "onResponse: "+ response.body().body);
                    Intent intent = new Intent(Activity_SettingPlaceOfBirth.this , Activity_Setting.class);
                    setResult(RESULT_OK , intent);
                    finish();
                }

                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SettingPlaceOfBirth.this);
                    builder.setTitle("서버에 문제가 생겼습니다.\n다시 시도해 주세요");
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

    public int GetUserID() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int user = Integer.parseInt(pref.getString("Login_data", ""));
        return user;
    }
}