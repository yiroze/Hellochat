package com.example.hellochat;

import com.example.hellochat.DTO.MypageResult;
import com.example.hellochat.DTO.ViewBoardData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MypageApi {


    @FormUrlEncoded
    @POST("mypage.php")
    Call<MypageResult> getMypage(
            @Field("user_idx") int user_idx,
            @Field("page") int page,
            @Field("limit") int limit
    );

}
