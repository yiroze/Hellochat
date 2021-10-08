package com.example.hellochat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hellochat.Fragment.Fragment_feed_following;
import com.example.hellochat.Fragment.Fragment_feed_new;
import com.example.hellochat.Fragment.Fragment_newsfeed;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> items;
    private ArrayList<String> title= new ArrayList<String>();

    public PagerAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm);
        items = new ArrayList<Fragment>();
        items.add(new Fragment_feed_new());
        items.add(new Fragment_feed_following());

        title.add("피드");
        title.add("팔로잉");
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
