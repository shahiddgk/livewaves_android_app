package com.app.livewave.fragments.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.FollowAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.HIDE_HEADER;

public class FollowFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, PlayerStateListener {
    String id;
    RecyclerView rv_followings;
    FollowAdapter adapter;
    ProgressBar progress_bar;
    NestedScrollView nested_scroll_view;
    //    Toolbar toolbar;
//    CollapsingToolbarLayout collapsingToolbarLayout;
    SwipeRefreshLayout swipe_to_refresh;
    //    AppBarLayout app_bar;
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    private int totalPages = 0;
    String nextPageUrl = null;
    List<FollowModel> followModelList = new ArrayList<>();
    int noOfFriendRequests;
    String userId;
    UserModel userModel;
    TextView textCartItemCount;
    private KProgressHUD dialog;
    //  KProgressHUD dialog;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        id = getArguments().getString("Id");
        userId = getArguments().getString("userId");
        initViews(view);

//        app_bar.addOnOffsetChangedListener(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setHasOptionsMenu(true);
        if (id != null) {
            if (userId != null) {
                if (userId.equals(String.valueOf(userModel.getId()))) {
//                        noOfFriendRequests = getIntent().getIntExtra("noOfFriendRequests", 0);
                    noOfFriendRequests = getArguments().getInt("noOfFriendRequests", 0);
                }
            }
        }

        // collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));

        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        rv_followings = view.findViewById(R.id.rv_followings);
        dialog = BaseUtils.progressDialog(getActivity());
        rv_followings.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_followings.setLayoutManager(layoutManager);
        if (id.equals("0"))
            adapter = new

                    FollowAdapter(getActivity(), 0);
        else
            adapter = new

                    FollowAdapter(getActivity(), 1);

        rv_followings.setAdapter(adapter);
        if (id.equals("0"))

            loadFollowings();
        else

            loadFollowers();
        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) ->

        {
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
                            if (id.equals("0"))
                                loadFollowings();
                            else
                                loadFollowers();
                        }
                    }
                }
            }
        });
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                followModelList.clear();
                if (id.equals("0"))
                    loadFollowings();
                else
                    loadFollowers();
                swipe_to_refresh.setRefreshing(false);
            }
        });
        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_follow);
//        id = getIntent().getStringExtra("Id");
//        userId = getIntent().getStringExtra("userId");
//        initViews();
//
//        app_bar.addOnOffsetChangedListener(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        if (id != null){
//            if (id.equals("0")) {
//                collapsingToolbarLayout.setTitle("Following");
//            } else {
//                if (userId != null) {
//                    if (userId.equals(String.valueOf(userModel.getId()))) {
//                        noOfFriendRequests = getIntent().getIntExtra("noOfFriendRequests", 0);
//                    }
//                }
//                collapsingToolbarLayout.setTitle("Followers");
//            }
//        }
//
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//
//        nested_scroll_view = findViewById(R.id.nested_scroll_view);
//        progress_bar = findViewById(R.id.progress_bar);
//        rv_followings = findViewById(R.id.rv_followings);
//        rv_followings.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowActivity.this, LinearLayoutManager.VERTICAL, false);
//        rv_followings.setLayoutManager(layoutManager);
//        if (id.equals("0"))
//            adapter = new FollowAdapter(FollowActivity.this, 0);
//        else
//            adapter = new FollowAdapter(FollowActivity.this, 1);
//
//        rv_followings.setAdapter(adapter);
//        if (id.equals("0"))
//            loadFollowings();
//        else
//            loadFollowers();
//        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (v.getChildAt(v.getChildCount() - 1) != null) {
//                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                        scrollY > oldScrollY) {
//                    currentItems = layoutManager.getChildCount();
//                    totalItems = layoutManager.getItemCount();
//                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
//                    if ((currentItems + scrollOutItems) >= totalItems) {
//                        if (nextPageUrl!= null) {
//                            progress_bar.setVisibility(View.VISIBLE);
//                            currentPageNumber++;
//                            if (id.equals("0"))
//                                loadFollowings();
//                            else
//                                loadFollowers();
//                        }
//                    }
//                }
//            }
//        });
//        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                currentPageNumber = 1;
//                followModelList.clear();
//                if (id.equals("0"))
//                    loadFollowings();
//                else
//                    loadFollowers();
//                swipe_to_refresh.setRefreshing(false);
//            }
//        });
//    }

    private void loadFollowers() {
        dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowers(userId, currentPageNumber), getActivity(), new ApiResponseHandler<GenericDataModel<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<FollowModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
                    if (currentPageNumber == 1) {
                        followModelList = new ArrayList<>();
                        followModelList = data.body().getData().getData();
                    } else {
                        followModelList.addAll(data.body().getData().getData());
                    }
                    Activity activity = getActivity();
                    if (isAdded() && activity != null) {
                        adapter.setList(followModelList);
                        dialog.dismiss();
                        progress_bar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void loadFollowings() {
        dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowings(userId, currentPageNumber), getActivity(), new ApiResponseHandler<GenericDataModel<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<FollowModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
                    if (currentPageNumber == 1) {
                        followModelList = new ArrayList<>();
                        followModelList = data.body().getData().getData();
                    } else {
                        followModelList.addAll(data.body().getData().getData());
                    }
                    Activity activity = getActivity();
                    if (isAdded() && activity != null) {
                        adapter.setList(followModelList);
//                    dialog.dismiss();
                        dialog.dismiss();

                        progress_bar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initViews(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
        //app_bar = view.findViewById(R.id.app_bar);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
//        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        collapsingToolbarLayout =
//                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);

        // dialog = BaseUtils.progressDialog(getActivity());
    }

    @Override
    public void onDestroy() {
//        dialog.dismiss();
        ApiClient.getInstance().getInterface().getFollowings(userId,currentPageNumber).cancel();
        ApiClient.getInstance().getInterface().getFollowers(userId,currentPageNumber).cancel();
        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (id.equals("1")) {
            if (userId != null && userId.equals(String.valueOf(userModel.getId()))) {
                getActivity().getMenuInflater().inflate(R.menu.followers_menu, menu);
                final MenuItem menuItem = menu.findItem(R.id.friend_requests);
                View actionView = menuItem.getActionView();
                textCartItemCount = actionView.findViewById(R.id.cart_badge);
                setBadge();
                actionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onOptionsItemSelected(menuItem);
                    }
                });
                super.onCreateOptionsMenu(menu, inflater);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setBadge() {
        if (textCartItemCount != null) {
            if (noOfFriendRequests == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(noOfFriendRequests, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.friend_requests: {
//                startActivity(new Intent(getActivity(), FriendRequestFragment.class));

                ((HomeActivity) getActivity()).loadFragment(R.string.tag_friend_request, null);
                return true;
            } case R.id.action_search: {
                item.setVisible(false);
                return true;
            } case R.id.button_waves: {
                Bundle bundle = new Bundle();
                bundle.putBoolean(HIDE_HEADER, false);
                ((HomeActivity)getActivity()).loadFragment(R.string.tag_waves_Features, bundle);

                return true;
            }
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