package com.example.hellochat.Activity.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.hellochat.DTO.CheckEmail;
import com.example.hellochat.GMailSender;
import com.example.hellochat.Interface.JoinApi;
import com.example.hellochat.R;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_password extends AppCompatActivity {

    EditText email;
    Button summit;
    String GmailCode;
    private static final String TAG = "Activity_password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        email = (EditText)findViewById(R.id.email);
        summit = (Button)findViewById(R.id.summit);


        summit.setOnClickListener(v -> {
            JoinApi service = RetrofitClientInstance.getRetrofitInstance().create(JoinApi.class);
            Call<CheckEmail> call = service.getCheckEmail(email.getText().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_password.this);
            call.enqueue(new Callback<CheckEmail>() {
                @Override
                public void onResponse(Call<CheckEmail> call, Response<CheckEmail> response) {
                    CheckEmail result;
                    result = response.body();
                    String email_check = result.response; // 서버에서 존재하는 이메일일 경우 0 아닐경우 1
                    if (email_check.equals("0")) {
                        //이메일 중복검사
                        Log.d(TAG, "onResponse: 이메일존재 ");
                        MailTread mailTread= new MailTread();
                        mailTread.start();
                        //보냈다고 다이얼로그 띄움
                        builder.setTitle("임시비밀번호를 전송했습니다.");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChangePassword(email.getText().toString() , GmailCode);
                                finish();
                            }
                        });
                    }
                    else {
                        //이메일이 존재하지않는다고 띄움
                        builder.setTitle("가입되지않은 이메일입니다.");
                        builder.setPositiveButton("ok", null);
                    }
                    builder.create().show();
                }

                @Override
                public void onFailure(Call<CheckEmail> call, Throwable t) {
                    builder.setTitle("서버에 문제가 생겼습니다 다시 시도해주세요.");
                    builder.setPositiveButton("ok", null);
                    builder.create().show();
                }
            });


        });


    }

    public void ChangePassword(String email , String password){
        JoinApi service = RetrofitClientInstance.getRetrofitInstance().create(JoinApi.class);
        Call<ResultData> call = service.updatePassword(email , password);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                ResultData result;
                result = response.body();
                Log.d(TAG, "onResponse: "+result);

            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {

            }
        });

    }


    class MailTread extends Thread{
        public void run(){
            GMailSender gMailSender = new GMailSender("ghwo2033@gmail.com" , "ghwo1353012");

            GmailCode = gMailSender.getEmailCode();
            try {
                Log.d(TAG, "run: "+GmailCode+email.getText().toString());
                gMailSender.sendMail("헬로챗 임시비밀번호",
                        GmailCode ,
                        email.getText().toString());
            } catch (Exception e) {
                Log.d(TAG, "run: "+GmailCode+email.getText().toString());
                e.printStackTrace();
            }

        }
    }
}

