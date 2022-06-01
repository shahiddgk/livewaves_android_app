package com.app.livewave.fragments.alert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.SubscriberActivity;
import com.app.livewave.adapters.AlertAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.CheckPermissionOnJointStream;
import com.app.livewave.models.JoinStreamModel;
import com.app.livewave.models.ResponseModels.AlertModelNew;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.app.livewave.utils.Constants.HIDE_HEADER;

public class AlertFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, CheckPermissionOnJointStream {
    RecyclerView rv_alerts;
    AlertAdapter adapter;
    Toolbar toolbar;
    ProgressBar progress_bar;
    AppBarLayout app_bar;
    ArrayList<AlertModelNew> alerts = new ArrayList<>();
    //    private int currentItems, totalItems, scrollOutItems;
//    private int currentPageNumber = 1;
//    private int totalPages = 0;
    NestedScrollView nested_scroll_view;
    SwipeRefreshLayout swipe_to_refresh;
    KProgressHUD dialog;
    TextView tvNoAlerts;
    JoinStreamModel joinStream;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        initViews(view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Notifications");
        collapsingToolbarLayout.setContentScrimColor(getContext().getResources().getColor(R.color.buttercup));
        return view;
    }

    private void initClickListener() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
//                    startActivity(new Intent(getContext(), SearchFragment.class));
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.HIDE_HEADER, false);
                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_search, null);
                    return true;
                } else if (item.getItemId() == R.id.button_waves) {

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(HIDE_HEADER, false);
                    ((HomeActivity)getActivity()).loadFragment(R.string.tag_waves_Features, bundle);
                    return true;

                }
                return false;
            }
        });
    }

    private void initViews(View view) {
        app_bar = view.findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(this);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        rv_alerts = view.findViewById(R.id.rv_alerts);
        tvNoAlerts = view.findViewById(R.id.tvNoAlerts);
        rv_alerts.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_alerts.setLayoutManager(layoutManager);
        adapter = new AlertAdapter(getContext());
        rv_alerts.setAdapter(adapter);
        adapter.setUpPermissionOnJointStream(this);
        dialog = BaseUtils.progressDialog(getContext());
        loadNotifications();
//        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (v.getChildAt(v.getChildCount() - 1) != null) {
//                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                        scrollY > oldScrollY) {
//                    currentItems = layoutManager.getChildCount();
//                    totalItems = layoutManager.getItemCount();
//                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
//                    if ((currentItems + scrollOutItems) >= totalItems) {
//                        if (currentPageNumber != totalPages) {
//                            progress_bar.setVisibility(View.VISIBLE);
//                            currentPageNumber++;
//                            loadNotifications();
//                        }
//                    }
//                }
//            }
//        });
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                currentPageNumber = 1;
                alerts.clear();
                loadNotifications();
                swipe_to_refresh.setRefreshing(false);
            }
        });
    }

    private void loadNotifications() {
//        if (currentPageNumber <= 1)
        dialog.show();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getNotifications(), getContext(),
                new ApiResponseHandlerWithFailure<List<AlertModelNew>>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<List<AlertModelNew>>> data) {
                        if (data != null) {
//                    currentPageNumber = data.body().getData().getCurrentPage();
//                    totalPages = data.body().getData().getLastPage();
//                    if (currentPageNumber == 1) {
//                        alerts = new ArrayList<>();
                            //alerts = data.body().getData().getData();
                            alerts.addAll(data.body().getData());
//                    } else {
//                        alerts.addAll(data.body().getData().getData());
//                        dialog.dismiss();
//                    }
                            if (alerts.size() > 0) {
                                tvNoAlerts.setVisibility(View.GONE);
                            }
                            adapter.setList(alerts);
                            dialog.dismiss();
                            progress_bar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(String failureCause) {
                        BaseUtils.showLottieDialog(getContext(), failureCause, R.raw.invalid, positive -> {
                        });
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipe_to_refresh.setEnabled(verticalOffset == 0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (joinStream != null) {
                        Intent intent = new Intent(getContext(), SubscriberActivity.class);

                        intent.putExtra("Title", joinStream.getTitle());
                        intent.putExtra("StreamId", joinStream.getStreamId());
                        intent.putExtra("HostPlatformId", joinStream.getParentPlatformID());
                        intent.putExtra("PlatformId", joinStream.getPlatformID());
                        startActivity(intent);

//                        Bundle bundle = new Bundle();
//                        bundle.putString("Title", joinStream.getTitle());
//                        bundle.putInt("StreamId", joinStream.getStreamId());
//                        bundle.putString("HostPlatformId", joinStream.getParentPlatformID());
//                        bundle.putString("PlatformId", joinStream.getPlatformID());
//                        ((HomeActivity) getActivity()).loadFragment(R.string.tag_subscriber, bundle);
                    }
                }
        }

    }

    @Override
    public void onJoinStream(JoinStreamModel joinStreamModel) {
        joinStream = joinStreamModel;
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.CAMERA,
                            android.Manifest.permission.RECORD_AUDIO},
                    1);
        } else {
            Intent intent = new Intent(getContext(), SubscriberActivity.class);
            intent.putExtra("Title", joinStreamModel.getTitle());
            intent.putExtra("StreamId", joinStreamModel.getStreamId());
            intent.putExtra("HostPlatformId", joinStreamModel.getParentPlatformID());
            intent.putExtra("PlatformId", joinStreamModel.getPlatformID());
            startActivity(intent);

//            Bundle bundle = new Bundle();
//            bundle.putString("Title", joinStreamModel.getTitle());
//            bundle.putInt("StreamId", joinStreamModel.getStreamId());
//            bundle.putString("HostPlatformId", joinStreamModel.getParentPlatformID());
//            bundle.putString("PlatformId", joinStreamModel.getPlatformID());
//            ((HomeActivity) getActivity()).loadFragment(R.string.tag_subscriber, bundle);
        }
    }
}