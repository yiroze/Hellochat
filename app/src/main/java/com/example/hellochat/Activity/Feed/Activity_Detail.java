package com.example.hellochat.Activity.Feed;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellochat.Activity.UserPage.Activity_MyDetail;
import com.example.hellochat.Activity.UserPage.Activity_UserDetail;
import com.example.hellochat.Adapter.Feed.DetailAdapter2;
import com.example.hellochat.DTO.Feed.CommentData;
import com.example.hellochat.DTO.Feed.DetailData;
import com.example.hellochat.DTO.Feed.DetailResult;
import com.example.hellochat.DTO.Feed.ReplyList;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Service.ClientService;
import com.example.hellochat.SoftKeyboardDectectorView;
import com.example.hellochat.Util.GetLanguageCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Detail extends AppCompatActivity {
    //??????????????????
    DetailAdapter2 mAdapter;
    DetailResult datalist;
    RecyclerView mRecyclerView;
    List<DetailData> datainfo;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;

    //????????????
    EditText editText;
    ImageView cancel, submit , back;
    TextView writer, parent;
    RelativeLayout relativelayout;
    int page = 1, limit = 5;
    int feed, user;
    int feednum;
    private static final String TAG = "Activity_Detail";


    //????????? , ????????????
    MediaRecorder recorder;
    MediaPlayer mPlayer;
    boolean isRecording, isPlaying, RecordState, RecordDataState = false;
    ConstraintLayout voice_layout;
    String RecordDataPath;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200, MESSAGE_RECORD_TIMER = 100,
            MESSAGE_RECORD_START = 103, MESSAGE_PLAYER_TIMER = 101, MESSAGE_PLAYER_START = 102;
    TextView time;
    TimerHandler timerHandler;
    ImageView record_bnt, mic, record_ok, reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initView();
        setKeyboard();
        timerHandler = new TimerHandler();
        Intent intent = getIntent();
        feednum = intent.getIntExtra("feed_idx", 0);
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String user = pref.getString("Login_data", "");
        int user_idx = Integer.parseInt(user);
        datainfo = new ArrayList<>();

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mAdapter = new DetailAdapter2(datainfo);
        mAdapter.setOnMoreBntClick_Contents(new DetailAdapter2.OnMoreBntClick_Contents() {
            @Override
            public void onMoreBntClick_Contents(View v, int pos) {
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(Activity_Detail.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //?????????????????? ?????????
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.divider_line));

        getDetail(feednum, user_idx, page, limit);

        submit.setOnClickListener(v -> {
            if (!editText.getText().toString().equals("") || RecordDataState) {
                if (RecordDataState) {
                    upload_Record();
                } else {
                    RecordDataPath = "";
                }
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (parent.getText().toString().equals("")) {
                            setComment(feednum, user_idx, editText.getText().toString(), 0, page, limit, RecordDataPath);
                            editText.setText("");
                        } else {
                            setComment(feednum, user_idx, editText.getText().toString(), Integer.parseInt(parent.getText().toString()), page, limit, RecordDataPath);
                            editText.setText("");
                            parent.setText("");
                            relativelayout.setVisibility(View.GONE);
                        }
                    }
                }, 250); //1 ??????
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

        cancel.setOnClickListener(v -> {
            relativelayout.setVisibility(View.GONE);
            writer.setText("");
            parent.setText("");
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getPaging(feednum, user_idx, page, limit);
                }
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
        back.setOnClickListener(v -> {
            finish();
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode != 11) {
                return;
            }
            getDetail(feed, user, page, limit);
        }
        if (requestCode == 1) {
            int pos= data.getIntExtra("position" , -1);
            String a = data.getStringExtra("reply");
            ArrayList<ReplyList> mList = new ArrayList<>();
            String content = data.getStringExtra("content");
            try {
                JSONArray jsonArray = new JSONArray(a.replace("ReplyList" , ""));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    mList.add(new ReplyList(jsonObject.getInt("user_idx"), jsonObject.get("name").toString()
                    , jsonObject.getString("profile") , jsonObject.get("contents").toString() , jsonObject.get("date").toString()
                    , jsonObject.getInt("comment_idx") , jsonObject.get("record").toString()));
                }
                datainfo.set(pos , datainfo.get(pos).update_reply(mList, content));
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getDetail(int feed_idx, int user_idx, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<DetailResult> call = service.get_detail(feed_idx, user_idx, page, limit);
        feed = feed_idx;
        user = user_idx;
        call.enqueue(new Callback<DetailResult>() {
            @Override
            public void onResponse(Call<DetailResult> call, Response<DetailResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body().result);
                    progressBar.setVisibility(View.GONE);
                    datalist = response.body();
                    datainfo = datalist.body;
                    Log.d(TAG, "onResponse: " + datainfo);
                    mAdapter = new DetailAdapter2(datainfo);
                    Log.d(TAG, "onResponse: setadapter");
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    //?????? ????????? ??????
                    setClick(mAdapter, feed_idx, user_idx, page, limit);
                }
            }

            @Override
            public void onFailure(Call<DetailResult> call, Throwable t) {
                Log.d(TAG, "onFailure: ????????????" + t.getMessage());
            }
        });
    }

    public void getPaging(int feed_idx, int user_idx, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<DetailResult> call = service.get_detailPaging(feed_idx, user_idx, page, limit);
        feed = feed_idx;
        user = user_idx;
        call.enqueue(new Callback<DetailResult>() {
            @Override
            public void onResponse(Call<DetailResult> call, Response<DetailResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body().result);
                    progressBar.setVisibility(View.GONE);
                    datalist = response.body();
                    List<DetailData> newList = datalist.body;
                    Log.d(TAG, "onResponse: "+newList.toString());
                    Log.d(TAG, "onResponse:page "+page);
                    Log.d(TAG, "onResponse:newList.size() "+newList.size());
                    for (int i = 0; i < newList.size(); i++) {
                        datainfo.add(new DetailData(newList.get(i).user_idx, newList.get(i).name, newList.get(i).profile, newList.get(i).mylang,
                                newList.get(i).mylang2, newList.get(i).mylang3, newList.get(i).studylang, newList.get(i).studylang2,
                                newList.get(i).studylang3, newList.get(i).contents, newList.get(i).date, newList.get(i).studylang_level,
                                newList.get(i).studylang_level2, newList.get(i).studylang_level3, newList.get(i).heart, newList.get(i).comment,
                                newList.get(i).view_type, newList.get(i).parents, newList.get(i).comment_idx, newList.get(i).islike, newList.get(i).like_cnt,
                                newList.get(i).comment_cnt, newList.get(i).image, newList.get(i).record, newList.get(i).reply, false , newList.get(i).reply_cnt));
                    }
                    mAdapter.notifyDataSetChanged();
                    //?????? ????????? ??????
                }
            }

            @Override
            public void onFailure(Call<DetailResult> call, Throwable t) {
                Log.d(TAG, "onFailure: ????????????" + t.getMessage());
            }
        });
    }

    public void setComment(int feed_idx, int user_idx, String comment, int parent, int page, int limit, String recordDataPath) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<CommentData> call = service.set_comment(feed_idx, user_idx, comment, parent, recordDataPath);
        call.enqueue(new Callback<CommentData>() {
            @Override
            public void onResponse(Call<CommentData> call, Response<CommentData> response) {
                if (response.isSuccessful()) {
                    getDetail(feed_idx, user_idx, page, limit);
                    CommentData data = response.body();
                    Log.d(TAG, "onResponse: " + data.toString());
                    if (data.accept_user != user_idx) {
                        Intent intent = new Intent(Activity_Detail.this, ClientService.class);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("content", data.comment_idx);
                            jsonObject.put("accept_user_idx", data.accept_user);
                            if (parent == 0) {
                                jsonObject.put("content_type", 11);
                            } else {
                                jsonObject.put("content_type", 12);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("msg", jsonObject.toString());
                        startService(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentData> call, Throwable t) {
            }
        });
    }

    //?????????????????? ????????? ?????????
    public class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable divider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

    public void ClickHeart(int feed_idx, int user_idx, int page, int limit, int accept_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_like(feed_idx, user_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                getDetail(feed_idx, user_idx, page, limit);
                Intent intent = new Intent(Activity_Detail.this, ClientService.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("content", feed_idx);
                    jsonObject.put("accept_user_idx", accept_idx);
                    jsonObject.put("content_type", 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("msg", jsonObject.toString());
                startService(intent);
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
            }
        });
    }

    public void Delete_Comment(int comment_idx, int feed_idx, int user_idx, int page, int limit) {
        //????????? ????????????
        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
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
                                getDetail(feed_idx, user_idx, page, limit);
                                Log.d(TAG, "onResponse: ");
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

    public void Delete_Reply(int comment_idx, int feed_idx, int user_idx, int page, int limit) {
        //???????????? ??????
        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
        dia.setTitle("?????????????????????????");
        dia.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_reply(comment_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        //????????? ??????
                        //alert????????? ?????????????????? ????????????
                        if (response.isSuccessful()) {
                            ResultData resultData = response.body();
                            Log.d(TAG, "onResponse: " + resultData.body);
                            if (resultData.body.equals("ok")) {
                                getDetail(feed_idx, user_idx, page, limit);
                                Log.d(TAG, "onResponse: ");
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

    public void Delete(int feed_idx) {
        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
        dia.setTitle("?????????????????????????");
        dia.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_post(feed_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        //????????? ??????
                        //alert????????? ?????????????????? ????????????
                        if (response.isSuccessful()) {
                            ResultData resultData = response.body();
                            Log.d(TAG, "onResponse: " + resultData.body);
                            if (resultData.body.equals("ok")) {
                                Log.d(TAG, "onResponse: ");
                                finish();
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

    public void setClick(DetailAdapter2 mAdapter, int feed_idx, int user_idx, int page, int limit) {
        mAdapter.setOnReplyClick(new DetailAdapter2.OnReplyClick() {
            @Override
            public void onReplyClick(View v, int position) {
                Intent intent = new Intent(Activity_Detail.this, Activity_Reply.class);
                intent.putExtra("comment", datainfo.get(position).contents);
                intent.putExtra("profile", datainfo.get(position).profile);
                intent.putExtra("name", datainfo.get(position).name);
                intent.putExtra("date", datainfo.get(position).date);
                intent.putExtra("comment_idx", datainfo.get(position).comment_idx);
                intent.putExtra("feed_num", feednum);
                intent.putExtra("position" , position);
                intent.putExtra("record" , datainfo.get(position).record);
                intent.putExtra("user_idx" , datainfo.get(position).user_idx);
                startActivityForResult(intent, 1);
            }
        });
        //??? ????????? ??????
        mAdapter.setOnClickListener(new DetailAdapter2.OnLikeClick() {
            @Override
            public void onLikeClick(View v, int pos) {
                ClickHeart(feed_idx, user_idx, page, limit, datainfo.get(pos).user_idx);
            }
        });
        //?????? ??????????????????
        mAdapter.setOnMoreBntClick_Reply(new DetailAdapter2.OnMoreBntClick_Reply() {
            @Override
            public void onMoreBntClick_Reply(View v, int pos) {
                String items[] = {"????????????", "????????????"};
                AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
                dia.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(Activity_Detail.this, Activity_modify_comment.class);
                            intent.putExtra("contents", datainfo.get(pos).contents);
                            intent.putExtra("comment_idx", datainfo.get(pos).comment_idx);
                            startActivityForResult(intent, 10);
                        } else if (which == 1) {
                            Delete_Reply(datainfo.get(pos).comment_idx, feed_idx, user_idx, page, limit);
                        }
                    }
                });
                AlertDialog alertDialog = dia.create();
                alertDialog.show();
            }
        });
        //?????? ??????????????????
        mAdapter.setOnMoreBntClick_Comment(new DetailAdapter2.OnMoreBntClick_Comment() {
            @Override
            public void onMoreBntClick_Comment(View v, int pos) {
                String items[] = {"????????????", "????????????"};
                AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
                dia.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(Activity_Detail.this, Activity_modify_comment.class);
                            intent.putExtra("contents", datainfo.get(pos).contents);
                            intent.putExtra("comment_idx", datainfo.get(pos).comment_idx);
                            startActivityForResult(intent, 10);
                        } else if (which == 1) {
                            Delete_Comment(datainfo.get(pos).comment_idx, feed_idx, user_idx, page, limit);
                        }
                    }
                });
                AlertDialog alertDialog = dia.create();
                alertDialog.show();
            }
        });
        mAdapter.setOnMoreBntClick_Contents(new DetailAdapter2.OnMoreBntClick_Contents() {
            @Override
            public void onMoreBntClick_Contents(View v, int pos) {
                String items[] = {"????????????", "????????????"};
                AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
                dia.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(Activity_Detail.this, Activity_modify.class);
                            intent.putExtra("feed_idx", feednum);
                            startActivity(intent);
                        } else if (which == 1) {
                            Delete(feednum);
                        }
                    }
                });
                AlertDialog alertDialog = dia.create();
                alertDialog.show();
            }
        });
        mAdapter.setOpenUserDetail(new DetailAdapter2.OpenUserDetail() {
            @Override
            public void openUserDetail(View v, int position) {
                Intent intent = new Intent(Activity_Detail.this, Activity_UserDetail.class);
                intent.putExtra("user_idx", datainfo.get(position).user_idx);
                startActivity(intent);
            }
        });
        mAdapter.setOpenMyDetail(new DetailAdapter2.OpenMyDetail() {
            @Override
            public void openMyDetail(View v, int position) {
                Intent intent = new Intent(Activity_Detail.this, Activity_MyDetail.class);
                intent.putExtra("user_idx", datainfo.get(position).user_idx);
                startActivity(intent);
            }
        });
        mAdapter.setOnLongClickListener(new DetailAdapter2.OnContentLongClickListener() {
            @Override
            public void onContentLongClick(View v, int position) {
                PopupMenu popupMenu = new PopupMenu(Activity_Detail.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menulist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.trans:
                                Log.d(TAG, "onMenuItemClick: " + getFirstTargetLang());
                                Intent intentTrans = new Intent(Activity_Detail.this, Activity_Trans.class);
                                intentTrans.putExtra("content", datainfo.get(position).contents);
                                intentTrans.putExtra("targetLang", getFirstTargetLang());
                                startActivity(intentTrans);
                                return true;

                            case R.id.tts:
                                String text = datainfo.get(position).contents;
                                new GetLanguageCode(text, Activity_Detail.this).start();
                                return true;

                            case R.id.copy:
                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("text", datainfo.get(position).contents); //??????????????? ID?????? ???????????? id ?????? ???????????? ??????
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(Activity_Detail.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                return true;

                        }
                        return false;
                    }
                });
                popupMenu.show();
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

    public void initView() {
        submit = (ImageView) findViewById(R.id.submit);
        editText = (EditText) findViewById(R.id.comment_edit);
        writer = (TextView) findViewById(R.id.writer);
        parent = (TextView) findViewById(R.id.parent);
        relativelayout = (RelativeLayout) findViewById(R.id.relativelayout);
        cancel = (ImageView) findViewById(R.id.cancel);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        time = (TextView) findViewById(R.id.time);
        voice_layout = (ConstraintLayout) findViewById(R.id.voice_layout);
        reset = (ImageView) findViewById(R.id.reset);
        record_bnt = (ImageView) findViewById(R.id.recording);
        mic = (ImageView) findViewById(R.id.record);
        record_ok = (ImageView) findViewById(R.id.record_ok);
        back = findViewById(R.id.back);
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


    public String getFirstTargetLang(){
        SharedPreferences pref = getSharedPreferences("Translator", MODE_PRIVATE);
        JSONArray JSON = null;
        try {
            JSON = new JSONArray(pref.getString("targetlang", ""));
            return JSON.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void upload_Record() {
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
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });

    }


}