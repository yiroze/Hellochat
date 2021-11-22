package com.example.hellochat.Activity.UserPage;

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

import com.example.hellochat.Adapter.UserPage.FollowerAdapter;
import com.example.hellochat.Adapter.UserPage.FollowingAdapter;
import com.example.hellochat.DTO.UserPage.FollowData;
import com.example.hellochat.DTO.UserPage.FollowResult;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Following extends AppCompatActivity {

    ImageView back;
    TextView textView;
    RecyclerView mRecyclerView;
    FollowingAdapter mAdapter;
    ArrayList<FollowData> mData;
    FollowResult resultData;
    int page = 1, limit = 10;
    private static final String TAG = "Activity_Follower";
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        InitView();
        GetData(GetUserID() , page , limit);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(Activity_Following.this);
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
        Call<FollowResult> call = service.get_following(user_idx , page , limit);
        call.enqueue(new Callback<FollowResult>() {
            @Override
            public void onResponse(Call<FollowResult> call, Response<FollowResult> response) {
                progressBar.setVisibility(View.GONE);
                resultData = response.body();
                mData = resultData.body;
                mAdapter = new FollowingAdapter(mData);
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
    public void ItemClickListener(FollowingAdapter mAdapter){
        mAdapter.setOnItemClickListener(new FollowerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(Activity_Following.this  , Activity_UserDetail.class);
                intent.putExtra("user_idx" , mData.get(position).user_idx);
                startActivity(intent);
            }
        });
    }


    public int GetUserID(){
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int user = Integer.parseInt(pref.getString("Login_data", ""));
        return user;
    }


    @Override
    protected void onStart() {
        super.onStart();
        GetData(GetUserID() , page , limit);
    }
}