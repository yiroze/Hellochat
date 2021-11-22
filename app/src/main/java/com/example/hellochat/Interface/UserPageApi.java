package com.example.hellochat.Interface;

import com.example.hellochat.DTO.UserPage.ModifyResult;
import com.example.hellochat.DTO.UserPage.MypageResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.UserPage.UserPageResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserPageApi {

    @FormUrlEncoded
    @POST("mypage.php")
    Call<MypageResult> getMypage(
            @Field("user_idx") int user_idx,
            @Field("page") int page,
            @Field("limit") int limit
    );

    @FormUrlEncoded
    @POST("get_my_data.php")
    Call<ModifyResult> getMyData(
            @Field("user_idx") int user_idx
    );

    @FormUrlEncoded
    @POST("modify_language.php")
    Call<ResultData> modify_language(
            @Field("user_idx") int user_idx,
            @Field("my_lang") String my_lang,
            @Field("my_lang2") String my_lang2,
            @Field("my_lang3") String my_lang3,
            @Field("study_lang") String study_lang,
            @Field("study_lang2") String study_lang2,
            @Field("study_lang3") String study_lang3,
            @Field("level") int level,
            @Field("level2") int level2,
            @Field("level3") int level3
    );

    @FormUrlEncoded
    @POST("modify_name.php")
    Call<ResultData> modify_name(
            @Field("user_idx") int user_idx,
            @Field("name") String name
    );

    @FormUrlEncoded
    @POST("modify_introduce.php")
    Call<ResultData> modify_introduce(
            @Field("user_idx") int user_idx,
            @Field("introduce") String introduce
    );

    @FormUrlEncoded
    @POST("modify_location.php")
    Call<ResultData> modify_location(
            @Field("user_idx") int user_idx,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude
    );

    @FormUrlEncoded
    @POST("modify_place_of_birth.php")
    Call<ResultData> modify_pob(
            @Field("user_idx") int user_idx,
            @Field("place") String place
    );

    @FormUrlEncoded
    @POST("modify_hobby.php")
    Call<ResultData> modify_hobby(
            @Field("user_idx") int user_idx,
            @Field("hobby") String hobby
    );

    @FormUrlEncoded
    @POST("modify_profile.php")
    Call<ResultData> modify_profile(
            @Field("user_idx") int user_idx,
            @Field("profile") String profile
    );


    @FormUrlEncoded
    @POST("get_user_page.php")
    Call<MypageResult> get_user_page(
            @Field("user_idx") int user_idx,
            @Field("page") int page,
            @Field("limit") int limit
    );
}
