package com.example.hellochat.Activity.Feed;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellochat.Adapter.Image.MultiImageAdapter;
import com.example.hellochat.Service.ClientService;
import com.example.hellochat.DTO.Feed.EditData;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.Feed.UploadResult;
import com.example.hellochat.Interface.JoinApi;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
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

public class Activity_Edit extends AppCompatActivity {
    EditText content;
    ImageView back, edit, record_bnt, play_control, record_cancel, image, voice;
    String TAG = this.getClass().getName();
    Button save, reset;
    RecyclerView recyclerView;
    MultiImageAdapter adapter;
    ArrayList<Uri> uriList = new ArrayList<>();
    Uri imageUri;
    ArrayList<String> tmp_name_list = new ArrayList<>();
    int GALLARY = 3000, GALLARY_RESULT = 3001, rec_pos = 0;
    private File tempFile;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200, MESSAGE_RECORD_TIMER = 100, MESSAGE_RECORD_START = 103, MESSAGE_PLAYER_TIMER = 101, MESSAGE_PLAYER_START = 102;
    ConstraintLayout voice_layout, player_layout;
    boolean isRecording, isPlaying, RecordState, RecordDataState = false;
    MediaRecorder recorder;
    MediaPlayer mPlayer;
    AppCompatSeekBar seekbar;
    TextView time;
    TimerHandler timerHandler;
    String user_index, RecordDataPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initViews();
        save.setEnabled(false);
        reset.setEnabled(false);
        timerHandler = new TimerHandler();

        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        user_index = pref.getString("Login_data", "");
        final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        back.setOnClickListener(v -> {
            finish();
        });
        image.setOnClickListener(v -> {
            verifyStoragePermissions(this);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLARY);
        });

        record_cancel.setOnClickListener(v -> {
            RecordDataState = false;
            player_layout.setVisibility(View.GONE);
        });

        voice.setOnClickListener(v -> {
            PermissionCheck();
            try {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            voice_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500));
            voice_layout.setVisibility(View.VISIBLE);
        });

        //Retrofit ???????????? ??????
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        edit.setOnClickListener(v -> {
            upload_Image();
            if (RecordDataState) {
                upload_Record();
            }
            Log.d(TAG, "????????????");
            String content_data = content.getText().toString();
            Log.e(TAG, "onCreate: " + tmp_name_list.toString());
            Log.d(TAG, "onCreate: " + RecordDataPath);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    // ?????? ?????? ??? ????????? ??????
                    Call<EditData> call = service.getEditData(user_index, content_data, tmp_name_list.toString(), RecordDataPath);
                    call.enqueue(new Callback<EditData>() {
                        @Override
                        public void onResponse(Call<EditData> call, Response<EditData> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "onResponse: ???????????? ");
                                EditData result;
                                result = response.body();
                                Log.d(TAG, "onResponse: " + result);
                                Log.d(TAG, "onResponse: " + user_index + content_data);
                                assert result != null;
                                // TCP ????????? ???????????????
                                Intent intent = new Intent(Activity_Edit.this, ClientService.class);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("content", result.idx);
                                    jsonObject.put("accept_user_idx", 0);
                                    jsonObject.put("content_type", 9);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("msg", jsonObject.toString());
                                startService(intent);


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
                                    //???????????? ???????????? ??????????????? ????????? ?????????????????? ?????????????????? ????????????
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Edit.this);
                                    builder.setTitle(result.msg);
                                    builder.setPositiveButton("ok", null);
                                    builder.create().show();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EditData> call, Throwable t) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Edit.this);
                            builder.setTitle("????????? ????????? ??????????????? ????????? ????????????????????????");
                            builder.setPositiveButton("ok", null);
                            builder.create().show();
                        }
                    });
                }
            }, 1000); //1 ??????


        });

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

        save.setOnClickListener(v -> {
            player_layout.setVisibility(View.VISIBLE);
            RecordState = false;
            record_bnt.setImageResource(R.drawable.record);
            time.setText("0:00");
            isPlaying = false;
            save.setEnabled(false);
            reset.setEnabled(false);
            copyFile(getFilePath(), SaveRecordData());
            RecordDataState = true;
        });
        reset.setOnClickListener(v -> {
            isPlaying = false;
            RecordState = false;
            time.setText("0:00");
            save.setEnabled(false);
            reset.setEnabled(false);
            record_bnt.setImageResource(R.drawable.record);
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
                    timerHandler.removeMessages(MESSAGE_RECORD_TIMER);
                    Log.d(TAG, "onCreate: ?????? ??????");
                    save.setEnabled(true);
                    reset.setEnabled(true);
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
                        rec_pos = mPlayer.getCurrentPosition();    // ???????????? ?????????????????? ?????????
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
        play_control.setOnClickListener(v -> {
            playClicked();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "???????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
            } else {
                //???????????? ???????????? ???????????? ??????
                if (data.getClipData() == null) {
                    Log.e(TAG, "onActivityResult: " + String.valueOf(data.getData()));
                    imageUri = data.getData();
                    uriList.add(imageUri);

                } else {
                    //????????? ????????? ??????
                    ClipData clipData = data.getClipData();
                    Log.e(TAG, "onActivityResult: " + clipData.getItemCount());
                    if (clipData.getItemCount() > 9) {
                        //????????????
                        Toast.makeText(getApplicationContext(), "????????? 9??? ?????? ?????????????????????", Toast.LENGTH_LONG).show();
                    } else {
                        if (uriList.size() + clipData.getItemCount() > 9) {
                            Toast.makeText(getApplicationContext(), "????????? 9??? ?????? ?????????????????????", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onActivityResult: " + uriList.size() + clipData.getItemCount());
                        } else {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                imageUri = clipData.getItemAt(i).getUri();
                                try {
                                    uriList.add(imageUri);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
                adapter = new MultiImageAdapter(uriList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                adapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        uriList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    }
                });

            }
        }

    }


    public void initViews() {
        content = (EditText) findViewById(R.id.content);
        back = (ImageView) findViewById(R.id.back);
        edit = (ImageView) findViewById(R.id.edit);
        image = (ImageView) findViewById(R.id.image);
        voice = (ImageView) findViewById(R.id.voice_msg);
        recyclerView = findViewById(R.id.image_recycler);
        record_bnt = (ImageView) findViewById(R.id.recording);
        seekbar = (AppCompatSeekBar) findViewById(R.id.SeekBar);
        play_control = (ImageView) findViewById(R.id.player_control);
        voice_layout = (ConstraintLayout) findViewById(R.id.voice_layout);
        time = (TextView) findViewById(R.id.time);
        player_layout = (ConstraintLayout) findViewById(R.id.player_layout);
        save = (Button) findViewById(R.id.save);
        reset = (Button) findViewById(R.id.reset);
        record_cancel = (ImageView) findViewById(R.id.record_cancel);
    }

    public void upload_Image() {
        for (int i = 0; i < uriList.size(); i++) {
            //????????? ????????? ?????????
            Cursor cursor = null;
            try {
//                  Uri ????????????
//                  content:/// ?????? file:/// ???  ????????????.
                String[] proj = {MediaStore.Images.Media.DATA};
                assert uriList.get(i) != null;
                cursor = getContentResolver().query(uriList.get(i), proj, null, null, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                tempFile = new File(cursor.getString(column_index));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            Log.d(TAG, "upload_file: " + tempFile);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", tempFile.getName(), requestFile);
            tmp_name_list.add("image/" + tempFile.getName());
            JoinApi service = RetrofitClientInstance.getRetrofitInstance().create(JoinApi.class);
            Call<UploadResult> call = service.uploadFile(body);
            call.enqueue(new Callback<UploadResult>() {
                @Override
                public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                    if (response.isSuccessful()) {
                        UploadResult uploadResult;
                        uploadResult = response.body();
                        assert uploadResult != null;
                        Log.e(TAG, "onResponse:??????????????????  " + uploadResult.toString());
                    }
                }

                @Override
                public void onFailure(Call<UploadResult> call, Throwable t) {
                    Log.e(TAG, "onFailure: ?????????????????? ??????" + t.toString());
                }
            });
        }
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


    public void playClicked() {
        try {
            if (mPlayer != null) {    // ???????????? ??????
                mPlayer.release();  // ????????? ??????
                mPlayer = null;
                Log.d(TAG, "onCreate: ???????????????");
            }
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(SaveRecordData()); // ?????? ?????? ?????? ??????
            mPlayer.prepare();  // ?????? ??????
            mPlayer.start();    // ??????
            Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekbar.setMax(mPlayer.getDuration());  // ????????? ??? ????????? ????????? ???????????? ??????
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    seekbar.setProgress(mPlayer.getCurrentPosition());
                }
            }
        }).start();

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
                    if (M2 == M && S2 == S) {
                        //??? ????????? ???????????????
                        isPlaying = false;
                        record_bnt.setImageResource(R.drawable.play);
                        break;
                    } else {
                        if (S2 == 60) {
                            M2 += 1;
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_RECORD_AUDIO_PERMISSION:
//                if (grantResults.length > 0) {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        //??????
//                        Toast.makeText(this, "?????? ??????", Toast.LENGTH_LONG).show();
//                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                        Toast.makeText(this, "?????? ??????", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(this, "????????? ???????????? ??????", Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//
//    }

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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
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

    public void upload_Record() {
        File file = new File(SaveRecordData());
        long time = System.currentTimeMillis();
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", user_index + time + file.getName(), requestFile);
        Call<ResultData> call = service.upload_Record(body);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                ResultData resultData = response.body();
                RecordDataPath = resultData.body;
                Log.d(TAG, "onResponse: " + RecordDataPath);
                Log.d(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }


}