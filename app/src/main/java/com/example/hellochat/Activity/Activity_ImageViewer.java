package com.example.hellochat.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hellochat.Adapter.Image.ImageViewerAdapter;
import com.example.hellochat.R;

import java.text.MessageFormat;
import java.util.ArrayList;

public class Activity_ImageViewer extends AppCompatActivity {
    private ArrayList<String> imageList;
    String imageData;
    int position;
    TextView info;
    ImageViewerAdapter mAdapter;
    private static final String TAG = "Activity_ImageViewer";
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        info = findViewById(R.id.position);
        back = findViewById(R.id.back);
        imageList = new ArrayList<String>();
        Intent intent = getIntent();
        imageData = intent.getStringExtra("image");
        position = intent.getIntExtra("position", 0);
        Log.d(TAG, "onCreate: " + position);
        String[] a =imageData.replace("[","").replace("]" , "").replace(" " , "").split(",");
        for (int i = 0; i < a.length; i++){
            imageList.add(a[i].toString());
        }
        mAdapter = new ImageViewerAdapter(this , imageList);
        PagerListener listener = new PagerListener();
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(listener);
        viewPager.setCurrentItem(position);
        info.setText(MessageFormat.format("{0}/{1}", position+1, mAdapter.getCount()));
        back.setOnClickListener(v -> {
            finish();
        });

    }


    class PagerListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            info.setText(MessageFormat.format("{0}/{1}", position+1, mAdapter.getCount()));
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}