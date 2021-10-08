package com.example.hellochat.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hellochat.Adapter.PagerAdapter;
import com.example.hellochat.R;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

public class Fragment_chat extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);



        return view;
    }

}
