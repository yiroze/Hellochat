package com.example.hellochat.Activity.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellochat.Activity.Login.Activity_Join2;
import com.example.hellochat.Adapter.Setting.SelectLangAdapter;
import com.example.hellochat.DTO.Feed.LanguageData;
import com.example.hellochat.R;

import java.util.ArrayList;

public class Activity_SelectLanguage extends AppCompatActivity {

    ArrayList<LanguageData> mList;
    SelectLangAdapter mAdapter;
    RecyclerView mRecycler;
    ImageView back;
    int count, type;
    String selected1, selected2, selected3, selected4, selected5;
    String ClassName;
    private ActivityResultLauncher<Intent> addlang;
    final static int TYPE_MYLANG = 1, TYPE_STUDYLANG = 2;
    private static final String TAG = "Activity_SelectLanguage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent getIntent = getIntent();
        count = getIntent.getIntExtra("count", 0);
        type = getIntent.getIntExtra("type", 0);
        back = findViewById(R.id.back);
        selected1 = getIntent.getStringExtra("selected1");
        selected2 = getIntent.getStringExtra("selected2");
        selected3 = getIntent.getStringExtra("selected3");
        selected4 = getIntent.getStringExtra("selected4");
        selected5 = getIntent.getStringExtra("selected5");
        ClassName = getIntent.getStringExtra("class");
        Log.d(TAG, "onCreate:selected1 " + selected1);
        Log.d(TAG, "onCreate:selected2 " + selected2);
        Log.d(TAG, "onCreate:selected3 " + selected3);
        Log.d(TAG, "onCreate:selected4 " + selected4);
        Log.d(TAG, "onCreate:selected5 " + selected5);
        mRecycler = findViewById(R.id.lang_recycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mList = new ArrayList<>();
        mList.add(new LanguageData("Arabic", "عربي"));
        mList.add(new LanguageData("Bengali", "বাংলা"));
        mList.add(new LanguageData("Chinese", "中文"));
        mList.add(new LanguageData("English", "English"));
        mList.add(new LanguageData("French", "français"));
        mList.add(new LanguageData("German", "Deutsch"));
        mList.add(new LanguageData("Hindi", "हिंदी"));
        mList.add(new LanguageData("Japanese", "日本語"));
        mList.add(new LanguageData("Korean", "한국어"));
        mList.add(new LanguageData("Russian", "русский"));
        mList.add(new LanguageData("Spanish", "Español"));
        mList.add(new LanguageData("Portuguese", "português"));
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).Language_en.equals(selected1)) {
                Log.d(TAG, "remove: " + mList.get(i).Language_en);
                mList.remove(i);
            }
        }
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).Language_en.equals(selected2)) {
                Log.d(TAG, "remove: " + mList.get(i).Language_en);
                mList.remove(i);
            }
        }
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).Language_en.equals(selected3)) {
                Log.d(TAG, "remove: " + mList.get(i).Language_en);
                mList.remove(i);
            }
        }
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).Language_en.equals(selected4)) {
                Log.d(TAG, "remove: " + mList.get(i).Language_en);
                mList.remove(i);
            }
        }
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).Language_en.equals(selected5)) {
                Log.d(TAG, "remove: " + mList.get(i).Language_en);
                mList.remove(i);
            }
        }
        Log.d(TAG, "onCreate: " + mList.toString());
        mAdapter = new SelectLangAdapter(mList);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((v, position) -> {
            if (type == TYPE_MYLANG) {
                Intent intent;
                if (ClassName.equals(Activity_SettingLanguage.class.getSimpleName())) {
                    intent = new Intent(Activity_SelectLanguage.this, Activity_SettingLanguage.class);
                } else {
                    intent = new Intent(Activity_SelectLanguage.this, Activity_Join2.class);
                }
                intent.putExtra("language_en", mList.get(position).Language_en);
                intent.putExtra("language_native", mList.get(position).Language_native);
                intent.putExtra("count", count);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Intent intent = new Intent(Activity_SelectLanguage.this, Activity_SelectLanguageLevel.class);
                intent.putExtra("language_en", mList.get(position).Language_en);
                intent.putExtra("language_native", mList.get(position).Language_native);
                addlang.launch(intent);
            }
        });
        mAdapter.notifyDataSetChanged();
        onActivityResult();
        back.setOnClickListener(v -> {
            finish();
        });
    }


    void onActivityResult() {
        addlang = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent getIntent = result.getData();
                            String language_en = getIntent.getStringExtra("language_en");
                            String language_native = getIntent.getStringExtra("language_native");
                            int level = getIntent.getIntExtra("level", 0);
                            Intent intent;
                            if (ClassName.equals(Activity_SettingLanguage.class.getSimpleName())) {
                                intent = new Intent(Activity_SelectLanguage.this, Activity_SettingLanguage.class);
                            } else {
                                intent = new Intent(Activity_SelectLanguage.this, Activity_Join2.class);
                            }
                            intent.putExtra("language_en", language_en);
                            intent.putExtra("language_native", language_native);
                            intent.putExtra("level", level);
                            intent.putExtra("count", count);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
    }
}