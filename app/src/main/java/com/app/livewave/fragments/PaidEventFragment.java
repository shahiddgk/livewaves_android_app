package com.app.livewave.fragments;

import static com.app.livewave.utils.Constants.CHECKED_IN;
import static com.app.livewave.utils.Constants.DATE_TIME;
import static com.app.livewave.utils.Constants.MY_EVENTS;
import static com.app.livewave.utils.Constants.NEWS_FEED;
import static com.app.livewave.utils.Constants.USER_ID;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.adapters.PostAdapter;
import com.app.livewave.fragments.Events.EventListFragment;
import com.app.livewave.fragments.Events.EventsAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
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


public class PaidEventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView rv_events;
    private LinearLayoutManager linearLayoutManager;
    private EventsAdapter eventsAdapter;
    private List<EventModel> eventModelList = new ArrayList<>();
    private SwipeRefreshLayout swipe_to_refresh;
    KProgressHUD dialog;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_paid_event, container, false);

        initComponents(view);
        getEvents();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().getInterface().getPaidEvents().cancel();
    }

    private void getEvents() {
        swipe_to_refresh.setRefreshing(false);
        dialog.show();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getPaidEvents(), getContext(), new ApiResponseHandlerWithFailure<GenericDataModel<EventModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<EventModel>>> data) {
                dialog.dismiss();
                if(!(data.body().getData().getData() == null)){
                    if (data.body() != null && data.body().getData().getData().size() > 0) {
                        setData(data.body().getData().getData());
                    } else {
                        eventModelList = new ArrayList<>();
                        eventsAdapter.setList(eventModelList);
                    }
                }
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });
    }

    private void setData(List<EventModel> data) {
        if (data.size() > 0) {
            eventModelList.clear();
            eventModelList.addAll(data);
            eventsAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }

    }


    private void initComponents(View view) {
        dialog = BaseUtils.progressDialog(getContext());
        rv_events = view.findViewById(R.id.rv_events);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        eventsAdapter = new EventsAdapter(context, eventModelList);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv_events.setLayoutManager(linearLayoutManager);
        rv_events.setAdapter(eventsAdapter);
        swipe_to_refresh.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getEvents();
    }

}