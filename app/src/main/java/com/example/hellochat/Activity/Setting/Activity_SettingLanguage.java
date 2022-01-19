package com.example.hellochat.Activity.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.TargetLangData;
import com.example.hellochat.Interface.LoginApi;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Util.Util;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_SettingLanguage extends AppCompatActivity {

    ConstraintLayout mylang_layout, mylang_layout2, mylang_layout3, study_layout, study_layout2, study_layout3;
    ProgressBar study_lang_level, study_lang_level2, study_lang_level3;
    ImageView add_mylang, add_studylang, delete_mylang2, delete_mylang3, delete_studylang2, delete_studylang3, back;
    TextView mylang_name, mylang_name2, mylang_name3, studylang_name, studylang_name2, studylang_name3, ok, mylang, mylang2, mylang3, studylang, studylang2, studylang3;
    String mylang_data, mylang2_data, mylang3_data, studylang_data, studylang2_data, studylang3_data;
    int level1, level2, level3;
    Util util = new Util();
    int mylang_cnt, studylang_cnt;
    final static int TYPE_MYLANG = 1, TYPE_STUDYLANG = 2;
    private ActivityResultLauncher<Intent> add_my_lang;
    private ActivityResultLauncher<Intent> add_study_lang;
    private static final String TAG = "Activity_SettingLanguag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_language);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent getIntent = getIntent();
        InitView(getIntent);
        onActivityResult();
        setClick();
    }


    void InitView(Intent intent) {
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
        back.setOnClickListener(v -> {
            finish();
        });
        ok = findViewById(R.id.ok);
        mylang_name = findViewById(R.id.mylang_name);
        mylang_name2 = findViewById(R.id.mylang_name2);
        mylang_name3 = findViewById(R.id.mylang_name3);
        studylang_name = findViewById(R.id.study_lang_name);
        studylang_name2 = findViewById(R.id.study_lang_name2);
        studylang_name3 = findViewById(R.id.study_lang_name3);
        mylang_data = intent.getStringExtra("mylang");
        mylang2_data = intent.getStringExtra("mylang2");
        mylang3_data = intent.getStringExtra("mylang3");
        studylang_data = intent.getStringExtra("studylang");
        studylang2_data = intent.getStringExtra("studylang2");
        studylang3_data = intent.getStringExtra("studylang3");
        if (!mylang_data.equals("")) {
            mylang_cnt++;
        }
        if (!mylang2_data.equals("")) {
            mylang_cnt++;
        }
        if (!mylang3_data.equals("")) {
            mylang_cnt++;
        }
        if (!studylang_data.equals("")) {
            studylang_cnt++;
        }
        if (!studylang2_data.equals("")) {
            studylang_cnt++;
        }
        if (!studylang3_data.equals("")) {
            studylang_cnt++;
        }
        level1 = intent.getIntExtra("level", 0);
        level2 = intent.getIntExtra("level2", 0);
        level3 = intent.getIntExtra("level3", 0);
        Log.d(TAG, "mylang_cnt:" + mylang_cnt + " " + " studylang_cnt " + studylang_cnt);
        Log.d(TAG, " mylang_data: " + mylang_data +
                " mylang2_data " + mylang2_data +
                " mylang3_data " + mylang3_data +
                " studylang_data " + studylang_data +
                " studylang2_data " + studylang2_data +
                " studylang3_data " + studylang3_data +
                " level1 " + level1 +
                " level2 " + level2 +
                " level3 " + level3);

        mylang.setText(mylang_data);
        mylang_name.setText(util.ReturnLanguageName(mylang_data));
        study_lang_level.setProgress(level1);
        studylang_name.setText(util.ReturnLanguageName(studylang_data));
        studylang.setText(studylang_data);
        if (!mylang2_data.equals("")) {
            mylang2.setText(mylang2_data);
            mylang_name2.setText(util.ReturnLanguageName(mylang2_data));
            mylang_layout2.setVisibility(View.VISIBLE);
            if (!mylang3_data.equals("")) {
                mylang3.setText(mylang3_data);
                mylang_name3.setText(util.ReturnLanguageName(mylang3_data));
                delete_mylang2.setVisibility(View.GONE);
                mylang_layout3.setVisibility(View.VISIBLE);
            }
        }
        if (!studylang2_data.equals("")) {
            studylang2.setText(studylang2_data);
            studylang_name2.setText(util.ReturnLanguageName(studylang2_data));
            study_layout2.setVisibility(View.VISIBLE);
            study_lang_level2.setProgress(level2);
            if (!studylang3_data.equals("")) {
                studylang3.setText(studylang3_data);
                studylang_name3.setText(util.ReturnLanguageName(studylang3_data));
                delete_studylang2.setVisibility(View.GONE);
                study_layout3.setVisibility(View.VISIBLE);
                study_lang_level3.setProgress(level3);
            }
        }
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
            if (studylang_cnt == 1) {
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
            if (mylang_cnt == 1) {
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
        ok.setOnClickListener(v -> {
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
            UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
            Call<ResultData> call = service.modify_language(GetUserID(),
                    mylang_name.getText().toString(), mylang_name2.getText().toString(), mylang_name3.getText().toString(),
                    studylang_name.getText().toString(), studylang_name2.getText().toString(), studylang_name3.getText().toString(),
                    study_lang_level.getProgress(), study_lang_level2.getProgress(), study_lang_level3.getProgress());
            call.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    Log.d(TAG, "onResponse: " + response.body().body);
                    Intent intent = new Intent(Activity_SettingLanguage.this, Activity_Setting.class);
                    setResult(RESULT_OK, intent);
                    setTargetLanguage(mylang_name.getText().toString(),mylang_name2.getText().toString(),mylang_name3.getText().toString());
                    finish();
                }

                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_SettingLanguage.this);
                    builder.setTitle("서버에 문제가 생겼습니다.\n다시 시도해 주세요");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                }
            });
        });
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
                            String language_native = getIntent.getStringExtra("language_native");
                            int level = getIntent.getIntExtra("level", 0);
                            int count = getIntent.getIntExtra("count", 0);
                            if (count == 0) {
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
                            String language_native = getIntent.getStringExtra("language_native");
                            int level = getIntent.getIntExtra("level", 0);
                            int count = getIntent.getIntExtra("count", 0);
                            if (count == 0) {
                                studylang.setText(util.ReturnLanguageName2alpha(language_en));
                                studylang_name.setText(language_en);
                                study_lang_level.setProgress(level);
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

    public int GetUserID() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        int user = Integer.parseInt(pref.getString("Login_data", ""));
        return user;
    }

    public void setTargetLanguage(String mylang ,String mylang2 ,String mylang3 ) {
        SharedPreferences pref2 = getSharedPreferences("Translator", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pref2.edit();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(util.ReturnLangCode(mylang));
        if(util.ReturnLangCode(mylang2)!=null){
            jsonArray.put(util.ReturnLangCode(mylang2));
            if(util.ReturnLangCode(mylang3)!=null){
                jsonArray.put(util.ReturnLangCode(mylang3));
            }
        }
        editor2.putString("targetlang", jsonArray.toString() );
        editor2.apply();
    }

}
