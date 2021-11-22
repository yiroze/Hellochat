package com.example.hellochat.Activity.Feed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContextWrapper;
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

import com.example.hellochat.Adapter.Image.MultiImageAdapter;
import com.example.hellochat.DTO.Feed.GetContents;
import com.example.hellochat.DTO.Feed.UploadResult;
import com.example.hellochat.Interface.JoinApi;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.SoftKeyboardDectectorView;

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

public class Activity_modify extends AppCompatActivity {
    EditText content;
    ImageView back, edit, record_bnt, play_control, record_cancel , image, voice;
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
    boolean isRecording, isPlaying, RecordState, RecordDataState = false , OriginalDataDeleted;
    MediaRecorder recorder;
    MediaPlayer mPlayer;
    AppCompatSeekBar seekbar;
    TextView time;
    TimerHandler timerHandler;
    String user_index, RecordDataPath;
    String nowPlayPath , OriginalRecordData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_modify);
        initViews();
        save.setEnabled(false);
        reset.setEnabled(false);
        timerHandler = new TimerHandler();

        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        user_index = pref.getString("Login_data", "");
        final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        //수정 정보 가져오기
        Intent intentA = getIntent();
        int feed_idx = intentA.getIntExtra("feed_idx", 0);
        Log.d(TAG, "onCreate: " + feed_idx);
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<GetContents> call_feed = service.get_content(feed_idx);
        call_feed.enqueue(new Callback<GetContents>() {
            @Override
            public void onResponse(Call<GetContents> call, Response<GetContents> response) {
                if (response.isSuccessful()) {
                    GetContents resultData = response.body();
                    content.setText(resultData.contents);
                    String image_data = resultData.image;
                    OriginalRecordData = resultData.Record;
                    if(OriginalRecordData.equals("")){
                        RecordDataState = false;
                        player_layout.setVisibility(View.GONE);
                    }else {
                        nowPlayPath = "http://3.37.204.197/hellochat/"+OriginalRecordData;
                    }

                    if (!image_data.equals("[]") && !image_data.equals("")) {
                        String a = image_data.replace("[", "").replace("]", "").replace(" ", "");
                        Log.d(TAG, "onResponse: "+a);
                        String[] item = a.split(",");
                        for (int i = 0; i < item.length; i++) {
                            Uri image_uri = Uri.parse("http://3.37.204.197/hellochat/" + item[i]);
                            uriList.add(image_uri);
                        }
                    }
                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(Activity_modify.this, 3));
                    adapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            uriList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                            adapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    uriList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetContents> call, Throwable t) {

            }
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
            OriginalRecordData = null;
            OriginalDataDeleted = true;
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

        edit.setOnClickListener(v -> {
            upload_Record();
            upload_Image();
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    Log.d(TAG, "run: "+RecordDataPath);
                    Call<ResultData> call = service.modify_post(feed_idx, content.getText().toString(), tmp_name_list.toString(), RecordDataPath);
                    call.enqueue(new Callback<ResultData>() {
                        @Override
                        public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                            if (response.isSuccessful()) {
                                ResultData resultData = response.body();
                                Log.d(TAG, "onResponse: " + resultData.body);
                                if (resultData.body.equals("ok")) {
                                    Log.d(TAG, "onResponse: ");
                                    finish();
                                } else {
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
                }
            }, 1000); //1 초후
        });

        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));
        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                //키보드 등장할 때
                voice_layout.setVisibility(View.GONE);
                voice_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
            }
        });
        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                // 키보드 사라질 때
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
            OriginalRecordData= null;
            nowPlayPath = SaveRecordData();
            OriginalDataDeleted= false;
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
                recorder = new MediaRecorder(); // 미디어리코더 객체 생성
                Log.d(TAG, "onCreate: 미디어객체생성");
            }
            if (!RecordState) {
                if (!isRecording) {
                    isRecording = true;
                    record_bnt.setImageResource(R.drawable.record_stop);
                    recordAudio();
                    timerHandler.sendEmptyMessage(MESSAGE_RECORD_START);
                    Log.d(TAG, "onCreate: 녹화 시작");
                } else {
                    stopRecording();
                    isRecording = false;
                    record_bnt.setImageResource(R.drawable.play);
                    RecordState = true;
                    timerHandler.removeMessages(MESSAGE_RECORD_TIMER);
                    Log.d(TAG, "onCreate: 녹화 중지");
                    save.setEnabled(true);
                    reset.setEnabled(true);
                }
            } else {
                if (!isPlaying) {
                    try {
                        if (mPlayer != null) {    // 사용하기 전에
                            mPlayer.release();  // 리소스 해제
                            mPlayer = null;
                            Log.d(TAG, "onCreate: 리소스해제");
                        }
                        mPlayer = new MediaPlayer();
                        mPlayer.setDataSource(getFilePath()); // 음악 파일 위치 지정
                        mPlayer.prepare();  // 미리 준비
                        mPlayer.start();    // 재생
                        timerHandler.sendEmptyMessage(MESSAGE_PLAYER_START);
                        Log.d(TAG, "onCreate: 재생시작");
                        Toast.makeText(getApplicationContext(), "재생시작", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isPlaying = true;
                    record_bnt.setImageResource(R.drawable.pause);
                } else {
                    if (mPlayer != null) {
                        rec_pos = mPlayer.getCurrentPosition();    // 어디까지 재생되었는지 알아냄
                        mPlayer.pause();    // 일시정지
                        Toast.makeText(getApplicationContext(), "일시정지", Toast.LENGTH_SHORT).show();
                        isPlaying = false;
                        record_bnt.setImageResource(R.drawable.play);
                        timerHandler.removeMessages(MESSAGE_PLAYER_TIMER);
                        Log.d(TAG, "onCreate: 일시정지");
                    }
                }

            }

        });
        play_control.setOnClickListener(v -> {
            playClicked();
        });

        back.setOnClickListener(v -> {
            for (int i = 0; i < uriList.size(); i++) {
                Log.e(TAG, "onCreate: " + uriList.get(i));
            }
            Log.e(TAG, "onCreate: " + tmp_name_list.toString());
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {
                //이미지를 하나라도 선택했을 경우
                if (data.getClipData() == null) {
                    Log.e(TAG, "onActivityResult: " + String.valueOf(data.getData()));
                    imageUri = data.getData();
                    uriList.add(imageUri);

                } else {
                    //여러개 선택한 경우
                    ClipData clipData = data.getClipData();
                    Log.e(TAG, "onActivityResult: " + clipData.getItemCount());
                    if (clipData.getItemCount() > 9) {
                        //초과선택
                        Toast.makeText(getApplicationContext(), "사진은 9장 까지 선택가능합니다", Toast.LENGTH_LONG).show();
                    } else {
                        if (uriList.size() + clipData.getItemCount() > 9) {
                            Toast.makeText(getApplicationContext(), "사진은 9장 까지 선택가능합니다", Toast.LENGTH_LONG).show();
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
            if (!uriList.get(i).getHost().equals("3.37.204.197")) {
                //이미지 서버에 업로드
                Cursor cursor = null;
                try {
//                  Uri 스키마를
//                  content:/// 에서 file:/// 로  변경한다.
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
                            Log.e(TAG, "onResponse:이미지업로드  " + uploadResult.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResult> call, Throwable t) {
                        Log.e(TAG, "onFailure: 이미지업로드 실패" + t.toString());
                    }
                });
            } else {
                tmp_name_list.add("image/" + uriList.get(i).getLastPathSegment());
            }

        }
    }

    //녹음 시작
    public void recordAudio() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//입력될 장치
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//저장될 포맷
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//인코더설정 디폴트
        recorder.setOutputFile(getFilePath());//저장위치 설정
        Log.d(TAG, "onCreate: " + getFilePath());
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //녹음 중지
    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }


    public void playClicked() {
        try {
            if (mPlayer != null) {    // 사용하기 전에
                mPlayer.release();  // 리소스 해제
                mPlayer = null;
                Log.d(TAG, "onCreate: 리소스해제");
            }
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(nowPlayPath); // 음악 파일 위치 지정
            mPlayer.prepare();  // 미리 준비
            mPlayer.start();    // 재생
            Toast.makeText(getApplicationContext(), "재생시작", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekbar.setMax(mPlayer.getDuration());  // 음악의 총 길이를 시크바 최대값에 적용
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)  // 사용자가 시크바를 움직이면
                    mPlayer.seekTo(progress);   // 재생위치를 바꿔준다(움직인 곳에서의 음악재생)
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mPlayer.start();
        new Thread(new Runnable() {  // 쓰레드 생성
            @Override
            public void run() {
                while (mPlayer.isPlaying()) {  // 음악이 실행중일때 계속 돌아가게 함
                    try {
                        Thread.sleep(500); // 1초마다 시크바 움직이게 함
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 현재 재생중인 위치를 가져와 시크바에 적용
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
                    S2 -= 1;
                    if (S2 == 0) {
                        if (M2 == 0) {
                            time.setText(M2 + ":" + String.format("%02d", S2));
                            isPlaying = false;
                            record_bnt.setImageResource(R.drawable.play);
                            break;
                        }
                        M2 -= 1;
                        S2 = 0;
                    }
                    time.setText(M2 + ":" + String.format("%02d", S2));
                    this.sendEmptyMessageDelayed(MESSAGE_PLAYER_TIMER, 1000);
                    Log.d(TAG, "handleMessage: 2");

                    break;
                case MESSAGE_PLAYER_START:
                    removeMessages(MESSAGE_PLAYER_TIMER);
                    S2 = S;
                    M2 = M;
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
            Toast.makeText(this, "오디오 권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "오디오 권한 없음", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                //오디오 권한 설명이 필요함
                Toast.makeText(this, "오디오 권한 설명이 필요함", Toast.LENGTH_LONG).show();
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
        if(OriginalRecordData == null ){
            if(!OriginalDataDeleted){
                Log.d(TAG, "upload_Record: 새로운 파일 받음 ");
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
        }else {
            RecordDataPath = OriginalRecordData;
        }
    }


}