package com.example.hellochat.Interface;

import com.example.hellochat.DTO.Chatting.ChatData;
import com.example.hellochat.DTO.Chatting.ChatListData;
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
            @Field("user_idx") int user_idx,
            @Field("page") int page,
            @Field("limit") int limit

    );
    @FormUrlEncoded
    @POST("chatting2.php")
    Call<ChatData> getChatting2(
            @Field("my_idx") int my_idx,
            @Field("user_idx") int user_idx,
            @Field("page") int page,
            @Field("limit") int limit

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
