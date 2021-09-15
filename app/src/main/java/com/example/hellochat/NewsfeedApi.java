package com.example.hellochat;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NewsfeedApi {


    @FormUrlEncoded
    @POST("view_board_api.php")
    Call<ViewBoardData> getViewBoard(
            @Field("idx") int idx,
            @Field("page") int page,
            @Field("limit") int limit
    );

    @FormUrlEncoded
    @POST("edit_api.php")
    Call<EditData> getEditData(
            @Field("idx") String idx,
            @Field("content") String content
    );


    @FormUrlEncoded
    @POST("deletepost_api.php")
    Call<ResultData> delete_post(
            @Field("feed_idx") int feed_idx
    );

    @FormUrlEncoded
    @POST("delete_comment.php")
    Call<ResultData> delete_comment(
            @Field("comment_idx") int comment_idx
    );

    @FormUrlEncoded
    @POST("delete_reply.php")
    Call<ResultData> delete_reply(
            @Field("comment_idx") int comment_idx
    );

    @FormUrlEncoded
    @POST("modifypost_api.php")
    Call<ResultData> modify_post(
            @Field("feed_idx") String feed_idx,
            @Field("contents") String content

    );


    @FormUrlEncoded
    @POST("get_content.php")
    Call<ResultData> get_content(
            @Field("feed_idx") String feed_idx
    );

    @FormUrlEncoded
    @POST("detail_api.php")
    Call<DetailResult> get_detail(
            @Field("feed_idx") int feed_idx,
            @Field("user_idx") int user_idx,
            @Field("page") int page,
            @Field("limit") int limit

    );


    @FormUrlEncoded
    @POST("set_comment.php")
    Call<ResultData> set_comment(
            @Field("feednum") int feed_idx,
            @Field("user_idx") int user_idx,
            @Field("contents") String contents,
            @Field("parent") int parent
    );

    @FormUrlEncoded
    @POST("click_like.php")
    Call<ResultData> click_like(
            @Field("feed_idx") int feed_idx,
            @Field("user_idx") int user_idx
    );


    @FormUrlEncoded
    @POST("update_comment.php")
    Call<ResultData> update_comment(
            @Field("comment_idx") int comment_idx,
            @Field("contents") String contents
    );


}
