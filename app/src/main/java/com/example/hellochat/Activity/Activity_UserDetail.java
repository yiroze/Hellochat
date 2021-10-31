package com.example.hellochat.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hellochat.Adapter.MyPageAdapter;
import com.example.hellochat.Adapter.UserDetailAdapter;
import com.example.hellochat.DTO.MypageData;
import com.example.hellochat.DTO.MypageResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.MypageApi;
import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_UserDetail extends AppCompatActivity {
    String TAG = this.getClass().getName();
    int idx , myidx;
    RecyclerView mRecyclerView;
    MypageResult result_data;
    ArrayList<MypageData> mypageData;
    UserDetailAdapter mAdapter;
    int page = 1, limit = 10;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    LinearLayout follow, message;
    ImageView follwImage;
    TextView followText;
    int user;
    String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Intent intent = getIntent();
        idx = intent.getIntExtra("user_idx", 0);
        Log.d(TAG, "onCreateView: " + idx);
        InitView();
        mypageData = new ArrayList<>();
        getMyPageData(idx, page, limit);
        getFollowed(myidx , idx);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getMyPageData(idx, page, limit);
                }
            }
        });
        follow.setOnClickListener(v -> {
            Follow(myidx,idx);
        });
        message.setOnClickListener(v -> {
            Intent intent_msg = new Intent(Activity_UserDetail.this , Activity_Chatting.class);
            intent_msg.putExtra("user_idx" , user);
            intent_msg.putExtra("user_name" , user_name);
            startActivity(intent_msg);
        });
    }


    public void InitView() {
        follow = (LinearLayout) findViewById(R.id.follow);
        follwImage = (ImageView) findViewById(R.id.follow_image);
        followText = (TextView) findViewById(R.id.follow_text);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        message = findViewById(R.id.message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_user);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(Activity_UserDetail.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        myidx = Integer.parseInt(pref.getString("Login_data", ""));
    }

    public void getMyPageData(int user_idx, int page, int limit) {
        MypageApi service = RetrofitClientInstance.getRetrofitInstance().create(MypageApi.class);
        Call<MypageResult> call = service.getMypage(user_idx, page, limit);
        call.enqueue(new Callback<MypageResult>() {
            @Override
            public void onResponse(Call<MypageResult> call, Response<MypageResult> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    result_data = response.body();
                    mypageData = result_data.body;
                    user =mypageData.get(0).user_idx;
                    user_name= mypageData.get(0).name;
                    mAdapter = new UserDetailAdapter(mypageData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onResponse: " + mypageData);
                    setClickListener(mAdapter, user_idx, page, limit);
                }
            }

            @Override
            public void onFailure(Call<MypageResult> call, Throwable t) {
            }
        });
    }

    public void setClickListener(UserDetailAdapter mAdapter, int user_idx, int page, int limit) {
        mAdapter.setOnFeedItemClickListener(new MyPageAdapter.OnFeedItemClickListener() {
            @Override
            public void onFeedItemClick(View v, int pos) {
                Intent intent = new Intent(Activity_UserDetail.this, Activity_Detail.class);
                intent.putExtra("feed_idx", mypageData.get(pos).feed_idx);
                startActivity(intent);
            }
        });
        mAdapter.setOnLikeClickListener(new MyPageAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(View v, int pos) {
                ClickHeart(mypageData.get(pos).feed_idx, user_idx, page, limit);
            }
        });
    }

    public void ClickHeart(int feed_idx, int user_idx, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_like(feed_idx, user_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                Log.d(TAG, "onResponse: " + user_idx);
                getMyPageData(user_idx, page, limit);
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    public void Follow(int my_idx, int target_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_follow(my_idx, target_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                getFollowed(my_idx, target_idx);
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {

            }
        });
    }

    public void getFollowed(int my_idx, int target_idx){
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.get_followed(my_idx, target_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                assert response.body() != null;
                Log.d(TAG, "onResponse: "+response.body().body);
                if (response.body().body.equals("0")) {
                    follwImage.setImageResource(R.drawable.plus);
                    followText.setText("팔로우");
                } else {
                    follwImage.setImageResource(R.drawable.ok);
                    followText.setText("팔로잉");
                }
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {

            }
        });

    }

}