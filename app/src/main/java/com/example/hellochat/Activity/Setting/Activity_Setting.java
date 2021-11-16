package com.example.hellochat.Activity.Setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hellochat.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class Activity_Setting extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Activity_Setting";

    private GoogleMap mMap;
    TextView name , mylang ,mylang2 ,mylang3 ,study_lang ,study_lang2 ,study_lang3 , introduce , PlaceOfBirth, hobby ,location;
    ImageView profile , back;
    ConstraintLayout name_layout, lang_layout, introduce_layout, PlaceOfBirth_layout, hobby_layout, location_layout;
    LinearLayout mylang2_layout,mylang3_layout, study_lang2_layout ,study_lang3_layout ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        InitView();
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng SEOUL = new LatLng(37.56 , 126.97);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL , 4));
    }

    void InitView(){
        name = findViewById(R.id.name);
        mylang = findViewById(R.id.mylang);
        mylang2 = findViewById(R.id.mylang2);
        mylang3 = findViewById(R.id.mylang3);
        study_lang = findViewById(R.id.study_lang);
        study_lang2 = findViewById(R.id.study_lang2);
        study_lang3 = findViewById(R.id.study_lang3);
        introduce = findViewById(R.id.introduce);
        PlaceOfBirth = findViewById(R.id.PlaceOfBirth);
        hobby = findViewById(R.id.hobby);
        location = findViewById(R.id.location);
        profile = findViewById(R.id.profile);
        back = findViewById(R.id.back);
        name_layout = findViewById(R.id.name_layout);
        lang_layout = findViewById(R.id.lang_layout);
        introduce_layout = findViewById(R.id.introduce_layout);
        PlaceOfBirth_layout = findViewById(R.id.PlaceOfBirth_layout);
        hobby_layout = findViewById(R.id.hobby_layout);
        location_layout = findViewById(R.id.location_layout);
        mylang2_layout = findViewById(R.id.mylang2_layout);
        mylang3_layout = findViewById(R.id.mylang3_layout);
        study_lang2_layout = findViewById(R.id.study_lang2_layout);
        study_lang3_layout = findViewById(R.id.study_lang3_layout);
    }
}