package com.example.hellochat.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.hellochat.Activity.Activity_MyDetail;
import com.example.hellochat.Activity.Activity_UserDetail;
import com.example.hellochat.Activity.Activity_modify;
import com.example.hellochat.Adapter.NewsfeedAdapter;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.ViewBoardData;
import com.example.hellochat.DTO.ViewData;
import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class Fragment_feed_new extends Fragment {
    String TAG = this.getClass().getName();
    NewsfeedAdapter mAdapter;
    ViewBoardData datalist;
    RecyclerView mRecyclerView;
    ArrayList<ViewData> datainfo;
    int idx;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int page = 1, limit = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_new, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_feed);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        datainfo = new ArrayList<>();
        idx = Integer.parseInt(getPref(container));
        Log.d(TAG, "onCreateView: " + idx);
        //Retrofit 인스턴스 생성
        getdata(idx , page , limit);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        nestedScrollView = view.findViewById(R.id.scroll_view);
        progressBar = view.findViewById(R.id.progress_bar);



        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getdata(idx, page, limit);
                }
            }
        });


        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: 새로고침");
                mAdapter.clear();
                page = 1;
                getdata(idx, page, limit);
                refreshLayout.setRefreshing(false);
            }
        });


        Log.d(TAG, "onCreateView: ");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        getdata(idx, page, limit);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    public void getdata(int idx, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ViewBoardData> call = service.getViewBoard(idx, page, limit);
        call.enqueue(new Callback<ViewBoardData>() {
            @Override
            public void onResponse(Call<ViewBoardData> call, Response<ViewBoardData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    datalist = response.body();
                    datainfo = datalist.body;
                    Log.d(TAG, "onResponse: " + datainfo);
                    Log.d(TAG, "onResponse: " + datalist.result);
                    mAdapter = new NewsfeedAdapter(datainfo);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setOpenMyDetail(new NewsfeedAdapter.OpenMyDetail() {
                        @Override
                        public void openMyDetail(View v, int position) {
                            Intent intent = new Intent(getActivity() , Activity_MyDetail.class);
                            intent.putExtra("user_idx" , datainfo.get(position).user_idx);
                            startActivity(intent);
                        }
                    });
                    mAdapter.setOpenUserDetail(new NewsfeedAdapter.OpenUserDetail() {
                        @Override
                        public void openUserDetail(View v, int position) {
                            Intent intent = new Intent(getActivity() , Activity_UserDetail.class);
                            intent.putExtra("user_idx" , datainfo.get(position).user_idx);
                            startActivity(intent);
                        }
                    });
                    mAdapter.setOnItemClickListener(new NewsfeedAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Log.d(TAG, "onItemClick: ");
                            ClickHeart(datainfo.get(position).feed_idx , idx , page , limit);
                        }
                    });
                    mAdapter.setOnMoreClickListener(new NewsfeedAdapter.OnMoreBntClickListener() {
                        @Override
                        public void onMoreBntClick(View v, int position) {
                            String items[] = {"수정하기", "삭제하기"};
                            AlertDialog.Builder dia = new AlertDialog.Builder(getActivity());
                            dia.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        Intent intent = new Intent(getActivity() , Activity_modify.class);
                                        Log.d(TAG, "onClick: "+datainfo.get(position).feed_idx);
                                        intent.putExtra("feed_idx", datainfo.get(position).feed_idx);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    } else if (which == 1) {
                                        Delete(datainfo.get(position).feed_idx  , page , limit);
                                    }
                                }
                            });
                            AlertDialog alertDialog = dia.create();
                            alertDialog.show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ViewBoardData> call, Throwable t) {
                Log.d(TAG, "onFailure: 통신실패" + t.getMessage());
            }
        });

    }
    public void ClickHeart(int feed_idx, int user_idx ,int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_like(feed_idx, user_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                getdata(user_idx , page , limit);
            }
            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
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
                                getdata(idx,page ,limit);
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
    public String getPref(ViewGroup container) {
        SharedPreferences pref = container.getContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }
}