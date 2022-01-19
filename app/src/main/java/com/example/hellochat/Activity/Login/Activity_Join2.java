package com.example.hellochat.Activity.Login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.example.hellochat.Activity.Setting.Activity_SelectLanguage;
import com.example.hellochat.DTO.Feed.UploadResult;
import com.example.hellochat.DTO.Login.JoinData;
import com.example.hellochat.Interface.JoinApi;
import com.example.hellochat.MainActivity;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Util.Util;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Join2 extends AppCompatActivity {

    private static final String TAG = "Activity_Join2";
    ConstraintLayout mylang_layout, mylang_layout2, mylang_layout3, study_layout, study_layout2, study_layout3;
    ProgressBar study_lang_level, study_lang_level2, study_lang_level3;
    ImageView add_mylang, add_studylang, delete_mylang2, delete_mylang3, delete_studylang2, delete_studylang3, back, plus, profile;
    TextView mylang_name, mylang_name2, mylang_name3, studylang_name, studylang_name2, studylang_name3, mylang, mylang2, mylang3, studylang, studylang2, studylang3;
    Button join;
    EditText introduce;
    Util util = new Util();
    LinearLayout mylang_level_layout, studylang_level_layout;
    private ActivityResultLauncher<Intent> add_my_lang;
    private ActivityResultLauncher<Intent> add_study_lang;
    final static int TYPE_MYLANG = 1, TYPE_STUDYLANG = 2;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    String photoUri;
    int mylang_cnt, studylang_cnt;
    String email, password, name;
    Boolean isPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent getIntent = getIntent();
        email = getIntent.getStringExtra("email");
        password = getIntent.getStringExtra("password");
        name = getIntent.getStringExtra("name");
        InitView();
        tedPermission();
        //사진업로드 받기
        plus.setOnClickListener(v -> {
            String items[] = {"갤러리", "카메라", "기본이미지"};
            AlertDialog.Builder dia = new AlertDialog.Builder(this);
            dia.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_FROM_ALBUM);
                        dialog.dismiss();
                    } else if (which == 1) {
                        takePhoto();
                    } else if (which == 2) {
                        tempFile = null;
                        profile.setImageResource(R.drawable.no_profile);
                    }
                }
            });
            AlertDialog alertDialog = dia.create();
            alertDialog.show();

        });
        setClick();
        onActivityResult();

    }

    void InitView() {
        mylang = findViewById(R.id.mylang);
        mylang2 = findViewById(R.id.mylang2);
        mylang3 = findViewById(R.id.mylang3);
        studylang = findViewById(R.id.study_lang);
        studylang2 = findViewById(R.id.study_lang2);
        studylang3 = findViewById(R.id.study_lang3);
        mylang_layout = findViewById(R.id.mylang_layout);
        mylang_layout2 = findViewById(R.id.mylang_layout2);
        mylang_layout3 = findViewById(R.id.mylang_layout3);
        study_layout = findViewById(R.id.study_lang_layout);
        study_layout2 = findViewById(R.id.study_lang_layout2);
        study_layout3 = findViewById(R.id.study_lang_layout3);
        study_lang_level = findViewById(R.id.study_lang_level);
        study_lang_level2 = findViewById(R.id.study_lang_level2);
        study_lang_level3 = findViewById(R.id.study_lang_level3);
        add_mylang = findViewById(R.id.add_mylang);
        add_studylang = findViewById(R.id.add_studylang);
        delete_mylang2 = findViewById(R.id.delete_mylang2);
        delete_mylang3 = findViewById(R.id.delete_mylang3);
        delete_studylang2 = findViewById(R.id.delete_studylang2);
        delete_studylang3 = findViewById(R.id.delete_studylang3);
        back = findViewById(R.id.back);
        join = findViewById(R.id.join);
        mylang_name = findViewById(R.id.mylang_name);
        mylang_name2 = findViewById(R.id.mylang_name2);
        mylang_name3 = findViewById(R.id.mylang_name3);
        studylang_name = findViewById(R.id.study_lang_name);
        studylang_name2 = findViewById(R.id.study_lang_name2);
        studylang_name3 = findViewById(R.id.study_lang_name3);
        introduce = findViewById(R.id.introduce);
        plus = findViewById(R.id.plus);
        profile = findViewById(R.id.profile);
        mylang_level_layout = findViewById(R.id.mylang_level_layout);
        studylang_level_layout = findViewById(R.id.study_lang_level_layout);
    }

    void setClick() {
        add_mylang.setOnClickListener(v -> {
            Log.d(TAG, "setClick: " + mylang_cnt);
            Intent intent = new Intent(this, Activity_SelectLanguage.class);
            intent.putExtra("count", mylang_cnt);
            intent.putExtra("type", TYPE_MYLANG);
            intent.putExtra("class", this.getClass().getSimpleName());
            for (int i = 0; i < mylang_cnt; i++) {
                if (i == 0) {
                    intent.putExtra("selected1", mylang_name.getText());
                } else if (i == 1) {
                    intent.putExtra("selected2", mylang_name2.getText());
                    Log.d(TAG, "setClick: ");
                }
            }
            for (int i = 0; i < studylang_cnt; i++) {
                if (i == 0) {
                    intent.putExtra("selected3", studylang_name.getText());
                } else if (i == 1) {
                    intent.putExtra("selected4", studylang_name2.getText());
                } else if (i == 2) {
                    intent.putExtra("selected5", studylang_name3.getText());

                }
            }
            add_my_lang.launch(intent);
        });
        add_studylang.setOnClickListener(v -> {
            Intent intent = new Intent(this, Activity_SelectLanguage.class);
            intent.putExtra("count", studylang_cnt);
            intent.putExtra("type", TYPE_STUDYLANG);
            intent.putExtra("class", this.getClass().getSimpleName());

            for (int i = 0; i < mylang_cnt; i++) {
                if (i == 0) {
                    intent.putExtra("selected1", mylang_name.getText());
                } else if (i == 1) {
                    intent.putExtra("selected2", mylang_name2.getText());
                } else if (i == 2) {
                    intent.putExtra("selected3", mylang_name3.getText());
                }
            }
            for (int i = 0; i < studylang_cnt; i++) {
                if (i == 0) {
                    intent.putExtra("selected4", studylang_name.getText());
                } else if (i == 1) {
                    intent.putExtra("selected5", studylang_name2.getText());
                }
            }
            add_study_lang.launch(intent);
        });
        study_layout.setOnClickListener(v -> {
            if (studylang_cnt == 0 || studylang_cnt == 1) {
                Intent intent = new Intent(this, Activity_SelectLanguage.class);
                intent.putExtra("count", 0);
                intent.putExtra("type", TYPE_STUDYLANG);
                intent.putExtra("class", this.getClass().getSimpleName());

                for (int i = 0; i < mylang_cnt; i++) {
                    if (i == 0) {
                        intent.putExtra("selected1", mylang_name.getText());
                    } else if (i == 1) {
                        intent.putExtra("selected2", mylang_name2.getText());
                        Log.d(TAG, "setClick: ");
                    } else if (i == 2) {
                        intent.putExtra("selected3", mylang_name3.getText());

                    }
                }
                add_study_lang.launch(intent);
            }
        });
        mylang_layout.setOnClickListener(v -> {
            if (mylang_cnt == 0 || mylang_cnt == 1) {
                Intent intent = new Intent(this, Activity_SelectLanguage.class);
                intent.putExtra("count", 0);
                intent.putExtra("type", TYPE_MYLANG);
                intent.putExtra("class", this.getClass().getSimpleName());

                for (int i = 0; i < studylang_cnt; i++) {
                    if (i == 0) {
                        intent.putExtra("selected1", studylang_name.getText());
                    } else if (i == 1) {
                        intent.putExtra("selected2", studylang_name2.getText());
                    } else if (i == 2) {
                        intent.putExtra("selected3", studylang_name3.getText());

                    }
                }
                add_my_lang.launch(intent);
            }
        });

        delete_mylang2.setOnClickListener(v -> {
            mylang_cnt--;
            mylang_layout2.setVisibility(View.GONE);
        });
        delete_mylang3.setOnClickListener(v -> {
            mylang_cnt--;
            mylang_layout3.setVisibility(View.GONE);
            delete_mylang2.setVisibility(View.VISIBLE);
            add_mylang.setVisibility(View.VISIBLE);
        });
        delete_studylang2.setOnClickListener(v -> {
            studylang_cnt--;
            study_layout2.setVisibility(View.GONE);
        });
        delete_studylang3.setOnClickListener(v -> {
            studylang_cnt--;
            study_layout3.setVisibility(View.GONE);
            delete_studylang2.setVisibility(View.VISIBLE);
            add_studylang.setVisibility(View.VISIBLE);
        });
        join.setOnClickListener(v -> {
            if (mylang_cnt != 0 && studylang_cnt != 0) {
                if (mylang_cnt == 1) {
                    mylang_name2.setText("");
                    mylang_name3.setText("");
                } else if (mylang_cnt == 2) {
                    mylang_name3.setText("");
                }
                if (studylang_cnt == 1) {
                    studylang_name2.setText("");
                    study_lang_level2.setProgress(0);
                    studylang_name3.setText("");
                    study_lang_level3.setProgress(0);
                } else if (studylang_cnt == 2) {
                    studylang_name3.setText("");
                    study_lang_level3.setProgress(0);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                builder.setTitle("가입하시겠습니까?");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (tempFile != null) {
                            upload_file();
                        } else {
                            Join();
                        }
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("모국어와 학습언어를 모두 선택해주세요");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            Uri photoUri = data.getData();

            cropImage(photoUri);


        } else if (requestCode == PICK_FROM_CAMERA) {
            Uri photoUri = Uri.fromFile(tempFile);

            cropImage(photoUri);


        } else if (requestCode == Crop.REQUEST_CROP) {
            setImage();
        }

    }

    private void setImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        profile.setImageBitmap(originalBm);
    }


    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                isPermission = true;

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                isPermission = false;
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.hellochat.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    private void cropImage(Uri photoUri) {

        Log.d(TAG, "tempFile : " + tempFile);

        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if (tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
    }

    void onActivityResult() {
        add_my_lang = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent getIntent = result.getData();
                            String language_en = getIntent.getStringExtra("language_en");
                            int count = getIntent.getIntExtra("count", 0);
                            if (count == 0) {
                                if (mylang_cnt < 1) {
                                    mylang_cnt++;
                                    add_mylang.setVisibility(View.VISIBLE);
                                }
                                mylang_level_layout.setVisibility(View.VISIBLE);
                                mylang_name.setVisibility(View.VISIBLE);
                                mylang.setText(util.ReturnLanguageName2alpha(language_en));
                                mylang_name.setText(language_en);
                            } else if (count == 1) {
                                mylang_cnt++;
                                mylang_layout2.setVisibility(View.VISIBLE);
                                mylang2.setText(util.ReturnLanguageName2alpha(language_en));
                                mylang_name2.setText(language_en);
                            } else if (count == 2) {
                                mylang_cnt++;
                                mylang_layout3.setVisibility(View.VISIBLE);
                                mylang3.setText(util.ReturnLanguageName2alpha(language_en));
                                mylang_name3.setText(language_en);
                                add_mylang.setVisibility(View.GONE);
                                delete_mylang2.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        add_study_lang = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent getIntent = result.getData();
                            String language_en = getIntent.getStringExtra("language_en");
                            int level = getIntent.getIntExtra("level", 0);
                            int count = getIntent.getIntExtra("count", 0);
                            if (count == 0) {
                                if (studylang_cnt < 1) {
                                    studylang_cnt++;
                                    add_studylang.setVisibility(View.VISIBLE);
                                }
                                studylang.setText(util.ReturnLanguageName2alpha(language_en));
                                studylang_name.setText(language_en);
                                study_lang_level.setProgress(level);
                                studylang_level_layout.setVisibility(View.VISIBLE);
                                studylang_name.setVisibility(View.VISIBLE);
                            } else if (count == 1) {
                                studylang_cnt++;
                                study_layout2.setVisibility(View.VISIBLE);
                                studylang2.setText(util.ReturnLanguageName2alpha(language_en));
                                studylang_name2.setText(language_en);
                                study_lang_level2.setProgress(level);

                            } else if (count == 2) {
                                studylang_cnt++;
                                study_layout3.setVisibility(View.VISIBLE);
                                studylang3.setText(util.ReturnLanguageName2alpha(language_en));
                                studylang_name3.setText(language_en);
                                add_studylang.setVisibility(View.GONE);
                                delete_studylang2.setVisibility(View.GONE);
                                study_lang_level3.setProgress(level);

                            }
                        }
                    }
                }
        );
    }

    public void upload_file() {
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
                    photoUri = uploadResult.image;
                    Join();
                }
            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {

            }
        });
    }

    void Join() {
        //Retrofit 인스턴스 생성
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("http://3.37.204.197/hellochat/")    // baseUrl 등록
                .addConverterFactory(GsonConverterFactory.create())  // Gson 변환기 등록
                .build();
        JoinApi service = retrofit.create(JoinApi.class);
        Call<JoinData> call = service.getJoinData(
                email, password, name,
                mylang_name.getText().toString(), mylang_name2.getText().toString(), mylang_name3.getText().toString(),
                studylang_name.getText().toString(), studylang_name2.getText().toString(), studylang_name3.getText().toString(),
                study_lang_level.getProgress(), study_lang_level2.getProgress(), study_lang_level3.getProgress(),
                introduce.getText().toString(), photoUri);
        call.enqueue(new Callback<JoinData>() {
            @Override
            public void onResponse(Call<JoinData> call, Response<JoinData> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: 통신성공");
                    JoinData result = response.body();
                    Log.d(TAG, "onResponse: " + result.msg);
                    Log.d(TAG, "onResponse: " + result.toString());
                    if (result.msg.equals("true")) {
                        //쿼리가 성공 메인화면으로 인텐트
                        Log.d(TAG, "onResponse: 통과");
                        Intent intent = new Intent(Activity_Join2.this, MainActivity.class);
                        intent.putExtra("idx", result.idx);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        //쿼리에 문제가 생김
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                        builder.setTitle("서버에 문제가 생겼습니다");
                        builder.setPositiveButton("ok", null);
                        builder.create().show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JoinData> call, Throwable t) {
                Log.d(TAG, "onFailure: 통신실패");
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                builder.setTitle("서버에 문제가 생겼습니다");
                builder.setPositiveButton("ok", null);
                builder.create().show();
            }
        });
    }


}