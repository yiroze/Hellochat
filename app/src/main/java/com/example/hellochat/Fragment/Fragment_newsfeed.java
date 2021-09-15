package com.example.hellochat.Fragment;

import android.content.Intent;
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

import com.example.hellochat.Activity.Activity_Edit;
import com.example.hellochat.Adapter.NewsfeedAdapter;
import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.DTO.ViewBoardData;
import com.example.hellochat.DTO.ViewData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_newsfeed extends Fragment  {
    String TAG = this.getClass().getName();
    NewsfeedAdapter mAdapter;
    ViewBoardData datalist;
    RecyclerView mRecyclerView;
    ArrayList<ViewData> datainfo;
    ImageView bell, edit;
    int idx;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int page = 1, limit = 10;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_feed);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        datainfo = new ArrayList<>();
        Bundle bundle = getArguments();

        if (bundle != null) {
            idx = Integer.parseInt(bundle.getString("idx"));
        } else {
        }
        Log.d(TAG, "onCreateView: " + idx);
        //Retrofit 인스턴스 생성
        getdata(idx , page , limit);

        mAdapter = new NewsfeedAdapter(datainfo);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        nestedScrollView = view.findViewById(R.id.scroll_view);
        progressBar = view.findViewById(R.id.progress_bar);


        edit = view.findViewById(R.id.edit);
        
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

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity() , Activity_Edit.class);
            startActivity(intent);
        });

        Log.d(TAG, "onCreateView: ");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //글쓰기
        if (requestCode == 10) {
            if (resultCode != 11) {
                return;
            }
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            getdata(idx, page, limit);
        }
        //글 수정
        if (requestCode == 100) {
            if (resultCode != 101) {
                return;
            }
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            getdata(idx, page, limit);
        }
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
        mAdapter.clear();
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
                    mAdapter = new NewsfeedAdapter(datainfo);
                    Log.d(TAG, "onResponse: setadapter");
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setOnItemClickListener(new NewsfeedAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Log.d(TAG, "onItemClick: ");
                            ClickHeart(datainfo.get(position).feed_idx , idx , page , limit);
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


}