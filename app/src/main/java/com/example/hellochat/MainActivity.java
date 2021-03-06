package com.example.hellochat;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.hellochat.DTO.TargetLangData;
import com.example.hellochat.Fragment.Fragment_chat;
import com.example.hellochat.Fragment.Fragment_mypage;
import com.example.hellochat.Fragment.Fragment_newsfeed;
import com.example.hellochat.Interface.LoginApi;
import com.example.hellochat.Service.ClientService;
import com.example.hellochat.Util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //죽지않는 서비스
    private Intent serviceIntent;


    BottomNavigationView bottomNavigationView;
    String TAG = this.getClass().getName();
    Fragment_chat fragment_chat;
    Fragment_mypage fragment_mypage;
    Fragment_newsfeed fragment_newsfeed;
    public static MainActivity activity = null;    //액티비티 변수 선언
    String idx;
    private static final int PERMISSION_REQUEST = 2;
    Util util = new Util();
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;
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
        idx = intent.getStringExtra("idx");
        if (idx != null && !idx.equals("")) {
            Log.d(TAG, "onCreate: "+idx);
            SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Login_data" , idx);
            editor.apply();
        }else {
            SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
            idx =  pref.getString("Login_data" , "");
        }
        setTargetLanguage(idx);


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


        //죽지않는서비스
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent1 = new Intent();
            intent1.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent1.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent1);
        }
        JSONObject jo = new JSONObject();
        try {
            jo.put("user_idx" , Integer.parseInt(idx));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (ClientService.serviceIntent==null) {
            Log.d(TAG, "onCreate: 서비스시작!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            serviceIntent = new Intent(this, ClientService.class);
            serviceIntent.putExtra("reconnection", jo.toString());
            startService(serviceIntent);
        } else {
            serviceIntent = ClientService.serviceIntent;//getInstance().getApplication();
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
        }
        requestPermissions();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: 메인액티비티 뉴인텐트");
        if(intent.getBooleanExtra("initChat" , false)){
            fragment_chat = (Fragment_chat) getSupportFragmentManager().findFragmentByTag("chat");
            fragment_chat.get_ChatList(Integer.parseInt(idx));
        }else if(intent.getBooleanExtra("initFeed" , false )){
            fragment_newsfeed = (Fragment_newsfeed) getSupportFragmentManager().findFragmentByTag("feed");
            fragment_newsfeed.getNewNotification_Count(Integer.parseInt(idx));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceIntent != null){
            Log.d(TAG, "onDestroy: 메인액티비티 없어짐");
            stopService(serviceIntent);
            serviceIntent = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Dynamic permissions are not required before Android M.
//            onPermissionsGranted();
            return;
        }

        String[] missingPermissions = getMissingPermissions();
        if (missingPermissions.length != 0) {
            requestPermissions(missingPermissions, PERMISSION_REQUEST);
        } else {
//            onPermissionsGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String[] getMissingPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new String[0];
        }

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to retrieve permissions.");
            return new String[0];
        }

        if (info.requestedPermissions == null) {
            Log.w(TAG, "No requested permissions.");
            return new String[0];
        }

        ArrayList<String> missingPermissions = new ArrayList<>();
        for (int i = 0; i < info.requestedPermissions.length; i++) {
            if ((info.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
                missingPermissions.add(info.requestedPermissions[i]);
            }
        }
        Log.d(TAG, "Missing permissions: " + missingPermissions);

        return missingPermissions.toArray(new String[missingPermissions.size()]);
    }


    public void setTargetLanguage(String idx){
        LoginApi service = RetrofitClientInstance.getRetrofitInstance().create(LoginApi.class);
        Call<TargetLangData> call = service.getTargetData(idx);
        call.enqueue(new Callback<TargetLangData>() {
            @Override
            public void onResponse(Call<TargetLangData> call, Response<TargetLangData> response) {
                TargetLangData data = response.body();
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(util.ReturnLangCode(data.targetLang));
                if(util.ReturnLangCode(data.targetLang2)!=null){
                    jsonArray.put(util.ReturnLangCode(data.targetLang2));
                    if(util.ReturnLangCode(data.targetLang3)!=null){
                        jsonArray.put(util.ReturnLangCode(data.targetLang3));
                    }
                }
                SharedPreferences pref2 = getSharedPreferences("Translator", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = pref2.edit();
                editor2.putString("targetlang", jsonArray.toString() );
                editor2.apply();
            }
            @Override
            public void onFailure(Call<TargetLangData> call, Throwable t) {
            }
        });
    }


    @Override
    public void onBackPressed() {
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            super.onBackPressed();
            toast.cancel();
        }
    }
}