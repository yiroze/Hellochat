package com.example.hellochat.Activity.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellochat.Adapter.Setting.SelectLangLevelAdapter;
import com.example.hellochat.DTO.Feed.LevelData;
import com.example.hellochat.R;

import java.util.ArrayList;

public class Activity_SelectLanguageLevel extends AppCompatActivity {

    ArrayList<LevelData> mList;
    SelectLangLevelAdapter mAdapter;
    RecyclerView mRecycler;
    ImageView back;
    String language_en , language_native;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language_level);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });
        mRecycler = findViewById(R.id.level_recycler);
        Intent getIntent= getIntent();
        language_en = getIntent.getStringExtra("language_en");
        language_native = getIntent.getStringExtra("language_native");
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mList = new ArrayList<>();
        mList.add(new LevelData("초보" , "학습을 시작한지 얼마 안 됐거나 ,자기소개를 하고 간단한 질문에 대답할 수 있어요" ,1));
        mList.add(new LevelData("기초" , "간단한 용어를 사용해 다양한 것들을 묘사하고 간단한 표현을 이해할 수 있어요" ,2));
        mList.add(new LevelData("중급" , "여행할 때 외국어를 구사할 수 있어요. 취미와 일 , 가족에 관해 대화를 나눌 수 있어요" ,3));
        mList.add(new LevelData("중상급" , "복잡한 주제의 주제를 이해할 수 있어요. 또 막힘없이 원어민과 대화를 나눌 수 있어요" ,4));
        mList.add(new LevelData("고급" , "사교, 학술 또는 전문적인 상황에서 외국어 능력을 구사할 수 있으며 , 복잡한 대화를 따라갈 수 있어요" ,5));
        mList.add(new LevelData("고급이상" , "듣거나 읽는 내용을 거의 이해할 수 있으며 , 복잡한 주제에 관해 대화를 나눌때 제 생각을 잘 표현할 수 있어요" ,6));
        mAdapter = new SelectLangLevelAdapter(mList);
        mRecycler.setLayoutManager(mLinearLayoutManager);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(this , Activity_SelectLanguage.class);
            intent.putExtra("language_en" , language_en);
            intent.putExtra("language_native" ,language_native);
            intent.putExtra("level" , mList.get(position).Level);
            setResult(RESULT_OK , intent);
            finish();
        });
        mAdapter.notifyDataSetChanged();
    }
}