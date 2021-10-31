package com.example.hellochat;

import com.example.hellochat.DTO.DetailResult;
import com.example.hellochat.DTO.EditData;
import com.example.hellochat.DTO.FollowResult;
import com.example.hellochat.DTO.GetContents;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.UploadResult;
import com.example.hellochat.DTO.ViewBoardData;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NewsfeedApi {


    @FormUrlEncoded
    @POST("view_board_api.php")
    Call<ViewBoardData> getViewBoard(
            @Field("idx") int idx,
            @Field("page") int page,
            @Field("limit") int limit
    );

    @FormUrlEncoded
    @POST("following_view_api.php")
    Call<ViewBoardData> getViewFollowing(
            @Field("idx") int idx,
            @Field("page") int page,
            @Field("limit") int limit
    );

    @FormUrlEncoded
    @POST("edit_api.php")
    Call<EditData> getEditData(
            @Field("idx") String idx,
            @Field("content") String content,
            @Field("image_uri") String img_uri,
            @Field("record_path") String record_path
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
            @Field("feed_idx") int feed_idx,
            @Field("contents") String content,
            @Field("image_uri") String image_uri,
            @Field("record") String record

    );


    @FormUrlEncoded
    @POST("get_content.php")
    Call<GetContents> get_content(
            @Field("feed_idx") int feed_idx
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
            @Field("parent") int parent,
            @Field("record") String record
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

    @Multipart
    @POST("upload_Record.php")
    Call<ResultData> upload_Record(@Part MultipartBody.Part uploaded_file);

    @FormUrlEncoded
    @POST("click_follow.php")
    Call<ResultData> click_follow(
            @Field("my_idx") int my_idx,
            @Field("target_idx") int target_idx
    );

    @FormUrlEncoded
    @POST("get_follow_state.php")
    Call<ResultData> get_followed(
            @Field("my_idx") int my_idx,
            @Field("target_idx") int target_idx
    );

    @FormUrlEncoded
    @POST("get_follower.php")
    Call<FollowResult> get_follower(
            @Field("my_idx") int my_idx,
            @Field("page") int page,
            @Field("limit") int limit

    );

    @FormUrlEncoded
    @POST("get_following.php")
    Call<FollowResult> get_following(
            @Field("my_idx") int my_idx,
            @Field("page") int page,
            @Field("limit") int limit
    );


}
