package com.app.livewave.fragments.Events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.EVENT_TIME;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.USER_NAME;

public class OtherPersonAndTimeSpecificEventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, PlayerStateListener {
    private RecyclerView rv_events;
    private LinearLayoutManager linearLayoutManager;
    private EventsAdapter eventsAdapter;
    private List<EventModel> eventModelList = new ArrayList<>();

    private String userId, status;
    private UserModel userModel;
    private String userName = "";
    private TextView tv_no_events, tv_date;
//    private AppBarLayout app_bar;
    private SwipeRefreshLayout swipe_to_refresh;
    private String eventTime;
    KProgressHUD dialog;
//    private Toolbar toolbar;
//    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_other_person_and_time_specific_events_fragment, container, false);
       // setHasOptionsMenu(true);
        initComponents(view);
        checkEventTypeAndSetData();
        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_other_person_and_time_specific_events_activity);
//        initComponents();
//        checkEventTypeAndSetData();
//    }

    private void initComponents(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
        rv_events = view.findViewById(R.id.rv_events);
        tv_no_events = view.findViewById(R.id.tv_no_events);
//        toolbar = view.findViewById(R.id.toolbar);
//        app_bar = view.findViewById(R.id.app_bar);
//        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
//        tv_date = findViewById(R.id.tv_date);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        eventsAdapter = new EventsAdapter(getActivity(), eventModelList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_events.setLayoutManager(linearLayoutManager);
        rv_events.setAdapter(eventsAdapter);
        swipe_to_refresh.setOnRefreshListener(this);
        dialog = BaseUtils.progressDialog(getActivity());
//        app_bar.addOnOffsetChangedListener(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onRefresh() {
        checkEventTypeAndSetData();
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
        ApiClient.getInstance().getInterface().getEvents(userId,status,eventTime).cancel();
    }

    private void getEvents() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getEvents(userId, status, eventTime), getActivity(), new ApiResponseHandler<GenericDataModel<EventModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<EventModel>>> data) {
                swipe_to_refresh.setRefreshing(false);
                if (data.body() != null && data.body().getData().getData().size() > 0) {
                    tv_no_events.setVisibility(View.GONE);
                    setData(data.body().getData().getData());
                } else {
                    dialog.dismiss();
                    tv_no_events.setVisibility(View.VISIBLE);
                }
            }
        });
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


    private void checkEventTypeAndSetData() {
        dialog.show();
//        Intent intent = getIntent();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(Constants.EVENT_TIME)) {
                eventTime = bundle.getString(Constants.EVENT_TIME);
//                tv_date.setText(eventTime);
//                collapsingToolbarLayout.setTitle(eventTime);
//                toolbar.setSubtitle(eventTime);
            }

            if (bundle.containsKey(Constants.USER_ID)) {
                userId = bundle.getString(Constants.USER_ID);

            }
            if (bundle.containsKey(Constants.USER_NAME)) {
//                collapsingToolbarLayout.setTitle(intent.getStringExtra(Constants.USER_NAME));
                userName = bundle.getString(Constants.USER_NAME);
            }
            getEvents();
        }

    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipe_to_refresh.setEnabled(verticalOffset == 0);

    }


    private void showDateFragment() {

        final MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.select_date).build();
        materialDatePicker.show(getChildFragmentManager(), "MATERIAL_DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
//                        Intent intent = new Intent(getActivity(), OtherPersonAndTimeSpecificEventsFragment.class);
//                        intent.putExtra(EVENT_TIME, BaseUtils.convertMilliSecToUTCWithCustomFormat(selection, "yyyy-MM-dd"));
//                        intent.putExtra(USER_NAME, collapsingToolbarLayout.getTitle());
//                        intent.putExtra(USER_ID, userId);
//                        startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.getString(EVENT_TIME, BaseUtils.convertMilliSecToUTCWithCustomFormat(selection, "yyyy-MM-dd"));
                        bundle.getCharSequence(USER_NAME, userName);
                        bundle.getString(USER_ID, userId);
                        ((HomeActivity) getActivity()).loadFragment(R.string.tag_other_person_and_time_specific_events, bundle);

                        materialDatePicker.dismiss();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_calender) {
            showDateFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.event_menu, menu);

        if (getArguments().containsKey(Constants.USER_ID)) {
            if (!getArguments().getString(Constants.USER_ID).equals(userModel.getId().toString())) {
                menu.findItem(R.id.action_add_event).setVisible(false);
            }
            if (getArguments().getString(Constants.USER_ID).equals(userModel.getId().toString())) {
                menu.findItem(R.id.action_add_event).setVisible(false);
                menu.findItem(R.id.action_calender).setVisible(false);
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.event_menu, menu);
//
//        if (getIntent().hasExtra(Constants.USER_ID)) {
//            if (!getIntent().getStringExtra(Constants.USER_ID).equals(userModel.getId().toString())) {
//                menu.findItem(R.id.action_add_event).setVisible(false);
//            }
//            if (getIntent().getStringExtra(Constants.USER_ID).equals(userModel.getId().toString())) {
//                menu.findItem(R.id.action_add_event).setVisible(false);
//                menu.findItem(R.id.action_calender).setVisible(false);
//            }
//        }
//        return true;
//
//    }

    @Override
    public void updatePlayerState() {

    }
}