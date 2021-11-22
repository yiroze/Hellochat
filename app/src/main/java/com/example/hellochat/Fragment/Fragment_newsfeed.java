package com.example.hellochat.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.hellochat.Activity.Feed.Activity_Edit;
import com.example.hellochat.Activity.Feed.Activity_Notification;
import com.example.hellochat.Adapter.Image.PagerAdapter;
import com.example.hellochat.DTO.Feed.NotificationCount;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_newsfeed extends Fragment  {
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView bell, edit;
    TextView count;
    private static final String TAG = "Fragment_newsfeed";
    NotificationCount notificationCount;
    int myidx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myidx= Integer.parseInt(getPref(container));
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        InitView(view);

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity() , Activity_Edit.class);
            startActivity(intent);
        });

        bell.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity() , Activity_Notification.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getNewNotification_Count(myidx);
    }

    public void InitView(View view){
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout)view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        edit = (ImageView)view.findViewById(R.id.edit);
        bell = view.findViewById(R.id.alram);
        count = view.findViewById(R.id.new_feed_count);
    }

    public void getNewNotification_Count(int myidx){
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<NotificationCount> call = service.get_NotificationCount(myidx);
        call.enqueue(new Callback<NotificationCount>() {
            @Override
            public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {
                notificationCount = response.body();
                assert notificationCount != null;
                if(notificationCount.body!=0){
                    count.setText(Integer.toString(notificationCount.body));
                    count.setVisibility(View.VISIBLE);
                }else {
                    count.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<NotificationCount> call, Throwable t) {

            }
        });

    }

    public String getPref(ViewGroup container) {
        SharedPreferences pref = container.getContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }
}