package com.example.hellochat.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hellochat.DTO.EditData;
import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Edit extends AppCompatActivity {
    EditText content;
    ImageView back, edit;
    String TAG = this.getClass().getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String user_index = pref.getString("Login_data", "");

        content = (EditText) findViewById(R.id.content);
        back = (ImageView) findViewById(R.id.back);
        edit = (ImageView) findViewById(R.id.edit);

        //Retrofit 인스턴스 생성

        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);

        edit.setOnClickListener(v -> {
            Log.d(TAG, "에딧클릭");
            String content_data = content.getText().toString();
            Call<EditData> call = service.getEditData(user_index, content_data);
            call.enqueue(new Callback<EditData>() {
                @Override
                public void onResponse(Call<EditData> call, Response<EditData> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: 통신성공 ");
                        EditData result;
                        result = response.body();
                        Log.d(TAG, "onResponse: " + result);
                        Log.d(TAG, "onResponse: " + user_index + content_data);

                        assert result != null;
                        if (result.result.equals("true")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Edit.this);
                            builder.setTitle(result.msg);
                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent_result = new Intent();
                                    setResult(11, intent_result);
                                    finish();
                                }
                            });
                            builder.create().show();
                            //돌아가면 실행되는 메서드에서 데이터 다시받아오고 리사이클러뷰 갱신하기
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Edit.this);
                            builder.setTitle(result.msg);
                            builder.setPositiveButton("ok", null);
                            builder.create().show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<EditData> call, Throwable t) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Edit.this);
                    builder.setTitle("서버에 문제가 생겼습니다 나중에 다시시도해주세요");
                    builder.setPositiveButton("ok", null);
                    builder.create().show();
                }
            });

        });

    }
}