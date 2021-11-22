package com.example.hellochat.Adapter.UserPage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.hellochat.Fragment.Fragment_Profile;
import com.example.hellochat.Fragment.Fragment_UserFeed;
import com.example.hellochat.Fragment.Fragment_feed_following;
import com.example.hellochat.Fragment.Fragment_feed_new;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> items;
    private ArrayList<String> title= new ArrayList<String>();
    int user;

    public UserViewPagerAdapter(@NonNull @NotNull FragmentManager fm , int idx) {
        super(fm);
        user = idx;
        items = new ArrayList<Fragment>();
        items.add(new Fragment_Profile(user));
        items.add(new Fragment_UserFeed(user));

        title.add("프로필");
        title.add("피드");
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
