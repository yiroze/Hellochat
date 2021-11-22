package com.example.hellochat.Activity.UserPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hellochat.Activity.Chatting.Activity_Chatting;
import com.example.hellochat.Adapter.UserPage.UserViewPagerAdapter;
import com.example.hellochat.DTO.UserPage.ModifyResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Activity_UserDetail extends AppCompatActivity implements OnMapReadyCallback {
    int idx, myidx;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView profile, follwImage, no_location_image , back;
    TextView name, mylang, mylang2, mylang3, study_lang, study_lang2, study_lang3, followText;
    LinearLayout mylang2_layout, mylang3_layout, study_lang2_layout, study_lang3_layout, message, follow;
    ProgressBar studylang_level, studylang_level2, studylang_level3;
    String user_name;
    private GoogleMap mMap;
    ModifyResult mData;
    LatLng NOW;
    MarkerOptions markerOptions;

    public int getIdx() {
        return idx;
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        Intent intent = getIntent();
        idx = intent.getIntExtra("user_idx", 0);
        InitView();
        myidx = GetUserID();
        getFollowed(myidx, idx);
        follow.setOnClickListener(v -> {
            Follow(myidx, idx);
        });
        message.setOnClickListener(v -> {
            Intent intent_msg = new Intent(Activity_UserDetail.this, Activity_Chatting.class);
            intent_msg.putExtra("user_idx", idx);
            intent_msg.putExtra("user_name", user_name);
            startActivity(intent_msg);
        });
        back.setOnClickListener(v -> {
            finish();
        });
        getMyData(idx);
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void InitView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        UserViewPagerAdapter pagerAdapter = new UserViewPagerAdapter(getSupportFragmentManager() , idx);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        follow = (LinearLayout) findViewById(R.id.follow);
        follwImage = (ImageView) findViewById(R.id.follow_image);
        followText = (TextView) findViewById(R.id.follow_text);
        message = findViewById(R.id.message);
        name = findViewById(R.id.name);
        mylang = findViewById(R.id.mylang);
        mylang2 = findViewById(R.id.mylang2);
        mylang3 = findViewById(R.id.mylang3);
        study_lang = findViewById(R.id.study_lang);
        study_lang2 = findViewById(R.id.study_lang2);
        study_lang3 = findViewById(R.id.study_lang3);
        mylang2_layout = findViewById(R.id.mylang2_layout);
        mylang3_layout = findViewById(R.id.mylang3_layout);
        study_lang2_layout = findViewById(R.id.study_lang2_layout);
        study_lang3_layout = findViewById(R.id.study_lang3_layout);
        studylang_level = findViewById(R.id.studylang_level);
        studylang_level2 = findViewById(R.id.studylang_level2);
        studylang_level3 = findViewById(R.id.studylang_level3);
        profile = findViewById(R.id.profile);
        no_location_image = findViewById(R.id.no_location_image);
        back = findViewById(R.id.back);

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

    public void getFollowed(int my_idx, int target_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.get_followed(my_idx, target_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                assert response.body() != null;
                Log.d(TAG, "onResponse: " + response.body().body);
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

    public void getMyData(int user_idx) {
        UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
        Call<ModifyResult> call = service.getMyData(user_idx);
        call.enqueue(new Callback<ModifyResult>() {
            @Override
            public void onResponse(Call<ModifyResult> call, Response<ModifyResult> response) {
                Log.d(TAG, "onResponse: " + response.body());
                mData = response.body();
                name.setText(mData.name);
                if (mData.longitude != 0 && mData.latitude != 0) {
                    UpdateLocation(mData.latitude, mData.longitude);
                }
                mylang.setText(mData.mylang);
                if (mData.mylang2 != null) {
                    mylang2.setText(mData.mylang2);
                    mylang2_layout.setVisibility(View.VISIBLE);
                } else {
                    mylang2.setText("");
                    mylang2_layout.setVisibility(View.GONE);
                }
                if (mData.mylang3 != null) {
                    mylang3.setText(mData.mylang3);
                    mylang3_layout.setVisibility(View.VISIBLE);
                } else {
                    mylang3.setText("");
                    mylang3_layout.setVisibility(View.GONE);
                }
                study_lang.setText(mData.studylang);
                studylang_level.setProgress(mData.studylang_level);
                if (mData.studylang2 != null) {
                    study_lang2.setText(mData.studylang2);
                    studylang_level2.setProgress(mData.studylang_level2);
                    study_lang2_layout.setVisibility(View.VISIBLE);
                } else {
                    study_lang2.setText("");
                    studylang_level2.setProgress(0);
                    study_lang2_layout.setVisibility(View.GONE);
                }
                if (mData.studylang3 != null) {
                    study_lang3.setText(mData.studylang3);
                    studylang_level3.setProgress(mData.studylang_level3);
                    study_lang3_layout.setVisibility(View.VISIBLE);
                } else {
                    study_lang3.setText("");
                    studylang_level3.setProgress(0);
                    study_lang3_layout.setVisibility(View.GONE);
                }
                if ( mData.profile != null) {
                    Glide.with(profile)
                            .load("http://3.37.204.197/hellochat/" + mData.profile)
                            .into(profile);
                }
            }

            @Override
            public void onFailure(Call<ModifyResult> call, Throwable t) {
            }
        });
    }

    void UpdateLocation(double latitude, double longitude) {
        if (latitude != 0 && longitude != 0) {
            NOW = new LatLng(latitude, longitude);
            markerOptions = new MarkerOptions();
            markerOptions.position(NOW);
            Log.d(TAG, "UpdateLocation: "+NOW.latitude);
            Log.d(TAG, "latitude: "+latitude);
            Log.d(TAG, "longitude: "+longitude);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NOW, 4));
            no_location_image.setVisibility(View.GONE);
        } else {
            no_location_image.setVisibility(View.VISIBLE);
        }
    }

    public int GetUserID() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int user = Integer.parseInt(pref.getString("Login_data", ""));
        return user;
    }

}