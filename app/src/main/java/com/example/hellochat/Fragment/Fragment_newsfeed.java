package com.example.hellochat.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.hellochat.Activity.Activity_Edit;
import com.example.hellochat.Activity.Activity_modify;
import com.example.hellochat.Adapter.NewsfeedAdapter;
import com.example.hellochat.Adapter.PagerAdapter;
import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.DTO.ViewBoardData;
import com.example.hellochat.DTO.ViewData;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_newsfeed extends Fragment  {

    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView bell, edit;
    private static final String TAG = "Fragment_newsfeed";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        InitView(view);

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity() , Activity_Edit.class);
            startActivity(intent);
        });

        return view;
    }

    public void InitView(View view){
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout)view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        edit = (ImageView)view.findViewById(R.id.edit);
    }



}