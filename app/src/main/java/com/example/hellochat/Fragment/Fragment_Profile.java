package com.example.hellochat.Fragment;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.hellochat.DTO.UserPage.ModifyResult;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class Fragment_Profile extends Fragment {
    private static final String TAG = "Fragment_Profile";
    TextView introduce, PlaceOfBirth, hobby, location ;
    ModifyResult mData;
    Geocoder geocoder;
    int user_idx;
    public Fragment_Profile(int user){
        user_idx = user;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        InitView(view);
        getMyData(user_idx);

        return view;
    }

    public void getMyData(int user_idx) {
        UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
        Call<ModifyResult> call = service.getMyData(user_idx);
        call.enqueue(new Callback<ModifyResult>() {
            @Override
            public void onResponse(Call<ModifyResult> call, Response<ModifyResult> response) {
                mData = response.body();
                Log.d(TAG, "onResponse: "+mData);
                if(mData.introduce != null){
                    introduce.setText(mData.introduce);
                }else {
                    introduce.setText("미설정");
                }
                if (mData.place_of_birth != null) {
                    PlaceOfBirth.setText(mData.place_of_birth);
                }else {
                    PlaceOfBirth.setText("미설정");
                }
                if (mData.hobby != null) {
                    hobby.setText(mData.hobby);
                }else {
                    hobby.setText("미설정");
                }
                if (mData.longitude != 0 && mData.latitude != 0) {
                    //역지오코딩으로 도시이름 받아와서 텍스트에 넣음
                    geocoder = new Geocoder(getActivity());
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(mData.latitude, mData.longitude, 10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list != null) {
                        if (list.size() == 0) {
                        } else {
                            location.setText(new StringBuilder().append(list.get(0).getAdminArea()).append(" , ").append(list.get(0).getCountryName()).toString());
                        }
                    }
                }else {
                    location.setText("미설정");
                }
            }

            @Override
            public void onFailure(Call<ModifyResult> call, Throwable t) {
            }
        });
    }

    public int getPref() {
        SharedPreferences pref = getActivity().getSharedPreferences("LOGIN", MODE_PRIVATE);
        return Integer.parseInt(pref.getString("Login_data", ""));
    }

    void InitView(View view){
        introduce = view.findViewById(R.id.introduce);
        PlaceOfBirth = view.findViewById(R.id.PlaceOfBirth);
        hobby = view.findViewById(R.id.hobby);
        location = view.findViewById(R.id.location);
    }
}