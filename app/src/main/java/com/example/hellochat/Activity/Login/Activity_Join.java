package com.example.hellochat.Activity.Login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hellochat.DTO.CheckEmail;
import com.example.hellochat.Interface.JoinApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Join extends AppCompatActivity {
    EditText email , password , check_password , name ;
    Button next;
    ImageView setImage;
    String TAG = this.getClass().getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        check_password = (EditText)findViewById(R.id.password_check);
        name = (EditText)findViewById(R.id.name);
        next = (Button)findViewById(R.id.next);
        setImage= (ImageView)findViewById(R.id.mark);
        //비밀번호 확인 체크
        check_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(password.getText().toString().equals(check_password.getText().toString())){
                    setImage.setImageResource(R.drawable.ok);
                }else {
                    setImage.setImageResource(R.drawable.fail);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //Retrofit 인스턴스 생성
        JoinApi service = RetrofitClientInstance.getRetrofitInstance().create(JoinApi.class);

        next.setOnClickListener(v ->{
            Call<CheckEmail> call = service.getCheckEmail(email.getText().toString());
            call.enqueue(new Callback<CheckEmail>() {
                @Override
                public void onResponse(Call<CheckEmail> call, Response<CheckEmail> response1) {
                    if(response1.isSuccessful()) {
                        CheckEmail result;
                        result = response1.body();
                        assert result != null;
                        String email_check = result.response; // 서버에서 중복된 이메일일 경우 0 아닐경우 1
                        Log.d(TAG, "onResponse: " + email_check);
                        Log.d(TAG, "onResponse: " + response1);
                        Pattern pattern = Patterns.EMAIL_ADDRESS;
                        Matcher matcher = pattern.matcher(email.getText().toString());

                        if (matcher.find()) {
                            //이메일 형식에 맞을 때
                        if (!email.getText().toString().equals("")) {
                            //이메일이 빈값인지 확인
                            Log.d(TAG, "onResponse: 이메일 빈값인지 확인 ");
                            if (email_check.equals("1")) {
                                //이메일 중복검사
                                Log.d(TAG, "onResponse: 이메일 중복아님 ");
                                if (!password.getText().toString().equals("")) {
                                    //비밀번호 빈값인지 확인
                                    Log.d(TAG, "onResponse: 비밀번호 빈값인지 확인 ");
                                    if (password.getText().toString().equals(check_password.getText().toString())) {
                                        //비밀번호 확인
                                        Log.d(TAG, "onResponse: 비밀번호 확인 완료 ");
                                        if (!name.getText().toString().equals("")) {
                                            //이름빈값인지 확인
                                            Log.d(TAG, "onResponse:이름빈값인지 확인");
                                            Intent intent = new Intent(Activity_Join.this, Activity_Join2.class);
                                            intent.putExtra("email", email.getText().toString());
                                            intent.putExtra("password", password.getText().toString());
                                            intent.putExtra("name", name.getText().toString());
                                            startActivity(intent);
                                        } else {
                                            //이름 빈값임
                                            Log.d(TAG, "onResponse:이름 빈값임");
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join.this);
                                            builder.setTitle("이름을 입력해주세요");
                                            builder.setPositiveButton("ok" ,null);
                                            builder.create().show();
                                        }
                                    } else {
                                        //비밀번호 불일치
                                        Log.d(TAG, "onResponse: 비밀번호확인을 다시해주세요 ");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join.this);
                                        builder.setTitle("비밀번호확인을 다시해주세요");
                                        builder.setPositiveButton("ok" ,null);
                                        builder.create().show();
                                    }
                                } else {
                                    //비밀번호가 빈값임
                                    Log.d(TAG, "onResponse: 비밀번호 빈값");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join.this);
                                    builder.setTitle("비밀번호를 입력해주세요");
                                    builder.setPositiveButton("ok" ,null);
                                    builder.create().show();
                                }
                            } else {
                                //이메일 중복
                                Log.d(TAG, "onResponse: 이메일 중복 ");
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join.this);
                                builder.setTitle("이미 가입된 이메일입니다");
                                builder.setPositiveButton("ok" ,null);
                                builder.create().show();
                            }
                        } else {
                            //이메일 빈값임
                            Log.d(TAG, "onResponse: 이메일 빈값임 ");
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join.this);
                            builder.setTitle("이메일을 입력해주세요");
                            builder.setPositiveButton("ok" ,null);
                            builder.create().show();
                        }
                    }else {
                        //이메일 형식이 아님
                            Log.d(TAG, "onResponse: 이메일 형식이 아님 ");
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join.this);
                            builder.setTitle("이메일 형식이 아닙니다");
                            builder.setPositiveButton("ok" ,null);
                            builder.create().show();
                        }
                    }
                    
                    
                }

                @Override
                public void onFailure(Call<CheckEmail> call, Throwable t) {
                    Log.d(TAG, "onFailure: 통신실패");
                }
            });

        });



    }
}