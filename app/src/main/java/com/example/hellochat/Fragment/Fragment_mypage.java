package com.example.hellochat.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.hellochat.Activity.Feed.Activity_Detail;
import com.example.hellochat.Activity.UserPage.Activity_Follower;
import com.example.hellochat.Activity.UserPage.Activity_Following;
import com.example.hellochat.Activity.Setting.Activity_Setting;
import com.example.hellochat.Activity.Feed.Activity_modify;
import com.example.hellochat.Adapter.UserPage.MyPageAdapter;
import com.example.hellochat.DTO.UserPage.MypageData;
import com.example.hellochat.DTO.UserPage.MypageResult;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_mypage extends Fragment {

    String TAG = this.getClass().getName();
    int idx;
    RecyclerView mRecyclerView;
    MypageResult result_data;
    ArrayList<MypageData> mypageData;
    MyPageAdapter mAdapter;
    int page =1 ,limit = 10;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;

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
        nestedScrollView = view.findViewById(R.id.scroll_view);
        progressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_mypage);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mypageData = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            idx = Integer.parseInt(bundle.getString("idx"));
        }else {
        }
        Log.d(TAG, "onCreateView: "+idx);
        getMyPageData(idx , page , limit);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getMyPageData(idx , page , limit);
                }
            }
        });
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: 새로고침");
                page = 1;
                getMyPageData(idx , page , limit);
                refreshLayout.setRefreshing(false);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //글 수정
        if (requestCode == 10) {
            if (resultCode != 11) {
                return;
            }
            getMyPageData(idx ,page ,limit );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        getMyPageData(idx , page , limit);
    }

    public void getMyPageData(int user_idx , int page , int limit){
        UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
        Call<MypageResult> call = service.getMypage(user_idx, page, limit);
        call.enqueue(new Callback<MypageResult>() {
            @Override
            public void onResponse(Call<MypageResult> call, Response<MypageResult> response) {
                if(response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    result_data = response.body();
                    mypageData = result_data.body;
                    mAdapter = new MyPageAdapter(mypageData);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onResponse: " + mypageData);
                    Log.d(TAG, "onResponse: " + result_data);

                    setClickListener(mAdapter, user_idx , page , limit);
                }
            }

            @Override
            public void onFailure(Call<MypageResult> call, Throwable t) {

            }
        });

    }
    public void setClickListener(MyPageAdapter mAdapter , int user_idx , int page , int limit){
        mAdapter.setOnFeedItemClickListener(new MyPageAdapter.OnFeedItemClickListener() {
            @Override
            public void onFeedItemClick(View v, int pos) {
                Intent intent = new Intent(getActivity() , Activity_Detail.class);
                intent.putExtra("feed_idx", mypageData.get(pos).feed_idx);
                startActivity(intent);
            }
        });
        mAdapter.setOnLikeClickListener(new MyPageAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(View v, int pos) {
                ClickHeart( mypageData.get(pos).feed_idx , user_idx, page , limit);
            }
        });
        mAdapter.setOnMoreBntClickListener(new MyPageAdapter.OnMoreBntClickListener() {
            @Override
            public void onMoreBntClick(View v, int pos) {
                String items[] = {"수정하기", "삭제하기"};
                AlertDialog.Builder dia = new AlertDialog.Builder(getActivity());
                dia.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(getActivity(), Activity_modify.class);
                            intent.putExtra("feed_idx" , mypageData.get(pos).feed_idx);
                            startActivityForResult(intent , 10);
                        } else if (which == 1) {
                            Delete(mypageData.get(pos).feed_idx, page, limit);
                        }
                    }
                });
                AlertDialog alertDialog = dia.create();
                alertDialog.show();
            }
        });
        mAdapter.setOnMyPageClickListener(new MyPageAdapter.OnMyPageClickListener() {
            @Override
            public void onmyPageClick(View v, int pos) {
                Intent intent = new Intent(getActivity(), Activity_Setting.class);
                startActivity(intent);
            }
        });
        mAdapter.setOnFollowerClickListener(new MyPageAdapter.OnFollowerClickListener() {
            @Override
            public void onFollowerClick(View v, int pos) {
                Intent intent = new Intent(getActivity(), Activity_Follower.class);
                startActivity(intent);

            }
        });
        mAdapter.setOnFollowingClickListener(new MyPageAdapter.OnFollowingClickListener() {
            @Override
            public void onFollowingClick(View v, int pos) {
                Intent intent = new Intent(getActivity(), Activity_Following.class);
                startActivity(intent);
            }
        });
    }

    public void ClickHeart(int feed_idx ,int user_idx ,int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_like(feed_idx, user_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                Log.d(TAG, "onResponse: "+user_idx);
                getMyPageData(user_idx , page , limit);
            }
            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });

    }

    public void Delete(int feed_idx , int page , int limit) {
        AlertDialog.Builder dia = new AlertDialog.Builder(getActivity());
        dia.setTitle("삭제하시겠습니까?");
        dia.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_post(feed_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        //메시지 받기
                        //alert띄우고 리사이클러뷰 새로고침
                        if (response.isSuccessful()) {
                            ResultData resultData = response.body();
                            Log.d(TAG, "onResponse: " + resultData.body);
                            if (resultData.body.equals("ok")) {
                                Log.d(TAG, "onResponse: ");
                                getMyPageData(idx , page , limit);

                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                    }
                });
            }
        });
        dia.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dia.create();
        alertDialog.show();
    }


}
