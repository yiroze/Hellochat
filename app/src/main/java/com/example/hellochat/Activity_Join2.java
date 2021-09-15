package com.example.hellochat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Multipart;

public class Activity_Join2 extends AppCompatActivity {
    private final int GALLERY = 1;
    String profile_uri;
    Spinner mylang_spinner, study_lang_spinner, level_spinner;
    Spinner mylang_spinner2, study_lang_spinner2, level_spinner2;
    Spinner mylang_spinner3, study_lang_spinner3, level_spinner3;
    EditText get_introduce;
    String[] mylang_data = {"", "Korean" , "English", "Chinese","Hindi", "French","Bengali", "Spanish", "Arabic", "Russian", "German", "Japanese", "Portuguese"};
    String[] study_lang_data = {"", "Korean" , "English", "Chinese","Hindi", "French","Bengali", "Spanish", "Arabic", "Russian", "German", "Japanese", "Portuguese"};
    String[] level_data = {"입문", "초급", "중급", "중상급", "상급", "고급"};
    String my_language = "";
    String my_language2 = "";
    String my_language3 = "";
    String study_language = "";
    String study_language2 = "";
    String study_language3 = "";
    int level, level2, level3;
    Button next;
    ImageView mylang_plus, study_lang_plus, delete_mylang2, delete_mylang3, delete_studylang2, delete_studylang3 ,qusetion_mark;
    int mylang_cnt = 0, studylang_cnt = 0;
    String TAG = this.getClass().getName();
    ImageView plus, profile;
    private File tempFile;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mylang_spinner = (Spinner) findViewById(R.id.mylang_spinner);
        mylang_spinner2 = (Spinner) findViewById(R.id.mylang_spinner2);
        mylang_spinner3 = (Spinner) findViewById(R.id.mylang_spinner3);
        study_lang_spinner = (Spinner) findViewById(R.id.study_lang_spinner);
        study_lang_spinner2 = (Spinner) findViewById(R.id.study_lang_spinner2);
        study_lang_spinner3 = (Spinner) findViewById(R.id.study_lang_spinner3);
        level_spinner = (Spinner) findViewById(R.id.level_spinner);
        level_spinner2 = (Spinner) findViewById(R.id.level_spinner2);
        level_spinner3 = (Spinner) findViewById(R.id.level_spinner3);
        get_introduce = (EditText) findViewById(R.id.introduce);
        next = (Button) findViewById(R.id.next);
        mylang_plus = (ImageView) findViewById(R.id.mylang_plus);
        study_lang_plus = (ImageView) findViewById(R.id.study_lang_plus);
        delete_mylang2 = (ImageView) findViewById(R.id.mylang_spinner2_bnt);
        delete_mylang3 = (ImageView) findViewById(R.id.mylang_spinner3_bnt);
        delete_studylang2 = (ImageView) findViewById(R.id.study_lang_bnt2);
        delete_studylang3 = (ImageView) findViewById(R.id.study_lang_bnt3);
        qusetion_mark = (ImageView) findViewById(R.id.qusetion_mark);
        plus = (ImageView) findViewById(R.id.plus);
        profile = (ImageView) findViewById(R.id.profile);
        LinearLayout mylang_layout2 = (LinearLayout) findViewById(R.id.mylang_layout2);
        LinearLayout mylang_layout3 = (LinearLayout) findViewById(R.id.mylang_layout3);
        LinearLayout study_lang_layout2 = (LinearLayout) findViewById(R.id.study_lang_layout2);
        LinearLayout study_lang_layout3 = (LinearLayout) findViewById(R.id.study_lang_layout3);

