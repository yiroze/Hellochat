package com.example.hellochat.Activity.Feed;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.Adapter.Feed.ReplyAdapter2;
import com.example.hellochat.DTO.Feed.ReplyData;
import com.example.hellochat.DTO.Feed.ReplyList;
import com.example.hellochat.DTO.Feed.ReplyResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Service.ClientService;
import com.example.hellochat.SoftKeyboardDectectorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Reply extends AppCompatActivity {
    TextView name, content, date;
    ImageView profile , more, back;
    RecyclerView mRecyclerView;
    int comment_idx;
    ReplyAdapter2 replyAdapter;
    ArrayList<ReplyList> mList;
    private static final String TAG = "Activity_Reply";

    private ActivityResultLauncher<Intent> modify_comment;
    private ActivityResultLauncher<Intent> modify_reply;

    //????????????
    EditText editText;
    ImageView submit;
    int feed, user;

    //????????? , ????????????
    MediaRecorder recorder;
    MediaPlayer mPlayer;
    boolean isRecording, isPlaying, RecordState, RecordDataState = false;
    ConstraintLayout voice_layout , player_layout;
    String RecordDataPath;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200, MESSAGE_RECORD_TIMER = 100,
            MESSAGE_RECORD_START = 103, MESSAGE_PLAYER_TIMER = 101, MESSAGE_PLAYER_START = 102;
    TextView time;
    TimerHandler timerHandler;
    ImageView record_bnt, mic, record_ok, reset , playercontrol;
    int position;
    SeekBar SeekBar;
    int user_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        setKeyboard();
        timerHandler = new TimerHandler();
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


        Intent getIntent = getIntent();
        comment_idx = getIntent.getIntExtra("comment_idx", 0);
        feed = getIntent.getIntExtra("feed_num", 0);
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        user = Integer.parseInt(pref.getString("Login_data", ""));

        String comment = getIntent.getStringExtra("comment");
        String profile = getIntent.getStringExtra("profile");
        String name = getIntent.getStringExtra("name");
        String date = getIntent.getStringExtra("date");
        String record = getIntent.getStringExtra("record");
        user_idx =  getIntent.getIntExtra("user_idx" , -1);
        position = getIntent.getIntExtra("position", -1);
        InitView(comment, profile, name, date , record);
        getReply(comment_idx);


        submit.setOnClickListener(v -> {
            if (!editText.getText().toString().equals("") || RecordDataState) {
                if (RecordDataState) {
                    upload_Record(comment_idx, feed, user, editText.getText().toString());
                } else {
                    RecordDataPath = "";
                    setReply(comment_idx, feed, user, editText.getText().toString(), "");
                }
                editText.setText("");
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                voice_layout.setVisibility(View.GONE);
                isPlaying = false;
                RecordState = false;
                RecordDataState = false;
                time.setText("0:00");
                record_bnt.setImageResource(R.drawable.record);
                record_ok.setVisibility(View.GONE);
            }
        });

        mic.setOnClickListener(v -> {
            PermissionCheck();
            try {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            voice_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500));
            voice_layout.setVisibility(View.VISIBLE);
        });

        reset.setOnClickListener(v -> {
            isPlaying = false;
            if (isRecording) {
                stopRecording();
                isRecording = false;
            }
            RecordState = false;
            RecordDataState = false;
            time.setText("0:00");
            timerHandler.removeMessages(MESSAGE_RECORD_TIMER);
            record_bnt.setImageResource(R.drawable.record);
            record_ok.setVisibility(View.GONE);
            recorder = null;
        });

        record_bnt.setOnClickListener(v -> {
            if (recorder == null) {
                recorder = new MediaRecorder(); // ?????????????????? ?????? ??????
                Log.d(TAG, "onCreate: ?????????????????????");
            }
            if (!RecordState) {
                if (!isRecording) {
                    isRecording = true;
                    record_bnt.setImageResource(R.drawable.record_stop);
                    recordAudio();
                    timerHandler.sendEmptyMessage(MESSAGE_RECORD_START);
                    Log.d(TAG, "onCreate: ?????? ??????");
                } else {
                    stopRecording();
                    isRecording = false;
                    record_bnt.setImageResource(R.drawable.play);
                    RecordState = true;
                    RecordDataState = true;
                    timerHandler.removeMessages(MESSAGE_RECORD_TIMER);
                    Log.d(TAG, "onCreate: ?????? ??????");
                    copyFile(getFilePath(), SaveRecordData());
                    record_ok.setVisibility(View.VISIBLE);
                }
            } else {
                if (!isPlaying) {
                    try {
                        if (mPlayer != null) {    // ???????????? ??????
                            mPlayer.release();  // ????????? ??????
                            mPlayer = null;
                            Log.d(TAG, "onCreate: ???????????????");
                        }
                        mPlayer = new MediaPlayer();
                        mPlayer.setDataSource(getFilePath()); // ?????? ?????? ?????? ??????
                        mPlayer.prepare();  // ?????? ??????
                        mPlayer.start();    // ??????
                        timerHandler.sendEmptyMessage(MESSAGE_PLAYER_START);
                        Log.d(TAG, "onCreate: ????????????");
                        Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isPlaying = true;
                    record_bnt.setImageResource(R.drawable.pause);
                } else {
                    if (mPlayer != null) {
                        mPlayer.pause();    // ????????????
                        Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                        isPlaying = false;
                        record_bnt.setImageResource(R.drawable.play);
                        timerHandler.removeMessages(MESSAGE_PLAYER_TIMER);
                        Log.d(TAG, "onCreate: ????????????");
                    }
                }
            }
        });
        OnActivityResult();
    }

    void InitView(String comment_data, String profile_data, String name_data, String date_data, String record) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        name = findViewById(R.id.name);
        content = findViewById(R.id.contents);
        date = findViewById(R.id.date);
        profile = findViewById(R.id.profile);
        mRecyclerView = findViewById(R.id.recycler);
        name.setText(name_data);
        if (profile_data != null && !profile_data.equals("")) {
            String imageUrl = "http://3.37.204.197/hellochat/" + profile_data;
            //?????? ????????? ??????
            Glide.with(profile)
                    .load(imageUrl)
                    .into(profile);
        }
        date.setText(date_data);
        content.setText(comment_data);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(Activity_Reply.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        submit = (ImageView) findViewById(R.id.submit);
        editText = (EditText) findViewById(R.id.comment_edit);
        time = (TextView) findViewById(R.id.time);
        voice_layout = (ConstraintLayout) findViewById(R.id.voice_layout);
        reset = (ImageView) findViewById(R.id.reset);
        record_bnt = (ImageView) findViewById(R.id.recording);
        mic = (ImageView) findViewById(R.id.record);
        record_ok = (ImageView) findViewById(R.id.record_ok);
        playercontrol= findViewById(R.id.player_control);
        SeekBar =findViewById(R.id.SeekBar);
        player_layout = findViewById(R.id.player_layout);
        more = findViewById(R.id.more);
        back= findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });
        if(!record.equals("")){
            player_layout.setVisibility(View.VISIBLE);
            playercontrol.setOnClickListener(v -> {
                getPlayer(record, SeekBar);
            });
        }else {
            player_layout.setVisibility(View.GONE);
        }
        if(user_idx == user){
            more.setVisibility(View.VISIBLE);
        }else {
            more.setVisibility(View.GONE);
        }
        more.setOnClickListener(v -> {
            String items[] = {"????????????", "????????????"};
            AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Reply.this);
            dia.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent intent = new Intent(Activity_Reply.this, Activity_modify_comment2.class);
                        intent.putExtra("contents", content.getText().toString());
                        intent.putExtra("comment_idx", comment_idx);
                        modify_comment.launch(intent);
                    } else if (which == 1) {
                        Delete_Comment(comment_idx);
                    }
                }
            });
            AlertDialog alertDialog = dia.create();
            alertDialog.show();
        });
    }

    void getReply(int comment_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ReplyResult> call = service.getReply(comment_idx);
        call.enqueue(new Callback<ReplyResult>() {
            @Override
            public void onResponse(Call<ReplyResult> call, Response<ReplyResult> response) {
                mList = response.body().body;
                replyAdapter = new ReplyAdapter2(mList);
                mRecyclerView.setAdapter(replyAdapter);
                replyAdapter.notifyDataSetChanged();
                Log.d(TAG, "onResponse: " + mList.toString());
                replyAdapter.setOnReplyClick(new ReplyAdapter2.OnReplyMoreClick() {
                    @Override
                    public void onReplyMoreClickListener(View v, int pos) {
                        String items[] = {"????????????", "????????????"};
                        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Reply.this);
                        dia.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent intent = new Intent(Activity_Reply.this, Activity_modify_reply.class);
                                    intent.putExtra("contents", mList.get(pos).contents);
                                    intent.putExtra("comment_idx", mList.get(pos).comment_idx);
                                    modify_reply.launch(intent);
                                } else if (which == 1) {
                                    Delete_Reply(mList.get(pos).comment_idx);
                                }
                            }
                        });
                        AlertDialog alertDialog = dia.create();
                        alertDialog.show();
                    }
                });
            }
            @Override
            public void onFailure(Call<ReplyResult> call, Throwable t) {

            }
        });
    }

    void setReply(int comment_idx, int feed_idx, int user_idx, String content, String recordDataPath) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ReplyData> call = service.setReply(comment_idx, feed_idx, user_idx, content, recordDataPath);
        call.enqueue(new Callback<ReplyData>() {
            @Override
            public void onResponse(Call<ReplyData> call, Response<ReplyData> response) {
                if (response.isSuccessful()) {
                    ReplyData data = response.body();
                    Log.d(TAG, "onResponse: " + data.toString());
                    if (data.accept_user != user) {
                        Intent intent = new Intent(Activity_Reply.this, ClientService.class);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("content", data.comment_idx);
                            jsonObject.put("accept_user_idx", data.accept_user);
                            jsonObject.put("content_type", 12);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("msg", jsonObject.toString());
                        startService(intent);
                    }
                    getReply(comment_idx);
                }
            }

            @Override
            public void onFailure(Call<ReplyData> call, Throwable t) {

            }
        });
    }


    private void PermissionCheck() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "????????? ?????? ??????", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "????????? ?????? ??????", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                //????????? ?????? ????????? ?????????
                Toast.makeText(this, "????????? ?????? ????????? ?????????", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String getFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "recorded.mp4");
        return file.getPath();
    }

    private String SaveRecordData() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "upload.mp4");
        return file.getPath();
    }

    private boolean copyFile(String strSrc, String save_file) {
        File file = new File(strSrc);

        boolean result;
        if (file != null && file.exists()) {

            try {

                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount = 0;
                byte[] buffer = new byte[1024];

                while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
                    newfos.write(buffer, 0, readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    //?????? ??????
    public void recordAudio() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//????????? ??????
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//????????? ??????
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//??????????????? ?????????
        recorder.setOutputFile(getFilePath());//???????????? ??????
        Log.d(TAG, "onCreate: " + getFilePath());
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //?????? ??????
    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public class TimerHandler extends Handler {
        int S = 0;
        int M = 0;
        int S2 = 0;
        int M2 = 0;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECORD_TIMER:
                    removeMessages(MESSAGE_RECORD_TIMER);
                    S += 1;
                    if (S == 60) {
                        M += 1;
                        S = 0;
                    }
                    time.setText(M + ":" + String.format("%02d", S));
                    this.sendEmptyMessageDelayed(MESSAGE_RECORD_TIMER, 1000);
                    Log.d(TAG, "handleMessage: 1");
                    break;

                case MESSAGE_RECORD_START:
                    S = 0;
                    M = 0;
                    time.setText(M + ":" + String.format("%02d", S));
                    Log.d(TAG, "handleMessage: 4" + S + "" + M);
                    this.sendEmptyMessageDelayed(MESSAGE_RECORD_TIMER, 1000);
                    break;

                case MESSAGE_PLAYER_TIMER:
                    removeMessages(MESSAGE_RECORD_TIMER);
                    S2 += 1;
                    time.setText(M2 + ":" + String.format("%02d", S2));
                    if(M2 == M && S2 == S){
                        //??? ????????? ???????????????
                        isPlaying = false;
                        record_bnt.setImageResource(R.drawable.play);
                        break;
                    }else {
                        if(S2 == 60){
                            M2 +=1;
                            S2 = 0;
                        }
                    }
                    this.sendEmptyMessageDelayed(MESSAGE_PLAYER_TIMER, 1000);
                    Log.d(TAG, "handleMessage: 2");

                    break;
                case MESSAGE_PLAYER_START:
                    removeMessages(MESSAGE_PLAYER_TIMER);
                    S2 = 0;
                    M2 = 0;
                    time.setText(M2 + ":" + String.format("%02d", S2));
                    Log.d(TAG, "handleMessage: 3");
                    this.sendEmptyMessageDelayed(MESSAGE_PLAYER_TIMER, 1000);
                    break;
            }
        }
    }

    public String getTargetLang() {
        SharedPreferences pref = getSharedPreferences("Translator", MODE_PRIVATE);
        return pref.getString("targetlang", "");
    }

    public void upload_Record(int comment_idx, int feed_idx, int user_idx, String content) {
        File file = new File(SaveRecordData());
        long time = System.currentTimeMillis();
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", user + time + file.getName(), requestFile);
        Call<ResultData> call = service.upload_Record(body);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                ResultData resultData = response.body();
                RecordDataPath = resultData.body;
                Log.d(TAG, "onResponse: " + RecordDataPath);
                Log.d(TAG, "onResponse: " + response.body());
                setReply(comment_idx, feed_idx, user_idx, content, RecordDataPath);
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });

    }

    public void setKeyboard() {
        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                //????????? ????????? ???
                voice_layout.setVisibility(View.GONE);
                voice_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            }
        });

        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                // ????????? ????????? ???
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent setResult = new Intent();
        setResult.putExtra("content", content.getText().toString());
        setResult.putExtra("reply", mList.toString());
        setResult.putExtra("position", position);
        setResult(RESULT_OK, setResult);
        finish();
    }

    public void getPlayer(String record , SeekBar seekBar){
        try {
            if (mPlayer != null) {    // ???????????? ??????
                mPlayer.release();  // ????????? ??????
                mPlayer = null;
            }
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource("http://3.37.204.197/hellochat/"+record); // ?????? ?????? ?????? ??????
            mPlayer.prepare();  // ?????? ??????
            mPlayer.start();    // ??????
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(mPlayer.getDuration());  // ????????? ??? ????????? ????????? ???????????? ??????
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)  // ???????????? ???????????? ????????????
                    mPlayer.seekTo(progress);   // ??????????????? ????????????(????????? ???????????? ????????????)
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mPlayer.start();
        new Thread(new Runnable() {  // ????????? ??????
            @Override
            public void run() {
                while (mPlayer.isPlaying()) {  // ????????? ??????????????? ?????? ???????????? ???
                    try {
                        Thread.sleep(500); // 1????????? ????????? ???????????? ???
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // ?????? ???????????? ????????? ????????? ???????????? ??????
                    seekBar.setProgress(mPlayer.getCurrentPosition());
                }
            }
        }).start();

    }
    public void Delete_Comment(int comment_idx) {
        //????????? ????????????
        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Reply.this);
        dia.setTitle("?????????????????????????");
        dia.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_comment(comment_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        //????????? ??????
                        //alert????????? ?????????????????? ????????????
                        if (response.isSuccessful()) {
                            ResultData resultData = response.body();
                            Log.d(TAG, "onResponse: " + resultData.body);
                            if (resultData.body.equals("ok")) {
                                content.setText("????????? ???????????????.");
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                    }
                });
            }
        });
        dia.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dia.create();
        alertDialog.show();
    }
    public void Delete_Reply(int reply_idx) {
        //????????? ????????????
        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Reply.this);
        dia.setTitle("?????????????????????????");
        dia.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_comment(reply_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        getReply(comment_idx);
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                    }
                });
            }
        });
        dia.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dia.create();
        alertDialog.show();
    }
    void OnActivityResult(){
        modify_comment = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent getIntent = result.getData();
                            String contents = getIntent.getStringExtra("content");
                            content.setText(contents);
                        }
                    }
                });
        modify_reply = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent getIntent = result.getData();
                            getReply(comment_idx);
                        }
                    }
                });
    }
}