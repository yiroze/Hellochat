package com.example.hellochat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hellochat.Adapter.ChattingAdapter;
import com.example.hellochat.Adapter.MultiImageAdapter;
import com.example.hellochat.ChatApi;
import com.example.hellochat.ClientService;
import com.example.hellochat.DTO.ChatData;
import com.example.hellochat.DTO.Chatting;
import com.example.hellochat.DTO.UploadResult;
import com.example.hellochat.JoinApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.SoftKeyboardDectectorView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Chatting extends AppCompatActivity {
    private static final String TAG = "Activity_Chatting";

    RecyclerView mRecyclerView;
    ChattingAdapter mAdapter;
    ArrayList<Chatting> mData;
    ChatData resultData;
    Uri imageUri;
    EditText edit_chat;
    ImageView send, back, image;
    TextView name;
    private File tempFile;
    ArrayList<String> tmp_name_list = new ArrayList<>();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int user;
    private Parcelable recyclerViewState;
    int page = 1, limit = 20;
    boolean is_last_position = true;
    int position;
    int GALLARY = 3000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_chatting);
        InitView();
        Intent getIntent = getIntent();
        mData = new  ArrayList<Chatting>();
        user = getIntent.getIntExtra("user_idx", 0);
        Intent service_intent = new Intent(Activity_Chatting.this, ClientService.class);
        service_intent.putExtra("user_idx", user);
        startService(service_intent);
        name.setText(getIntent.getStringExtra("user_name"));
        setAdapter(getPref(), user);
        back.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: " + mRecyclerView + " " + (mData.size() - 1) + " ");
            mRecyclerView.scrollToPosition(mData.size() - 1);
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                is_last_position = false;
                position = lastVisibleItemPosition;
                Log.d(TAG, "onScrolled: " + is_last_position + " position == " + lastVisibleItemPosition);
                if (lastVisibleItemPosition == itemTotalCount) {
                    is_last_position = true;
                    Log.d(TAG, "last Position..." + itemTotalCount + is_last_position);
                } else if (firstVisibleItemPosition == 0) {
                    page++;
                    Log.d(TAG, "Position=5" + page);

                }
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


        send.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: " + edit_chat.getText().toString());
            Intent intent = new Intent(Activity_Chatting.this, ClientService.class);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("content", edit_chat.getText().toString());
                jsonObject.put("accept_user_idx", user);
                jsonObject.put("content_type", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra("msg", jsonObject.toString());
            startService(intent);
            edit_chat.setText("");
        });

        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));
        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                //키보드 등장할 때
                Log.d(TAG, "onShowSoftKeyboard: ");
                if (is_last_position) {
                    Log.d(TAG, "onShowSoftKeyboard: " + (mData.size() - 1));
                    mRecyclerView.scrollToPosition(mData.size() - 1);
                } else if (position == mData.size() - 2) {
                    mRecyclerView.scrollToPosition(mData.size() - 1);
                }
            }
        });
        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                // 키보드 사라질 때
                Log.d(TAG, "onHiddenSoftKeyboard: ");
                if (is_last_position) {
                    Log.d(TAG, "onHiddenSoftKeyboard: " + (mData.size() - 1));
                    mRecyclerView.scrollToPosition(mData.size() - 1);
                } else if (position == mData.size() - 2

                ) {
                    mRecyclerView.scrollToPosition(mData.size() - 1);
                }
            }
        });
        set_check(user);
    }

    public void initAdapter(int myidx, int user_idx, Boolean is_send) {
        Log.d(TAG, "initAdapter: " + myidx + " " + user_idx);
        ChatApi service = RetrofitClientInstance.getRetrofitInstance().create(ChatApi.class);
        Call<ChatData> call = service.getChatting(myidx, user_idx);
        call.enqueue(new Callback<ChatData>() {
            @Override
            public void onResponse(Call<ChatData> call, Response<ChatData> response) {
                Log.d(TAG, "onResponse: " + response.body());
                resultData = response.body();
                mData = resultData.body;
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                mAdapter = new ChattingAdapter(mData, getPref());
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                Log.d(TAG, "onResponse: " + (mData.size() - 1));
                if (is_send) {
                    Log.d(TAG, "onResponse: " + (mData.size() - 1));
                    mRecyclerView.scrollToPosition(mData.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<ChatData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    public void setAdapter(int myidx, int user_idx) {
        Log.d(TAG, "initAdapter: " + myidx + " " + user_idx);
        ChatApi service = RetrofitClientInstance.getRetrofitInstance().create(ChatApi.class);
        Call<ChatData> call = service.getChatting(myidx, user_idx);
        call.enqueue(new Callback<ChatData>() {
            @Override
            public void onResponse(Call<ChatData> call, Response<ChatData> response) {
                resultData = response.body();
                mData = resultData.body;
                Log.d(TAG, "onResponse: " +mData.toString());
                recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                mAdapter = new ChattingAdapter(mData, getPref());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mData.size() - 1);
                Log.d(TAG, "onResponse: "+mData.get(10).checked);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.scrollToPosition(mData.size() - 1);
                    }
                }, 50);
            }

            @Override
            public void onFailure(Call<ChatData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent.getBooleanExtra("send_result", false));
        //받은데이터인지 보낸데이터인지 구분
        if (intent.getBooleanExtra("send_result", false)) {
            initAdapter(getPref(), user, intent.getBooleanExtra("send_result", false));
        } else {
            initAdapter(getPref(), user, is_last_position);
        }

    }

    public int getPref() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        return Integer.parseInt(pref.getString("Login_data", ""));
    }

    public void InitView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        edit_chat = findViewById(R.id.edit_chat);
        send = findViewById(R.id.send);
        name = findViewById(R.id.name);
        mRecyclerView = findViewById(R.id.chat_list_recycler);
        back = findViewById(R.id.back);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        image = findViewById(R.id.add_image);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent service_intent = new Intent(Activity_Chatting.this, ClientService.class);
        service_intent.putExtra("user_idx", 0);
        startService(service_intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {
                //여러개 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e(TAG, "onActivityResult: " + clipData.getItemCount());
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    imageUri = clipData.getItemAt(i).getUri();
                    upload_Image(imageUri , i+1 , clipData.getItemCount());
                }
            }
        }
    }

    public void set_check(int user){
        Intent intent = new Intent(Activity_Chatting.this, ClientService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", "");
            jsonObject.put("accept_user_idx", user);
            jsonObject.put("content_type", 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("msg", jsonObject.toString());
        startService(intent);
    }

    public void upload_Image(Uri imageUri, int count , int last_count) {
            //이미지 서버에 업로드
            Cursor cursor = null;
            try {
//                  Uri 스키마를
//                  content:/// 에서 file:/// 로  변경한다.
                String[] proj = {MediaStore.Images.Media.DATA};
                assert imageUri != null;
                cursor = getContentResolver().query(imageUri, proj, null, null, null);
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
                        if(last_count == count){
                            Intent intent = new Intent(Activity_Chatting.this, ClientService.class);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("content", tmp_name_list.toString());
                                jsonObject.put("accept_user_idx", user);
                                jsonObject.put("content_type", 2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            intent.putExtra("msg", jsonObject.toString());
                            startService(intent);
                            tmp_name_list.clear();
                            Log.d(TAG, "onResponse: tmp_name_list is clear? " +tmp_name_list.size());
                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadResult> call, Throwable t) {
                    Log.e(TAG, "onFailure: 이미지업로드 실패" + t.toString());
                }
            });
        }
}

