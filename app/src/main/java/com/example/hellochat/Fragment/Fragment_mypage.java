package com.example.hellochat.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hellochat.LoginApi;
import com.example.hellochat.DTO.LoginData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_mypage extends Fragment {

    TextView name , mylang , mylang2 , mylang3 , studylang , studylang2 , studylang3 , level, level2, level3;
    Button logout;
    String TAG = this.getClass().getName();
    String idx ="";
    retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
            .baseUrl("http://3.37.204.197/hellochat/")    // baseUrl 등록
            .addConverterFactory(GsonConverterFactory.create())  // Gson 변환기 등록
            .build();



    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_mypage, container, false);
//        name = view.findViewById(R.id.name);
//        mylang= view.findViewById(R.id.mylang);    mylang2= view.findViewById(R.id.mylang2);    mylang3= view.findViewById(R.id.mylang3);
//        studylang= view.findViewById(R.id.study_lang);  studylang2= view.findViewById(R.id.study_lang2);  studylang3= view.findViewById(R.id.study_lang3);
//        level = view.findViewById(R.id.level);    level2= view.findViewById(R.id.level2);    level3 = view.findViewById(R.id.level3);
//        logout =view.findViewById(R.id.logout);


        Bundle bundle = getArguments();

        if (bundle != null) {
            idx = bundle.getString("idx");
        }else {
        }
        Log.d(TAG, "onCreateView: "+idx);
        //Retrofit 인스턴스 생성

        LoginApi service = retrofit.create(LoginApi.class);
        Call<LoginData> call = service.getState(idx);
        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(@NotNull Call<LoginData> call, @NotNull Response<LoginData> response) {
                LoginData msg = response.body();
                Log.d(TAG, "onResponse: "+msg.toString());
                name.setText(msg.name);
                mylang.setText(msg.my_language);
                studylang.setText(msg.study_language);
                level.setText(msg.level);
                mylang2.setText(msg.my_language2);
                studylang2.setText(msg.study_language2);
                level2.setText(msg.level2);
                mylang3.setText(msg.my_language3);
                studylang3.setText(msg.study_language3);
                level3.setText(msg.level3);
                if(msg.level2.equals("0")){
                    level2.setVisibility(View.GONE);
                }
                if(msg.level3.equals("0")){
                    level3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                Log.d(TAG, "onResponse:통신실패 "+t.getMessage());
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

}
//        logout.setOnClickListener(v -> {
//            Intent intent = new Intent( getActivity() , Activity_Login.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        });