package com.example.hellochat;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApi {

    @FormUrlEncoded
    @POST("login_api.php")
    Call<LoginData> getLoginData(
            @Field("email") String email,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST("state_api.php")
    Call<LoginData> getState(
            @Field("idx") String idx
    );






}
