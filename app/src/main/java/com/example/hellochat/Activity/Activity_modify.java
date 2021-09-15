package com.example.hellochat.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_modify extends AppCompatActivity {
    EditText content;
    ImageView back, edit;
    String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_modify);
        content = (EditText) findViewById(R.id.content);
        back = (ImageView) findViewById(R.id.back);
        edit = (ImageView) findViewById(R.id.edit);
        Intent intent= getIntent();
        String feed_idx= intent.getStringExtra("feed_idx");
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call_feed = service.get_content(feed_idx);
        call_feed.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                if(response.isSuccessful()){
                            ResultData resultData = response.body();
                            content.setText(resultData.body);
                }
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {

            }
        });


        edit.setOnClickListener(v -> {
            Call<ResultData> call = service.modify_post(feed_idx ,content.getText().toString() );
            call.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    if(response.isSuccessful()){
                        ResultData resultData = response.body();
                        Log.d(TAG, "onResponse: "+resultData.body);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_modify.this);
                        if(resultData.body.equals("ok")){
                            Log.d(TAG, "onResponse: ");
                            finish();
                        }else{
                            //수정실패 메시지 띄우기
                            finish();
                        }
                    }
                }
                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {
                    Log.d(TAG, "onFailure: ");
                }
            });


        });

    }
}