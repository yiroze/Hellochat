package com.example.hellochat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_modify_comment extends Activity {
    EditText editText;
    TextView ok, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_comment);
        editText = (EditText) findViewById(R.id.editText);
        ok = (TextView) findViewById(R.id.ok);
        cancel = (TextView) findViewById(R.id.cancel);
        Intent intent = getIntent();
        int comment_idx = intent.getIntExtra("comment_idx", 0);
        String contents = intent.getStringExtra("contents");
        editText.setText(contents);
        ok.setOnClickListener(v -> {
            NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
            Call<ResultData> call = service.update_comment(comment_idx , editText.getText().toString());
            call.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    if(response.isSuccessful()){
                        Intent result = new Intent();
                        setResult(11,result);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {
                }
            });
        });
        cancel.setOnClickListener(v -> {
            finish();
        });


    }
}