package com.example.hellochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.hellochat.Fragment.Fragment_chat;
import com.example.hellochat.Fragment.Fragment_mypage;
import com.example.hellochat.Fragment.Fragment_newsfeed;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    String TAG = this.getClass().getName();
    Fragment_chat fragment_chat;
    Fragment_mypage fragment_mypage;
    Fragment_newsfeed fragment_newsfeed;
    public static MainActivity activity = null;    //액티비티 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        fragment_chat = new Fragment_chat();
        fragment_newsfeed = new Fragment_newsfeed();
        fragment_mypage = new Fragment_mypage();
        Intent intent = getIntent();
        String idx = intent.getStringExtra("idx");
        Log.d(TAG, "onCreate: "+idx);
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Login_data" , idx);
        editor.apply();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_layout ,fragment_chat , "chat").commitAllowingStateLoss();

        Bundle bundle = new Bundle(1);
        bundle.putString("idx" , idx);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.i(TAG, "바텀 네비게이션 클릭");

              FragmentManager fragmentManager = getSupportFragmentManager();
            switch (item.getItemId()){
                case R.id.chat:
                    Log.i(TAG, "chat: 들어옴");
                    fragment_chat.setArguments(bundle);
                    if(fragmentManager.findFragmentByTag("chat") != null){
                        //프래그먼트가 존재하면 보여주기만 하기
                        Log.d(TAG, "onNavigationItemSelected: chat not null");
                        fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                    }else {
                        //존재하지않는다면 프래그먼트를 매니저에 추가한다.
                        Log.d(TAG, "onNavigationItemSelected: chat null");
                        fragmentManager.beginTransaction().add(R.id.main_layout , fragment_chat ,"chat").commit();
                    }
                    if(fragmentManager.findFragmentByTag("feed")!=null){
                        //다른 프래그먼트가 보이면 가린다.
                        Log.d(TAG, "onNavigationItemSelected: hidefeed");
                        fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("feed"))).commit();
                    }
                    if(fragmentManager.findFragmentByTag("mypage")!=null){
                        //다른 프래그먼트가 보이면 가린다.
                        Log.d(TAG, "onNavigationItemSelected: hide mypage");
                        fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("mypage"))).commit();
                    }
                return true;

            }
              switch (item.getItemId()){
                  case R.id.newsfeed:
                      Log.i(TAG, "newsfeed: 들어옴");
                      fragment_newsfeed.setArguments(bundle);
                      if(fragmentManager.findFragmentByTag("feed") != null){
                          //프래그먼트가 존재하면 보여주기만 하기
                          Log.d(TAG, "onNavigationItemSelected: feed not null");
                          fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("feed"))).commit();
                      }else {
                          //존재하지않는다면 프래그먼트를 매니저에 추가한다.
                          Log.d(TAG, "onNavigationItemSelected: feed null");
                          fragmentManager.beginTransaction().add(R.id.main_layout , fragment_newsfeed ,"feed").commit();
                      }
                      if(fragmentManager.findFragmentByTag("chat")!=null){
                          //다른 프래그먼트가 보이면 가린다.
                          Log.d(TAG, "onNavigationItemSelected: hide chat");
                          fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                      }
                      if(fragmentManager.findFragmentByTag("mypage")!=null){
                          //다른 프래그먼트가 보이면 가린다.
                          Log.d(TAG, "onNavigationItemSelected: hide mypage");
                          fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("mypage"))).commit();
                      }
                      return true;

              }
              switch (item.getItemId()){
                  case R.id.mypage:
                      Log.i(TAG, "mypage: 들어옴");
                      fragment_mypage.setArguments(bundle);
                      if(fragmentManager.findFragmentByTag("mypage") != null){
                          //프래그먼트가 존재하면 보여주기만 하기
                          fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag("mypage"))).commit();
                      }else {
                          //존재하지않는다면 프래그먼트를 매니저에 추가한다.
                          fragmentManager.beginTransaction().add(R.id.main_layout , fragment_mypage ,"mypage").commit();
                      }
                      if(fragmentManager.findFragmentByTag("chat")!=null){
                          //다른 프래그먼트가 보이면 가린다.
                          fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("chat"))).commit();
                      }
                      if(fragmentManager.findFragmentByTag("feed")!=null){
                          //다른 프래그먼트가 보이면 가린다.
                          fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("feed"))).commit();
                      }
                      return true;
              }
            return true;
          }
        });


    }
}