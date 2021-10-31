package com.example.hellochat.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hellochat.LoginApi;
import com.example.hellochat.DTO.LoginData;
import com.example.hellochat.MainActivity;
import com.example.hellochat.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Login extends AppCompatActivity {
    EditText email, password;
    TextView join, search_password;
    Button login;
    String TAG = this.getClass().getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Login_data", "");
        editor.apply();
        if (MainActivity.activity != null) { //액티비티가 살아 있다면
            MainActivity activity = (MainActivity) MainActivity.activity;
            activity.finish();
        }


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        join = (TextView) findViewById(R.id.join);
        search_password = (TextView) findViewById(R.id.search_password);
        login = (Button) findViewById(R.id.login_bnt);

        //Retrofit 인스턴스 생성
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("http://3.37.204.197/hellochat/")    // baseUrl 등록
                .addConverterFactory(GsonConverterFactory.create())  // Gson 변환기 등록
                .build();
        LoginApi service = retrofit.create(LoginApi.class);

        //회원가입 클릭
        join.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Login.this, Activity_Join.class);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            Call<LoginData> call = service.getLoginData(email.getText().toString(), password.getText().toString());
            if (!email.getText().toString().equals("") || !password.getText().toString().equals("")) {
                Log.d(TAG, "onCreate: " + email.getText().toString());
                //아이디 비밀번호 빈칸확인
                call.enqueue(new Callback<LoginData>() {
                    @Override
                    public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: "+response.body().toString());

                            LoginData msg = response.body();
                            Log.d(TAG, "onResponse: " + msg);
                            Log.d(TAG, "onResponse: " + msg.result);
                            if (msg.result.equals("ok")) {
                                //아이디 , 비밀번호 일치 -> 로그인진행
                                Intent intent = new Intent(Activity_Login.this, MainActivity.class);
                                intent.putExtra("idx", msg.idx);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                //로그인실패 , 서버에서 받은 메시지띄움
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Login.this);
                                builder.setTitle(msg.result);
                                builder.setPositiveButton("ok", null);
                                builder.create().show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginData> call, Throwable t) {
                        Log.d(TAG, "onFailure: 통신실패");
                    }
                });

            } else {
                //아이디와 비밀번호를 입력해주세요
            }
        });
        search_password.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Login.this , Activity_password.class);
            startActivity(intent);
        });


    }
}