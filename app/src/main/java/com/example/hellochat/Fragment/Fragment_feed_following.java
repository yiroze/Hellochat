package com.example.hellochat.Fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hellochat.Activity.UserPage.Activity_MyDetail;
import com.example.hellochat.Activity.Feed.Activity_Trans;
import com.example.hellochat.Activity.UserPage.Activity_UserDetail;
import com.example.hellochat.Activity.Feed.Activity_modify;
import com.example.hellochat.Adapter.Feed.NewsfeedAdapter;
import com.example.hellochat.Service.ClientService;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.DTO.Feed.ViewBoardData;
import com.example.hellochat.DTO.Feed.ViewData;
import com.example.hellochat.Interface.NewsfeedApi;
import com.example.hellochat.Util.Papago;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;
import com.example.hellochat.Util.GetLanguageCode;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class Fragment_feed_following extends Fragment {
    String TAG = this.getClass().getName();
    NewsfeedAdapter mAdapter;
    ViewBoardData datalist;
    RecyclerView mRecyclerView;
    ArrayList<ViewData> datainfo;
    int idx;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    int page = 1, limit = 10;
    ViewGroup contain;
    TextToSpeech tts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        contain = container;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_following, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_feed);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        datainfo = new ArrayList<>();
        idx = Integer.parseInt(getPref(container));
        Log.d(TAG, "onCreateView: " + idx);
        //Retrofit 인스턴스 생성
        getdata(idx, page, limit);

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
        tts = new TextToSpeech(getActivity() , new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
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
        Call<ViewBoardData> call = service.getViewFollowing(idx, page, limit);
        call.enqueue(new Callback<ViewBoardData>() {
            @Override
            public void onResponse(Call<ViewBoardData> call, Response<ViewBoardData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    datalist = response.body();
                    datainfo = datalist.body;
                    Log.d(TAG, "onResponse: " + datainfo);
                    Log.d(TAG, "onResponse: " + response.body().result);
                    mAdapter = new NewsfeedAdapter(datainfo);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setOnLongClickListener((v, position) -> {
                        PopupMenu popupMenu = new PopupMenu(getContext(), v);
                        popupMenu.getMenuInflater().inflate(R.menu.menulist, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.trans:
                                        Log.d(TAG, "onMenuItemClick: "+ getFirstTargetLang(contain));
                                        Intent intentTrans = new Intent(getActivity() , Activity_Trans.class);
                                        intentTrans.putExtra("content" , datainfo.get(position).contents);
                                        intentTrans.putExtra("targetLang" , getFirstTargetLang(contain));
                                        startActivity(intentTrans);
                                        return true;

                                    case R.id.tts:
                                        String text = datainfo.get(position).contents;
                                        new GetLanguageCode(text , getActivity()).start();
                                        return true;

                                    case R.id.copy:
                                        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText("text",datainfo.get(position).contents); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                                        clipboardManager.setPrimaryClip(clipData);
                                        Toast.makeText( getActivity() , "복사되었습니다.",Toast.LENGTH_SHORT).show();
                                        return true;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    });
                    mAdapter.setOpenMyDetail(new NewsfeedAdapter.OpenMyDetail() {
                        @Override
                        public void openMyDetail(View v, int position) {
                            Intent intent = new Intent(getActivity(), Activity_MyDetail.class);
                            intent.putExtra("user_idx", datainfo.get(position).user_idx);
                            startActivity(intent);
                        }
                    });
                    mAdapter.setOpenUserDetail(new NewsfeedAdapter.OpenUserDetail() {
                        @Override
                        public void openUserDetail(View v, int position) {
                            Intent intent = new Intent(getActivity(), Activity_UserDetail.class);
                            intent.putExtra("user_idx", datainfo.get(position).user_idx);
                            startActivity(intent);
                        }
                    });
                    mAdapter.setOnItemClickListener(new NewsfeedAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Log.d(TAG, "onItemClick: ");
                            ClickHeart(datainfo.get(position).feed_idx, idx, page, limit, datainfo.get(position).user_idx);
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
                                        Intent intent = new Intent(getActivity(), Activity_modify.class);
                                        Log.d(TAG, "onClick: " + datainfo.get(position).feed_idx);
                                        intent.putExtra("feed_idx", datainfo.get(position).feed_idx);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    } else if (which == 1) {
                                        Delete(datainfo.get(position).feed_idx, page, limit);
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

    public void ClickHeart(int feed_idx, int user_idx, int page, int limit, int accept_idx) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_like(feed_idx, user_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                getdata(user_idx, page, limit);
                Intent intent = new Intent(getActivity(), ClientService.class);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("content", feed_idx);
                    jsonObject.put("accept_user_idx", accept_idx);
                    jsonObject.put("content_type", 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("msg", jsonObject.toString());
                getActivity().startService(intent);
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
            }
        });
    }

    public void Delete(int feed_idx, int page, int limit) {
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
                                getdata(idx, page, limit);
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

    public String getFirstTargetLang(ViewGroup container){
        SharedPreferences pref = container.getContext().getSharedPreferences("Translator", MODE_PRIVATE);
        JSONArray JSON = null;
        try {
            JSON = new JSONArray(pref.getString("targetlang", ""));
            return JSON.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

