package com.app.livewave.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.adapters.HashtagSearchAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ParameterModels.HashtagsModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class HashtagsListFragment extends Fragment {

    private SwipeRefreshLayout swipe_to_refresh;

    private RecyclerView rv_hashtag;
    private HashtagSearchAdapter hashtagTrendListAdapter;
    private List<HashtagsModel> hashtagsModelList = new ArrayList<>();

    public HashtagsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hashtags_list, container, false);
        initComponents(view);
        Log.d("!@#", "view loaded hahsh");
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initComponents(View view) {
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        swipe_to_refresh.setEnabled(false);

        rv_hashtag = view.findViewById(R.id.rv_hashtag);
        rv_hashtag.hasFixedSize();
        hashtagTrendListAdapter = new HashtagSearchAdapter(getContext(), hashtagsModelList);
        rv_hashtag.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rv_hashtag.setAdapter(hashtagTrendListAdapter);
        filter("");
    }

    public void filter(String text) {
//        if (text.equals("")) {
//            hashtagsModelList = new ArrayList<>();
//            hashtagTrendListAdapter.setList(hashtagsModelList);
//        } else {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().searchHashTags(text, "hashtag"), getContext(), new ApiResponseHandler<List<HashtagsModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<HashtagsModel>>> data) {
                if (data.body().getData() != null)
                    hashtagsModelList = data.body().getData();
                hashtagTrendListAdapter.setList(hashtagsModelList);
            }
        });
//        }
    }
}