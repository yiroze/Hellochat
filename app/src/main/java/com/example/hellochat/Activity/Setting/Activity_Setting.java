package com.example.hellochat.Activity.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.hellochat.Activity.Login.Activity_Login;
import com.example.hellochat.DTO.UserPage.ModifyResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.Feed.UploadResult;
import com.example.hellochat.Interface.JoinApi;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Util.GpsTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Setting extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Activity_Setting";
    final static int GALLERY = 3000, Request_name = 1, Request_lang = 2, Request_introduce = 3, Request_place_of_birth = 4, Request_hobby = 5;

    private GoogleMap mMap;
    TextView name, mylang, mylang2, mylang3, study_lang, study_lang2, study_lang3, introduce, PlaceOfBirth, hobby, location;
    ImageView profile, back, no_location_image, logout;
    ConstraintLayout name_layout, lang_layout, introduce_layout, PlaceOfBirth_layout, hobby_layout, location_layout;
    LinearLayout mylang2_layout, mylang3_layout, study_lang2_layout, study_lang3_layout;
    ModifyResult mData;
    ProgressBar studylang_level, studylang_level2, studylang_level3;
    private ActivityResultLauncher<Intent> SetName;
    private ActivityResultLauncher<Intent> SetLang;
    private ActivityResultLauncher<Intent> SetIntroduce;
    private ActivityResultLauncher<Intent> SetPOB;
    private ActivityResultLauncher<Intent> SetHobby;
    private ActivityResultLauncher<Intent> SetProfile;
    GpsTracker gpsTracker;
    Geocoder geocoder;
    MarkerOptions markerOptions;
    LatLng NOW;
    private File tempFile;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        markerOptions = new MarkerOptions();
        InitView();
        onActivityResult();
        getMyData(GetUserID());
        SetOnClick();
        location_layout.setOnClickListener(v -> {
            String items[] = {"위치정보 업데이트", "위치정보 숨기기"};
            AlertDialog.Builder dia = new AlertDialog.Builder(this);
            dia.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        UpdateLocation();
                    } else if (which == 1) {
                        DeleteLocation();
                        mMap.clear();
                    }
                }
            });
            AlertDialog alertDialog = dia.create();
            alertDialog.show();
        });
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    void SetOnClick() {
        name_layout.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Setting.this, Activity_SettingName.class);
            intent.putExtra("name", name.getText());
            SetName.launch(intent);
        });
        lang_layout.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Setting.this, Activity_SettingLanguage.class);
            intent.putExtra("mylang", mylang.getText());
            intent.putExtra("mylang2", mylang2.getText());
            intent.putExtra("mylang3", mylang3.getText());
            intent.putExtra("studylang", study_lang.getText());
            intent.putExtra("studylang2", study_lang2.getText());
            intent.putExtra("studylang3", study_lang3.getText());
            intent.putExtra("level", studylang_level.getProgress());
            intent.putExtra("level2", studylang_level2.getProgress());
            intent.putExtra("level3", studylang_level3.getProgress());
            SetLang.launch(intent);
        });
        introduce_layout.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Setting.this, Activity_SettingIntroduce.class);
            intent.putExtra("introduce", introduce.getText());
            SetIntroduce.launch(intent);
        });
        PlaceOfBirth_layout.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Setting.this, Activity_SettingPlaceOfBirth.class);
            intent.putExtra("place", PlaceOfBirth.getText());
            SetPOB.launch(intent);
        });
        hobby_layout.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Setting.this, Activity_SettingHobby.class);
            intent.putExtra("hobby", hobby.getText());
            SetHobby.launch(intent);
        });

        profile.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            SetProfile.launch(galleryIntent);
        });
        back.setOnClickListener(v -> {
            finish();
        });
        logout.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Setting.this, Activity_Login.class);
            startActivity(intent);
            finish();
        });
    }

    void InitView() {
        name = findViewById(R.id.name);
        mylang = findViewById(R.id.mylang);
        mylang2 = findViewById(R.id.mylang2);
        mylang3 = findViewById(R.id.mylang3);
        study_lang = findViewById(R.id.study_lang);
        study_lang2 = findViewById(R.id.study_lang2);
        study_lang3 = findViewById(R.id.study_lang3);
        mylang2_layout = findViewById(R.id.mylang2_layout);
        mylang3_layout = findViewById(R.id.mylang3_layout);
        study_lang2_layout = findViewById(R.id.study_lang2_layout);
        study_lang3_layout = findViewById(R.id.study_lang3_layout);
        studylang_level = findViewById(R.id.studylang_level);
        studylang_level2 = findViewById(R.id.studylang_level2);
        studylang_level3 = findViewById(R.id.studylang_level3);
        profile = findViewById(R.id.profile);
        introduce = findViewById(R.id.introduce);
        PlaceOfBirth = findViewById(R.id.PlaceOfBirth);
        hobby = findViewById(R.id.hobby);
        location = findViewById(R.id.location);
        back = findViewById(R.id.back);
        name_layout = findViewById(R.id.name_layout);
        lang_layout = findViewById(R.id.lang_layout);
        introduce_layout = findViewById(R.id.introduce_layout);
        PlaceOfBirth_layout = findViewById(R.id.PlaceOfBirth_layout);
        hobby_layout = findViewById(R.id.hobby_layout);
        location_layout = findViewById(R.id.location_layout);
        no_location_image = findViewById(R.id.no_location_image);
        logout = findViewById(R.id.logout);
    }

    public void getMyData(int user_idx) {
        UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
        Call<ModifyResult> call = service.getMyData(user_idx);
        call.enqueue(new Callback<ModifyResult>() {
            @Override
            public void onResponse(Call<ModifyResult> call, Response<ModifyResult> response) {
                Log.d(TAG, "onResponse: " + response.body());
                mData = response.body();
                name.setText(mData.name);
                introduce.setText(mData.introduce);
                if (mData.place_of_birth != null) {
                    PlaceOfBirth.setText(mData.place_of_birth);
                }
                if (mData.hobby != null) {
                    hobby.setText(mData.hobby);
                }
                if (mData.longitude != 0 && mData.latitude != 0) {
                    //역지오코딩으로 도시이름 받아와서 텍스트에 넣음
                    UpdateLocation();
                }
                mylang.setText(mData.mylang);
                if (mData.mylang2 != null) {
                    mylang2.setText(mData.mylang2);
                    mylang2_layout.setVisibility(View.VISIBLE);
                } else {
                    mylang2.setText("");
                    mylang2_layout.setVisibility(View.GONE);
                }
                if (mData.mylang3 != null) {
                    mylang3.setText(mData.mylang3);
                    mylang3_layout.setVisibility(View.VISIBLE);
                } else {
                    mylang3.setText("");
                    mylang3_layout.setVisibility(View.GONE);
                }
                study_lang.setText(mData.studylang);
                studylang_level.setProgress(mData.studylang_level);
                if (mData.studylang2 != null) {
                    study_lang2.setText(mData.studylang2);
                    studylang_level2.setProgress(mData.studylang_level2);
                    study_lang2_layout.setVisibility(View.VISIBLE);
                } else {
                    study_lang2.setText("");
                    studylang_level2.setProgress(0);
                    study_lang2_layout.setVisibility(View.GONE);
                }
                if (mData.studylang3 != null) {
                    study_lang3.setText(mData.studylang3);
                    studylang_level3.setProgress(mData.studylang_level3);
                    study_lang3_layout.setVisibility(View.VISIBLE);
                } else {
                    study_lang3.setText("");
                    studylang_level3.setProgress(0);
                    study_lang3_layout.setVisibility(View.GONE);
                }
                if (!mData.profile.equals("") && mData.profile != null) {
                    Log.d(TAG, "onResponse: onResponse: onResponse: onResponse: onResponse: onResponse: onResponse: onResponse: onResponse: ");
                    Glide.with(profile)
                            .load("http://3.37.204.197/hellochat/" + mData.profile)
                            .into(profile);
                }
            }

            @Override
            public void onFailure(Call<ModifyResult> call, Throwable t) {
            }
        });
    }

    public int GetUserID() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int user = Integer.parseInt(pref.getString("Login_data", ""));
        return user;
    }

    void onActivityResult() {
        SetName = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            getMyData(GetUserID());
                        }
                    }
                });
        SetLang = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Log.d(TAG, "onActivityResult: setlang");
                            getMyData(GetUserID());
                        }
                    }
                }
        );
        SetIntroduce = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        getMyData(GetUserID());
                    }
                }
        );
        SetPOB = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        getMyData(GetUserID());
                    }
                }
        );
        SetHobby = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        getMyData(GetUserID());
                    }
                }
        );
        SetProfile = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getData() != null) {
                            photoUri = result.getData().getData();
                            Cursor cursor = null;
                            try {
                                /*
                                 *  Uri 스키마를
                                 *  content:/// 에서 file:/// 로  변경한다.
                                 */
                                String[] proj = {MediaStore.Images.Media.DATA};

                                assert photoUri != null;
                                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                                assert cursor != null;
                                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                                cursor.moveToFirst();

                                tempFile = new File(cursor.getString(column_index));
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }
                            Log.d(TAG, "onActivityResult: " + tempFile);
                            upload_file(tempFile);
                            getMyData(GetUserID());
                        }
                    }

                });

    }

    void UpdateLocation() {
        gpsTracker = new GpsTracker(Activity_Setting.this);
        int i = 0;
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도
        if (latitude != 0 && longitude != 0) {
            UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
            Call<ResultData> call = service.modify_location(GetUserID(), latitude, longitude);
            call.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    Log.d(TAG, "onResponse: " + response.body().body);
                    mMap.clear();
                    NOW = new LatLng(latitude, longitude);
                    markerOptions.position(NOW);
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NOW, 4));
                    geocoder = new Geocoder(Activity_Setting.this);
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(latitude, longitude, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list != null) {
                        if (list.size() == 0) {
                        } else {
                            location.setText(new StringBuilder().append(list.get(0).getAdminArea()).append(" , ").append(list.get(0).getCountryName()).toString());
                            Log.d(TAG, "onClick: " + list.get(1).toString());
                        }
                    }
                    no_location_image.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {
                }
            });
        }
    }

    void DeleteLocation() {
        gpsTracker = new GpsTracker(Activity_Setting.this);
        int i = 0;
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도
        if (latitude != 0 && longitude != 0) {
            UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
            Call<ResultData> call = service.modify_location(GetUserID(), 0, 0);
            call.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    mMap.clear();
                    location.setText("미설정");
                    no_location_image.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {
                }
            });
        }
    }

    public void upload_file(File tempFile) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", tempFile.getName(), requestFile);
        JoinApi service = RetrofitClientInstance.getRetrofitInstance().create(JoinApi.class);
        Call<UploadResult> call = service.uploadFile(body);
        call.enqueue(new Callback<UploadResult>() {
            @Override
            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                if (response.isSuccessful()) {
                    UploadResult uploadResult;
                    uploadResult = response.body();
                    assert uploadResult != null;
                    Log.d(TAG, "onResponse: " + uploadResult.toString());
                    UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
                    Call<ResultData> call2 = service.modify_profile(GetUserID(), uploadResult.image);
                    call2.enqueue(new Callback<ResultData>() {
                        @Override
                        public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                            getMyData(GetUserID());
                        }

                        @Override
                        public void onFailure(Call<ResultData> call, Throwable t) {
                        }
                    });
                }


            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {
            }
        });
    }
}