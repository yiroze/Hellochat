package com.example.hellochat.Activity.UserPage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hellochat.Activity.Setting.Activity_Setting;
import com.example.hellochat.Activity.Feed.Activity_Trans;
import com.example.hellochat.Activity.Feed.Activity_Detail;
import com.example.hellochat.Activity.Feed.Activity_Edit;
import com.example.hellochat.Adapter.MyPageAdapter;
import com.example.hellochat.Adapter.UserDetailAdapter;
import com.example.hellochat.DTO.MypageData;
import com.example.hellochat.DTO.MypageResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.MypageApi;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Util.GetLanguageCode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_MyDetail extends AppCompatActivity {
    String TAG = this.getClass().getName();
    int idx, myidx;
    RecyclerView mRecyclerView;
    MypageResult result_data;
    ArrayList<MypageData> mypageData;
    UserDetailAdapter mAdapter;
    int page = 1, limit = 10;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    LinearLayout modify, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_detail);
        Intent intent = getIntent();
        idx = intent.getIntExtra("user_idx", 0);
        Log.d(TAG, "onCreateView: " + idx);
        InitView();
        mypageData = new ArrayList<>();
        getMyPageData(idx, page, limit);
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
        modify.setOnClickListener(v -> {
            Intent intentSet = new Intent(Activity_MyDetail.this, Activity_Setting.class);
            startActivity(intentSet);
        });

        edit.setOnClickListener(v -> {
            Intent intentEdit = new Intent(Activity_MyDetail.this, Activity_Edit.class);
            startActivity(intentEdit);
        });
    }

    public void InitView() {
        modify = (LinearLayout) findViewById(R.id.modify);
        edit = (LinearLayout) findViewById(R.id.edit);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_user);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(Activity_MyDetail.this);
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
                Intent intent = new Intent(Activity_MyDetail.this, Activity_Detail.class);
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
        mAdapter.setOnLongClickListener(new UserDetailAdapter.OnContentLongClickListener() {
            @Override
            public void onContentLongClick(View v, int position) {
                PopupMenu popupMenu = new PopupMenu(Activity_MyDetail.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menulist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.trans:
                                Log.d(TAG, "onMenuItemClick: "+ getTargetLang());
                                Intent intentTrans = new Intent(Activity_MyDetail.this, Activity_Trans.class);
                                intentTrans.putExtra("content" , mypageData.get(position).contents);
                                intentTrans.putExtra("targetLang" , getTargetLang());
                                startActivity(intentTrans);
                                return true;

                            case R.id.tts:
                                String text = mypageData.get(position).contents;
                                new GetLanguageCode(text , Activity_MyDetail.this).start();
                                return true;

                            case R.id.copy:
                                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("text",mypageData.get(position).contents); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText( Activity_MyDetail.this, "복사되었습니다.",Toast.LENGTH_SHORT).show();
                                return true;

                        }
                        return false;
                    }
                });
                popupMenu.show();
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
    public String getTargetLang() {
        SharedPreferences pref = getSharedPreferences("Translator", MODE_PRIVATE);
        return pref.getString("targetlang", "");
    }
}