package com.app.livewave.fragments.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.adapters.StreamsListAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

import retrofit2.Response;

import static com.app.livewave.utils.Constants.GLOBAL_STREAMS;

public class LiveStreamsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recycleView;
    private StreamsListAdapter streamsListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipe_to_refresh;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private TextView tv_no_streams_running;
    private List<StreamModel> streamModelList;
    KProgressHUD dialog;
    public LiveStreamsListFragment() {
    }


    public static LiveStreamsListFragment newInstance(String param1) {
        LiveStreamsListFragment fragment = new LiveStreamsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_streams_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setDataAndCallApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataAndCallApi();
    }


    private void getStreamFollowingData() {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getFollowingStreams(), getContext(), new ApiResponseHandlerWithFailure<GenericDataModel<StreamModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<StreamModel>>> data) {
                setData(data.body().getData().getData());
                swipe_to_refresh.setRefreshing(false);
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });
    }

    private void getStreamData() {
         ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getGlobalStreams(), getContext(), new ApiResponseHandlerWithFailure<GenericDataModel<StreamModel>>() {
             @Override
             public void onSuccess(Response<ApiResponse<GenericDataModel<StreamModel>>> data) {
                 setData(data.body().getData().getData());
                 swipe_to_refresh.setRefreshing(false);
             }

             @Override
             public void onFailure(String failureCause) {
                 dialog.dismiss();
             }
         });
    }

    private void initViews(View view) {
        recycleView = view.findViewById(R.id.rv_events);
        tv_no_streams_running = view.findViewById(R.id.tv_no_events);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        swipe_to_refresh.setOnRefreshListener(this);
        dialog = BaseUtils.progressDialog(getContext());

    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
        ApiClient.getInstance().getInterface().getFollowingStreams().cancel();
        ApiClient.getInstance().getInterface().getGlobalStreams().cancel();
    }

    private void setData(List<StreamModel> streamModelList) {
        if (getContext() != null) {
            linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            recycleView.setLayoutManager(linearLayoutManager);
            streamsListAdapter = new StreamsListAdapter(getContext() , getActivity());
            recycleView.setAdapter(streamsListAdapter);
            recycleView.hasFixedSize();
            streamsListAdapter.setList(streamModelList);
            if (streamModelList.size() > 0) {
                tv_no_streams_running.setVisibility(View.GONE);
            }
            dialog.dismiss();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);

    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    private void setDataAndCallApi() {
        dialog.show();
        if (streamModelList != null) {
            setData(streamModelList);
        }
        if (mParam1.equals(GLOBAL_STREAMS)) {
            getStreamData();
        } else {
            getStreamFollowingData();
        }
        dialog.dismiss();
    }
    @Override
    public void onRefresh() {
        setDataAndCallApi();
    }
}