package com.example.hellochat;

import com.example.hellochat.DTO.ChatData;
import com.example.hellochat.DTO.ChatListData;
import com.example.hellochat.DTO.MypageResult;
import com.example.hellochat.DTO.ResultData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChatApi {

    @FormUrlEncoded
    @POST("chatting.php")
    Call<ChatData> getChatting(
            @Field("my_idx") int my_idx,
            @Field("user_idx") int user_idx
    );


    @FormUrlEncoded
    @POST("chat_list.php")
    Call<ChatListData> getChatList(
            @Field("my_idx") int my_idx
    );

    @FormUrlEncoded
    @POST("get_user_name.php")
    Call<ResultData> getUserName(
            @Field("user_idx") int user_idx
    );


}
