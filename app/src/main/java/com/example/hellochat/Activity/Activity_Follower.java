package com.example.hellochat.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hellochat.Adapter.FollowerAdapter;
import com.example.hellochat.DTO.FollowData;
import com.example.hellochat.DTO.FollowResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Follower extends AppCompatActivity {

    ImageView back;
    TextView textView;
    RecyclerView mRecyclerView;
    FollowerAdapter mAdapter;
    ArrayList<FollowData> mData;
    FollowResult resultData;
    int page = 1, limit = 10;
    private static final String TAG = "Activity_Follower";
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        InitView();
        GetData(GetUserID() , page , limit);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(Activity_Follower.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    GetData(GetUserID() , page , limit);
                }
            }
        });

    }


    public void InitView(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        back = findViewById(R.id.back);
        textView = findViewById(R.id.text);
        mRecyclerView = findViewById(R.id.recycler);
        nestedScrollView = findViewById(R.id.scroll_view);
        progressBar = findViewById(R.id.progress_bar);
    }

    public void GetData(int user_idx, int page , int limit ){
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<FollowResult> call = service.get_follower(user_idx , page , limit);
        call.enqueue(new Callback<FollowResult>() {
            @Override
            public void onResponse(Call<FollowResult> call, Response<FollowResult> response) {
                progressBar.setVisibility(View.GONE);
                resultData = response.body();
                mData = resultData.body;
                mAdapter = new FollowerAdapter(mData);
                ItemClickListener(mAdapter);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "onResponse: " + mData);

            }

            @Override
            public void onFailure(Call<FollowResult> call, Throwable t) {
            }
        });
    }

    public int GetUserID(){
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int user = Integer.parseInt(pref.getString("Login_data", ""));
        return user;
    }

    public void ItemClickListener(FollowerAdapter mAdapter){
        mAdapter.setOnItemClickListener(new FollowerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(Activity_Follower.this  , Activity_UserDetail.class);
                intent.putExtra("user_idx" , mData.get(position).user_idx);
                startActivity(intent);
            }
        });
        mAdapter.setOnFollowClick(new FollowerAdapter.OnFollowClickListener() {
            @Override
            public void onFollowClick(View v, int position) {
                Follow(GetUserID() , mData.get(position).user_idx);
                GetData(GetUserID() , page , limit);
            }
        });
    }

    public void Follow(int my_idx, int target_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_follow(my_idx, target_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                GetData(GetUserID() , page , limit);
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetData(GetUserID() , page , limit);
    }
}