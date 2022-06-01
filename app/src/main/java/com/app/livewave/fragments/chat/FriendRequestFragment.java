package com.app.livewave.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.FriendRequestAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class FriendRequestFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, PlayerStateListener {

    RecyclerView rv_friend_requests;
    FriendRequestAdapter adapter;
    ProgressBar progress_bar;
    NestedScrollView nested_scroll_view;
//    Toolbar toolbar;
//    CollapsingToolbarLayout collapsingToolbarLayout;
    SwipeRefreshLayout swipe_to_refresh;
    //AppBarLayout app_bar;
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    private int totalPages = 0;
    String nextPageUrl = null;
    UserModel userModel;
    List<UserModel> friendRequestList = new ArrayList<>();
//    KProgressHUD dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        initViews(view);
//        dialog.show();
        ((HomeActivity)getActivity()).showProgressDialog();
        //app_bar.addOnOffsetChangedListener(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Follow Requests");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));

        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        rv_friend_requests = view.findViewById(R.id.rv_friend_requests);
        rv_friend_requests.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_friend_requests.setLayoutManager(layoutManager);
        adapter = new FriendRequestAdapter(getActivity());
        rv_friend_requests.setAdapter(adapter);
        loadFriendRequests();
        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    currentItems = layoutManager.getChildCount();
                    totalItems = layoutManager.getItemCount();
                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
                    if ((currentItems + scrollOutItems) >= totalItems) {
                        if (nextPageUrl != null) {
                            progress_bar.setVisibility(View.VISIBLE);
                            currentPageNumber++;
                            loadFriendRequests();
                        }
                    }
                }
            }
        });
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                friendRequestList.clear();
                loadFriendRequests();
                swipe_to_refresh.setRefreshing(false);
            }
        });

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_friend_request);
//        initViews();
//        dialog.show();
//        app_bar.addOnOffsetChangedListener(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Follow Requests");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//
//        nested_scroll_view = findViewById(R.id.nested_scroll_view);
//        progress_bar = findViewById(R.id.progress_bar);
//        rv_friend_requests = findViewById(R.id.rv_friend_requests);
//        rv_friend_requests.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(FriendRequestActivity.this, LinearLayoutManager.VERTICAL, false);
//        rv_friend_requests.setLayoutManager(layoutManager);
//        adapter = new FriendRequestAdapter(FriendRequestActivity.this);
//        rv_friend_requests.setAdapter(adapter);
//        loadFriendRequests();
//        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (v.getChildAt(v.getChildCount() - 1) != null) {
//                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                        scrollY > oldScrollY) {
//                    currentItems = layoutManager.getChildCount();
//                    totalItems = layoutManager.getItemCount();
//                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
//                    if ((currentItems + scrollOutItems) >= totalItems) {
//                        if (nextPageUrl != null) {
//                            progress_bar.setVisibility(View.VISIBLE);
//                            currentPageNumber++;
//                            loadFriendRequests();
//                        }
//                    }
//                }
//            }
//        });
//        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                currentPageNumber = 1;
//                friendRequestList.clear();
//                loadFriendRequests();
//                swipe_to_refresh.setRefreshing(false);
//            }
//        });
//    }

    private void loadFriendRequests() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFriends(userModel.getId(), currentPageNumber),getActivity(), new ApiResponseHandler<GenericDataModel<UserModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<UserModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
                    if (currentPageNumber == 1) {
                        friendRequestList = new ArrayList<>();
                        friendRequestList = data.body().getData().getData();
                    } else {
                        friendRequestList.addAll(data.body().getData().getData());
                    }
                    adapter.setList(friendRequestList);
                    ((HomeActivity)getActivity()).hideProgressDialog();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
    }
    private void initViews(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
//        dialog = BaseUtils.progressDialog(getActivity());

        //app_bar = view.findViewById(R.id.app_bar);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
//        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        collapsingToolbarLayout =
//                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
    }

    @Override
    public void onDestroy() {
//        dialog.dismiss();
        ((HomeActivity)getActivity()).hideProgressDialog();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipe_to_refresh.setEnabled(verticalOffset == 0);
    }

    @Override
    public void updatePlayerState() {

    }
}