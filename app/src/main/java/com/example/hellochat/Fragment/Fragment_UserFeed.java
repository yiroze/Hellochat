package com.example.hellochat.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hellochat.Activity.Feed.Activity_Detail;
import com.example.hellochat.Activity.Feed.Activity_modify;
import com.example.hellochat.Adapter.UserPage.MyPageAdapter;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.UserPage.MypageData;
import com.example.hellochat.DTO.UserPage.MypageResult;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.Interface.UserPageApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_UserFeed extends Fragment {
    private static final String TAG = "Fragment_UserFeed";
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipe;
    RecyclerView mRecycler;
    ProgressBar progress_bar;
    MyPageAdapter mAdapter;
    MypageResult mData;
    ArrayList<MypageData> mList;
    int page = 1, limit = 10;

    int user_idx;
    public Fragment_UserFeed(int user){
        user_idx = user;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_user_feed, container, false);
        InitView(view);
        mList = new ArrayList<>();
        getdata(user_idx , page , limit);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progress_bar.setVisibility(View.VISIBLE);
                    getdata(user_idx, page, limit);
                }
            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: 새로고침");
                page = 1;
                getdata(user_idx, page, limit);
                swipe.setRefreshing(false);
            }
        });
        return view;
    }

    void InitView(View view){
        nestedScrollView = view.findViewById(R.id.scroll_view);
        swipe = view.findViewById(R.id.swipe);
        mRecycler = view.findViewById(R.id.recycler_feed);
        progress_bar = view.findViewById(R.id.progress_bar);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLinearLayoutManager);
    }
    public void getdata(int idx, int page, int limit) {
        UserPageApi service = RetrofitClientInstance.getRetrofitInstance().create(UserPageApi.class);
        Call<MypageResult> call = service.get_user_page(user_idx, page, limit);
        call.enqueue(new Callback<MypageResult>() {
            @Override
            public void onResponse(Call<MypageResult> call, Response<MypageResult> response) {
                progress_bar.setVisibility(View.GONE);
                mData = response.body();
                mList = mData.body;
                mAdapter = new MyPageAdapter(mList);
                mRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                setClickListener(mAdapter, user_idx , page , limit);
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
                intent.putExtra("feed_idx", mList.get(pos).feed_idx);
                startActivity(intent);
            }
        });
        mAdapter.setOnLikeClickListener(new MyPageAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(View v, int pos) {
                ClickHeart( mList.get(pos).feed_idx , user_idx, page , limit);
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
                            intent.putExtra("feed_idx" , mList.get(pos).feed_idx);
                            startActivityForResult(intent , 10);
                        } else if (which == 1) {
                            Delete(mList.get(pos).feed_idx, page, limit);
                        }
                    }
                });
                AlertDialog alertDialog = dia.create();
                alertDialog.show();
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
                getdata(user_idx , page , limit);
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
                                getdata(user_idx , page , limit);

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