package com.app.livewave.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.adapters.WalletAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.WithdrawalModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class WithdrawalFragment extends Fragment {
    SwipeRefreshLayout swipe_to_refresh;
    RecyclerView rv_wallet;
    WalletAdapter adapter;
    ProgressBar progress_bar;
    List<WithdrawalModel> withdrawalModelList = new ArrayList<>();
    KProgressHUD dialog;
    NestedScrollView nested_scroll_view;
    LinearLayoutManager layoutManager;
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    String nextPageUrl = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdrawal, container, false);
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
                            loadHistory();
                        }
                    }
                }
            }
        });
        swipe_to_refresh.setOnRefreshListener(() -> {
            currentPageNumber = 1;
            withdrawalModelList.clear();
            loadHistory();
            swipe_to_refresh.setRefreshing(false);
        });
        return view;
    }

    private void loadHistory() {
        if (currentPageNumber <= 1)
            dialog.show();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getWithdrawal(currentPageNumber), getContext(), new ApiResponseHandlerWithFailure<GenericDataModel<WithdrawalModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<WithdrawalModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
                    if (currentPageNumber == 1) {
                        withdrawalModelList = new ArrayList<>();
                        withdrawalModelList = data.body().getData().getData();
                    } else {
                        withdrawalModelList.addAll(data.body().getData().getData());
                    }
                    adapter.setList(withdrawalModelList);
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
        dialog = BaseUtils.progressDialog(getContext());
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        rv_wallet = view.findViewById(R.id.rv_wallet);
        rv_wallet.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_wallet.setLayoutManager(layoutManager);
        adapter = new WalletAdapter(getContext());
        rv_wallet.setAdapter(adapter);
        loadHistory();
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }
}