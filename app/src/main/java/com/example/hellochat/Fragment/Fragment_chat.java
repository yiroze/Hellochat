package com.example.hellochat.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.hellochat.Activity.Activity_Chatting;
import com.example.hellochat.Adapter.ChatListAdapter;
import com.example.hellochat.Adapter.DetailAdapter;
import com.example.hellochat.ChatApi;
import com.example.hellochat.DTO.ChatList;
import com.example.hellochat.DTO.ChatListData;
import com.example.hellochat.DTO.MypageData;
import com.example.hellochat.DTO.MypageResult;
import com.example.hellochat.MypageApi;
import com.example.hellochat.R;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_chat extends Fragment {

    EditText editText;
    Button bnt;

    String TAG = this.getClass().getName();

    RecyclerView mRecyclerView;
    int my_idx;
    ArrayList<ChatList> data;
    ChatListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.chat_list_recycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        get_ChatList(Integer.parseInt(getPref(container)));
        Log.d(TAG, "onCreateView: " + Integer.parseInt(getPref(container)));
        my_idx = Integer.parseInt(getPref(container));
        return view;
    }


    public void get_ChatList(int my_idx) {
        ChatApi service = RetrofitClientInstance.getRetrofitInstance().create(ChatApi.class);
        Call<ChatListData> call = service.getChatList(my_idx);
        call.enqueue(new Callback<ChatListData>() {
            @Override
            public void onResponse(Call<ChatListData> call, Response<ChatListData> response) {
                ChatListData result = response.body();
                data = result.body;
                mAdapter = new ChatListAdapter(data);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "onResponse: " + data.toString());
                mAdapter.setonChatListClick(new ChatListAdapter.OnChatListClick() {
                    @Override
                    public void onChatListClick(View v, int pos) {
                        Intent intent = new Intent(getActivity(), Activity_Chatting.class);
                        intent.putExtra("user_idx", data.get(pos).friend_idx);
                        intent.putExtra("user_name" , data.get(pos).name);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<ChatListData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        get_ChatList(my_idx);
    }

    public String getPref(ViewGroup container) {
        SharedPreferences pref = container.getContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }


}
