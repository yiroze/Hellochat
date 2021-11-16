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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hellochat.Activity.Chatting.Activity_Chatting;
import com.example.hellochat.Activity.Feed.Activity_Trans;
import com.example.hellochat.Activity.Feed.Activity_Detail;
import com.example.hellochat.Adapter.MyPageAdapter;
import com.example.hellochat.Adapter.UserDetailAdapter;
import com.example.hellochat.Service.ClientService;
import com.example.hellochat.DTO.MypageData;
import com.example.hellochat.DTO.MypageResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.MypageApi;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Util.GetLanguageCode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_UserDetail extends AppCompatActivity implements OnMapReadyCallback {
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                ClickHeart(mypageData.get(pos).feed_idx, user_idx, page, limit, mypageData.get(pos).user_idx);
            }
        });
        mAdapter.setOnLongClickListener(new UserDetailAdapter.OnContentLongClickListener() {
            @Override
            public void onContentLongClick(View v, int position) {
                PopupMenu popupMenu = new PopupMenu(Activity_UserDetail.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menulist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.trans:
                                Log.d(TAG, "onMenuItemClick: "+ getTargetLang());
                                Intent intentTrans = new Intent(Activity_UserDetail.this, Activity_Trans.class);
                                intentTrans.putExtra("content" , mypageData.get(position).contents);
                                intentTrans.putExtra("targetLang" , getTargetLang());
                                startActivity(intentTrans);
                                return true;

                            case R.id.tts:
                                String text = mypageData.get(position).contents;
                                new GetLanguageCode(text , Activity_UserDetail.this).start();
                                return true;

                            case R.id.copy:
                                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("text",mypageData.get(position).contents); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText( Activity_UserDetail.this, "복사되었습니다.",Toast.LENGTH_SHORT).show();
                                return true;

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void ClickHeart(int feed_idx, int user_idx ,int page, int limit , int accept_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_like(feed_idx, user_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                getMyPageData(user_idx, page, limit);
                Intent intent = new Intent(Activity_UserDetail.this, ClientService.class);
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
    public String getTargetLang() {
        SharedPreferences pref = getSharedPreferences("Translator", MODE_PRIVATE);
        return pref.getString("targetlang", "");
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

    }
}