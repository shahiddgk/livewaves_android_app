package com.app.livewave.fragments;

import static com.app.livewave.utils.Constants.NEWS_FEED;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.livewave.R;
import com.app.livewave.adapters.PostAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class PaidPostFragment extends Fragment {

    private RecyclerView rv_paid_post;
    private PostAdapter adapter;
    private List<PostModel> posts = new ArrayList<>();
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    String nextPageUrl = null;
    private NestedScrollView nested_scroll_view;
    private SwipeRefreshLayout swipe_to_refresh;
    KProgressHUD dialog;
    private ProgressBar progress_bar;
    private UserModel userModel;
    private LinearLayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_paid_post, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        Paper.init(getContext());
        userModel = Paper.book().read(Constants.currentUser);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        dialog = BaseUtils.progressDialog(getContext());
        rv_paid_post = view.findViewById(R.id.rv_paid_posts);
//        ViewGroup.LayoutParams params=rv_paid_post.getLayoutParams();
//        params.height= (int) Constants.screenHeight+1;
//        rv_paid_post.setLayoutParams(params);
        setPaidPostAdapter();
        swipeToRefresh();
    }

    private void swipeToRefresh() {
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                posts.clear();
                loadPaidPost();
                swipe_to_refresh.setRefreshing(false);
            }
        });
    }


    private void setPaidPostAdapter() {
        rv_paid_post.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_paid_post.setLayoutManager(layoutManager);
        rv_paid_post.setNestedScrollingEnabled(false);
        nested_scroll_view.setNestedScrollingEnabled(true);
        adapter = new PostAdapter(getContext(), userModel.getId(), NEWS_FEED, rv_paid_post);
        rv_paid_post.setAdapter(adapter);
        loadPaidPost();
        onScroll();
    }

    private void onScroll() {
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
                            loadPaidPost();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().getInterface().getPaidPosts(currentPageNumber).cancel();
    }

    private void loadPaidPost() {
        if (currentPageNumber <= 1)
            dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getPaidPosts(currentPageNumber), getContext(), new ApiResponseHandler<GenericDataModel<PostModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<PostModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
//                    totalPages = data.body().getData().getLastPage();
                    if (currentPageNumber == 1) {
                        posts = new ArrayList<>();
                        if(!(data.body().getData().getData() == null)){
                            posts = (data.body().getData().getData());
                        }
                    } else {
                        posts.addAll(data.body().getData().getData());
                    }
                    adapter.setList(posts);
                    dialog.dismiss();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
    }
}