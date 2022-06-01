package com.app.livewave.fragments;

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
import com.app.livewave.adapters.TransferAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.TransferModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class TransferredFragment extends Fragment {

    SwipeRefreshLayout swipe_to_refresh;
    RecyclerView rv_transferred;
    TransferAdapter adapter;
    ProgressBar progress_bar;
    KProgressHUD dialog;
    LinearLayoutManager layoutManager;
    List<TransferModel> transferModelList = new ArrayList<>();
    NestedScrollView nested_scroll_view;
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    String nextPageUrl = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transferred, container, false);
        initViews(view);
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
                            loadTransferred();
                        }
                    }
                }
            }
        });
        swipe_to_refresh.setOnRefreshListener(() -> {
            currentPageNumber = 1;
            transferModelList.clear();
            loadTransferred();
            swipe_to_refresh.setRefreshing(false);
        });
        return view;
    }

    private void loadTransferred() {
        if (currentPageNumber <= 1)
            dialog.show();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getTransfers(currentPageNumber), getContext(), new ApiResponseHandlerWithFailure<GenericDataModel<TransferModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<TransferModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
                    if (currentPageNumber == 1) {
                        transferModelList = new ArrayList<>();
                        transferModelList = data.body().getData().getData();
                    } else {
                        transferModelList.addAll(data.body().getData().getData());
                    }
                    adapter.setList(transferModelList);
                    dialog.dismiss();
                    progress_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });
    }
    private void initViews(View view) {
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        dialog = BaseUtils.progressDialog(getContext());
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        progress_bar = view.findViewById(R.id.progress_bar);
        rv_transferred = view.findViewById(R.id.rv_transferred);
        rv_transferred.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_transferred.setLayoutManager(layoutManager);
        adapter = new TransferAdapter(getContext());
        rv_transferred.setAdapter(adapter);
        loadTransferred();
    }
    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}