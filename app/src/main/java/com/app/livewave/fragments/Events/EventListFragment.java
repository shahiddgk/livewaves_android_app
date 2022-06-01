package com.app.livewave.fragments.Events;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
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

import static com.app.livewave.utils.Constants.CHECKED_IN;
import static com.app.livewave.utils.Constants.DATE_TIME;
import static com.app.livewave.utils.Constants.MY_EVENTS;
import static com.app.livewave.utils.Constants.USER_ID;

public class EventListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private RecyclerView rv_events;
    private LinearLayoutManager linearLayoutManager;
    private EventsAdapter eventsAdapter;
    private List<EventModel> eventModelList = new ArrayList<>();

    private String userId, dateTime, status;
    private Context context;
    private UserModel userModel;
    private TextView tv_no_events;

    private SwipeRefreshLayout swipe_to_refresh;
    KProgressHUD dialog;

    public EventListFragment() {
        // Required empty public constructor
    }


    public static EventListFragment newInstance(String param1) {
        EventListFragment fragment = new EventListFragment();
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
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        initComponents(view);
        getIntentData();
        checkEventTypeAndSetData();
        return view;
    }

    private void getIntentData() {
        if (requireActivity().getIntent().hasExtra(USER_ID)) {
            userId = requireActivity().getIntent().getStringExtra(USER_ID);
        }
        if (mParam1.equals(CHECKED_IN)) {
            status = mParam1;
        }
    }

    private void checkEventTypeAndSetData() {
        dialog.show();
        //If the user click on other person event it will not get other person checked in events
        if (userModel != null && userModel.getId() != null) {
            if (userModel.getId().toString().equals(userId)) {
                getEvents();
            } else if (mParam1.equals(MY_EVENTS)) {
                getEvents();
            } else if (mParam1.equals(DATE_TIME)) {
                getEvents();
            } else if (mParam1.equals(CHECKED_IN)) {
                getEvents();
            }
        } else if (mParam1.equals(MY_EVENTS)) {
            getEvents();
        } else if (mParam1.equals(DATE_TIME)) {
            getEvents();
        } else if (mParam1.equals(CHECKED_IN)) {
            getEvents();
        }
    }

    private void setData(List<EventModel> data) {
        if (data.size() > 0) {
            eventModelList.clear();
            tv_no_events.setVisibility(View.GONE);
            eventModelList.addAll(data);
            eventsAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }

    }

    private void getEvents() {
        userId = userModel.getId().toString();
        System.out.println("userIduserIduserId");
        System.out.println(userId);
        if (status != null && status.equals(CHECKED_IN)) {
            userId = null;
        }

//        ApiManager.apiCall(ApiClient.getInstance().getInterface().getEvents(userId, status, dateTime), getActivity(), new ApiResponseHandler<GenericDataModel<EventModel>>() {
//            @Override
//            public void onSuccess(Response<ApiResponse<GenericDataModel<EventModel>>> data) {
//                swipe_to_refresh.setRefreshing(false);
//                if (data.body() != null && data.body().getData().getData().size() > 0) {
//                    tv_no_events.setVisibility(View.GONE);
//                    setData(data.body().getData().getData());
//                } else {
//                    dialog.dismiss();
//                    tv_no_events.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getEvents(userId, status, dateTime), getContext(), new ApiResponseHandlerWithFailure<GenericDataModel<EventModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<EventModel>>> data) {
                swipe_to_refresh.setRefreshing(false);
                if (data.body() != null && data.body().getData().getData().size() > 0) {
                    tv_no_events.setVisibility(View.GONE);
                    setData(data.body().getData().getData());
                } else {
                    eventModelList = new ArrayList<>();
                    eventsAdapter.setList(eventModelList);
                    tv_no_events.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });
    }


    private void initComponents(View view) {
        Paper.init(context);
        userModel = Paper.book().read(Constants.currentUser);
        dialog = BaseUtils.progressDialog(getContext());
        rv_events = view.findViewById(R.id.rv_events);
        tv_no_events = view.findViewById(R.id.tv_no_events);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        eventsAdapter = new EventsAdapter(context, eventModelList);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv_events.setLayoutManager(linearLayoutManager);
        rv_events.setAdapter(eventsAdapter);
        swipe_to_refresh.setOnRefreshListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onRefresh() {
        checkEventTypeAndSetData();
    }

    @Override
    public void onDestroy() {
        ApiClient.getInstance().getInterface().getEvents(userId,status,dateTime).cancel();
        dialog.dismiss();
        super.onDestroy();
    }
}