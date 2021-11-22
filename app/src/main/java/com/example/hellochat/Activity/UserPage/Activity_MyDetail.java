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
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hellochat.Activity.Chatting.Activity_Chatting;
import com.example.hellochat.Activity.Feed.Activity_Detail;
import com.example.hellochat.Activity.Feed.Activity_Edit;
import com.example.hellochat.Activity.Feed.Activity_Trans;
import com.example.hellochat.Activity.Setting.Activity_Setting;
import com.example.hellochat.Adapter.UserPage.MyPageAdapter;
import com.example.hellochat.Adapter.UserPage.UserDetailAdapter;
import com.example.hellochat.Adapter.UserPage.UserViewPagerAdapter;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.UserPage.ModifyResult;
import com.example.hellochat.DTO.UserPage.MypageData;
import com.example.hellochat.DTO.UserPage.MypageResult;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Util.GetLanguageCode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_MyDetail extends AppCompatActivity implements OnMapReadyCallback {
    String TAG = this.getClass().getName();
    int idx, myidx;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView profile, follwImage, no_location_image, back;
    TextView name, mylang, mylang2, mylang3, study_lang, study_lang2, study_lang3, followText;
    LinearLayout mylang2_layout, mylang3_layout, study_lang2_layout, study_lang3_layout, modify, edit;
    ProgressBar studylang_level, studylang_level2, studylang_level3;
    String user_name;
    private GoogleMap mMap;
    ModifyResult mData;
    LatLng NOW;
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_detail);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        Intent intent = getIntent();
        idx = intent.getIntExtra("user_idx", 0);
        InitView();
        myidx = GetUserID();
        getMyData(idx);
        modify.setOnClickListener(v -> {
            Intent intentSet = new Intent(Activity_MyDetail.this, Activity_Setting.class);
            startActivity(intentSet);
        });

        edit.setOnClickListener(v -> {
            Intent intentEdit = new Intent(Activity_MyDetail.this, Activity_Edit.class);
            startActivity(intentEdit);
        });
        back.setOnClickListener(v -> {
            finish();
        });
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
        modify = (LinearLayout) findViewById(R.id.modify);
        edit = (LinearLayout) findViewById(R.id.edit);
        follwImage = (ImageView) findViewById(R.id.follow_image);
        followText = (TextView) findViewById(R.id.follow_text);
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

    @Override
    protected void onStart() {
        super.onStart();
        getMyData(idx);
    }
}