        ArrayAdapter<String> mylang_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mylang_data);
        ArrayAdapter<String> studylang_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, study_lang_data);
        ArrayAdapter<String> level_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, level_data);

        mylang_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        studylang_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        level_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        qusetion_mark.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Join2.this , level_info.class);
            startActivity(intent);
        });

        mylang_spinner.setAdapter(mylang_adapter);
        mylang_spinner2.setAdapter(mylang_adapter);
        mylang_spinner3.setAdapter(mylang_adapter);
        study_lang_spinner.setAdapter(studylang_adapter);
        study_lang_spinner2.setAdapter(studylang_adapter);
        study_lang_spinner3.setAdapter(studylang_adapter);
        level_spinner.setAdapter(level_adapter);
        level_spinner2.setAdapter(level_adapter);
        level_spinner3.setAdapter(level_adapter);
        mylang_plus.setOnClickListener(v -> {
            if (my_language != "") {
                if (mylang_cnt < 2) {
                    if (mylang_cnt == 0) {
                        mylang_cnt += 1;
                        mylang_spinner2.setSelection(0);
                        mylang_layout2.setVisibility(View.VISIBLE);
                    } else if (mylang_cnt == 1) {
                        if (my_language2 != "") {
                            mylang_cnt += 1;
                            mylang_spinner3.setSelection(0);
                            delete_mylang2.setVisibility(View.GONE);
                            mylang_layout3.setVisibility(View.VISIBLE);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                            builder.setTitle("모국어를 골라주세요");
                            builder.setPositiveButton("ok", null);
                            builder.create().show();
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                    builder.setTitle("더이상 추가할 수 없습니다");
                    builder.setPositiveButton("ok", null);
                    builder.create().show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                builder.setTitle("모국어를 골라주세요");
                builder.setPositiveButton("ok", null);
                builder.create().show();
            }

        });
        study_lang_plus.setOnClickListener(v -> {
            if (study_language != "") {
                if (studylang_cnt < 2) {
                    if (studylang_cnt == 0) {
                        studylang_cnt += 1;
                        study_lang_layout2.setVisibility(View.VISIBLE);
                    } else if (studylang_cnt == 1) {
                        if (study_language2 != "") {
                            studylang_cnt += 1;
                            delete_studylang2.setVisibility(View.GONE);
                            study_lang_layout3.setVisibility(View.VISIBLE);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                            builder.setTitle("학습언어를 골라주세요");
                            builder.setPositiveButton("ok", null);
                            builder.create().show();
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                    builder.setTitle("더이상 추가할 수 없습니다");
                    builder.setPositiveButton("ok", null);
                    builder.create().show();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                builder.setTitle("학습언어를 골라주세요");
                builder.setPositiveButton("ok", null);
                builder.create().show();
            }
        });
        delete_mylang2.setOnClickListener(v -> {
            mylang_cnt -= 1;
            mylang_layout2.setVisibility(View.GONE);
            my_language2 = "";
        });
        delete_mylang3.setOnClickListener(v -> {
            mylang_cnt -= 1;
            delete_mylang2.setVisibility(View.VISIBLE);
            mylang_layout3.setVisibility(View.GONE);
            my_language3 = "";
        });
        delete_studylang2.setOnClickListener(v -> {
            studylang_cnt -= 1;
            study_lang_layout2.setVisibility(View.GONE);
            study_language2 = "";
            level2 = 0;
        });
        delete_studylang3.setOnClickListener(v -> {
            studylang_cnt -= 1;
            delete_studylang2.setVisibility(View.VISIBLE);
            study_lang_layout3.setVisibility(View.GONE);
            study_language3 = "";
            level3 = 0;
        });

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String name = intent.getStringExtra("name");

        mylang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mylang_data[position].equals("")) {
                    if (mylang_data[position].equals(my_language2) || mylang_data[position].equals(my_language3) || mylang_data[position].equals(study_language) || mylang_data[position].equals(study_language2) || mylang_data[position].equals(study_language3)) {
                        mylang_spinner.setSelection(0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                        builder.setTitle("이미 선택된 언어입니다");
                        builder.setPositiveButton("ok", null);
                        builder.create().show();
                    } else {
                        my_language = mylang_data[position];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mylang_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mylang_data[position].equals("")) {
                    if (mylang_data[position].equals(my_language) || mylang_data[position].equals(my_language3) || mylang_data[position].equals(study_language) || mylang_data[position].equals(study_language2) || mylang_data[position].equals(study_language3)) {
                        mylang_spinner2.setSelection(0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                        builder.setTitle("이미 선택된 언어입니다");
                        builder.setPositiveButton("ok", null);
                        builder.create().show();
                    } else {
                        my_language2 = mylang_data[position];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mylang_spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mylang_data[position].equals("")) {
                    if (mylang_data[position].equals(my_language) || mylang_data[position].equals(my_language2) || mylang_data[position].equals(study_language) || mylang_data[position].equals(study_language2) || mylang_data[position].equals(study_language3)) {
                        mylang_spinner3.setSelection(0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                        builder.setTitle("이미 선택된 언어입니다");
                        builder.setPositiveButton("ok", null);
                        builder.create().show();
                    } else {
                        my_language3 = mylang_data[position];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        study_lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!study_lang_data[position].equals("")) {
                    if (study_lang_data[position].equals(my_language3) || study_lang_data[position].equals(my_language2) || study_lang_data[position].equals(my_language) || study_lang_data[position].equals(study_language2) || study_lang_data[position].equals(study_language3)) {
                        study_lang_spinner.setSelection(0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                        builder.setTitle("이미 선택된 언어입니다");
                        builder.setPositiveButton("ok", null);
                        builder.create().show();
                    } else {
                        study_language = study_lang_data[position];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        study_lang_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!study_lang_data[position].equals("")) {
                    if (study_lang_data[position].equals(my_language3) || study_lang_data[position].equals(my_language2) || study_lang_data[position].equals(my_language) || study_lang_data[position].equals(study_language) || study_lang_data[position].equals(study_language3)) {
                        study_lang_spinner2.setSelection(0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                        builder.setTitle("이미 선택된 언어입니다");
                        builder.setPositiveButton("ok", null);
                        builder.create().show();
                    } else {
                        study_language2 = study_lang_data[position];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        study_lang_spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!study_lang_data[position].equals("")) {
                    if (study_lang_data[position].equals(my_language3) || study_lang_data[position].equals(my_language2) || study_lang_data[position].equals(my_language) || study_lang_data[position].equals(study_language) || study_lang_data[position].equals(study_language2)) {
                        study_lang_spinner3.setSelection(0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
                        builder.setTitle("이미 선택된 언어입니다");
                        builder.setPositiveButton("ok", null);
                        builder.create().show();
                    } else {
                        study_language3 = study_lang_data[position];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = StringToInt_level(level_data[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        level_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level2 = StringToInt_level(level_data[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        level_spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level3 = StringToInt_level(level_data[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        next.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Join2.this);
            builder.setTitle("가입하시겠습니까?");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String introduce = get_introduce.getText().toString();
                    //Retrofit 인스턴스 생성
                    retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                            .baseUrl("http://3.37.204.197/hellochat/")    // baseUrl 등록
                            .addConverterFactory(GsonConverterFactory.create())  // Gson 변환기 등록
                            .build();
                    JoinApi service = retrofit.create(JoinApi.class);
                    Call<JoinData> call = service.getJoinData(email, password, name, my_language, my_language2, my_language3, study_language, study_language2, study_language3, level, level2, level3, introduce ,profile_uri);
                    Log.d(TAG, "onCreate: " + " 모국어" + my_language + " 학습언어" + study_language + " 레벨" + level + " 이메일" + email + " 비밀번호" + password + " 이름" + name + " 자기소개" + introduce);
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
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();

        });

        //사진업로드 받기
        plus.setOnClickListener(v -> {
            tedPermission();
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(galleryIntent, GALLERY);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK){
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        if(requestCode==GALLERY){
            photoUri = data.getData();
            Cursor cursor = null;
            try {
                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = { MediaStore.Images.Media.DATA };

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
            Log.d(TAG, "onActivityResult: "+tempFile);
            setImage();
            upload_file();
        }

    }
    private void setImage(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        profile.setImageBitmap(originalBm);
    }

    public int StringToInt_level(String level_request) {
        int level_result = 0;

        switch (level_request) {
            case "입문":
                level_result = 1;
                break;

            case "초급":
                level_result = 2;
                break;

            case "중급":
                level_result = 3;
                break;

            case "중상급":
                level_result = 4;
                break;

            case "상급":
                level_result = 5;
                break;

            case "고급":
                level_result = 6;
                break;
            default:
                break;
        }

        return level_result;
    }

    private void tedPermission(){

        PermissionListener permissionListener= new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    public void upload_file(){
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", tempFile.getName(), requestFile);
        JoinApi service = RetrofitClientInstance.getRetrofitInstance().create(JoinApi.class);
        Call<UploadResult> call = service.uploadFile(body);
        call.enqueue(new Callback<UploadResult>() {
            @Override
            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                if(response.isSuccessful()){
                    UploadResult uploadResult;
                    uploadResult = response.body();
                    assert uploadResult != null;
                    Log.d(TAG, "onResponse: "+uploadResult.toString());
                    profile_uri = uploadResult.image;
                }


            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {

            }
        });


    }

}