package com.example.hellochat.Activity.Feed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hellochat.Activity.UserPage.Activity_UserDetail;
import com.example.hellochat.Adapter.NotificationAdapter;
import com.example.hellochat.DTO.NotificationData;
import com.example.hellochat.DTO.NotificationResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Activity_Notification extends AppCompatActivity {
    RecyclerView recyclerView;
    NotificationAdapter mAdapter;
    NotificationResult mResult;
    ArrayList<NotificationData> mData;
    ImageView back;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int page = 1, limit = 10;
    int my_idx;
    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        InitView();
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mData= new ArrayList<>();
        my_idx = Integer.parseInt(getPref());
        getNotification(my_idx, page, limit);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getNotification(my_idx, page, limit);
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getNotification(my_idx, page, limit);
                refreshLayout.setRefreshing(false);
            }
        });
    }

    void InitView(){
        refreshLayout = findViewById(R.id.swipe);
        nestedScrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recycler);
    }
    public void getNotification(int idx, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<NotificationResult> call = service.get_Notification(idx, page, limit);
        call.enqueue(new Callback<NotificationResult>() {
            @Override
            public void onResponse(Call<NotificationResult> call, Response<NotificationResult> response) {
                progressBar.setVisibility(View.GONE);
                mResult = response.body();
                mData = mResult.body;
                Log.d(TAG, "onResponse: "+mData.toString());
                mAdapter = new NotificationAdapter(mData);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                setChecked(idx);
                mAdapter.setOnNotification((v, pos) -> {
                    Intent intent = new Intent(Activity_Notification.this , Activity_Detail.class);
                    intent.putExtra("feed_idx" , mData.get(pos).feed_idx);
                    startActivity(intent);
                });
                mAdapter.setOnProfileClick((v, pos) -> {
                    Intent intent = new Intent(Activity_Notification.this , Activity_UserDetail.class);
                    intent.putExtra("user_idx" , mData.get(pos).user_idx);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(Call<NotificationResult> call, Throwable t) {
                Log.d(TAG, "onResponse: "+t.toString());
            }
        });
    }

    public void setChecked(int my_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.set_check_notification(my_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
            }
        });

    }
    public String getPref() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }
}