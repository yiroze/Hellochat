package com.example.hellochat;

import com.example.hellochat.DTO.CheckEmail;
import com.example.hellochat.DTO.JoinData;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.UploadResult;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface JoinApi {

    @FormUrlEncoded
    @POST("email_check.php")
    Call<CheckEmail> getCheckEmail(
            @Field("email") String Email
    );

    @FormUrlEncoded
    @POST("updatePassword.php")
    Call<ResultData> updatePassword(
            @Field("email") String Email,
            @Field("password") String password
            );


    @FormUrlEncoded
    @POST("join_api.php")
    Call<JoinData> getJoinData(
            @Field("email") String email,
            @Field("password") String password,
            @Field("name") String name,
            @Field("my_language") String my_language,
            @Field("my_language2") String my_language2,
            @Field("my_language3") String my_language3,
            @Field("study_language") String study_language,
            @Field("study_language2") String study_language2,
            @Field("study_language3") String study_language3,
            @Field("level") int level,
            @Field("level2") int level2,
            @Field("level3") int level3,
            @Field("introduce") String introduce,
            @Field("profile") String profile
    );

    @Multipart
    @POST("upload_profile.php")
    Call<UploadResult> uploadFile(@Part MultipartBody.Part uploaded_file);

}
