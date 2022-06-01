package com.app.livewave.fragments.Events;

import static com.app.livewave.utils.Constants.CHECKED_IN;
import static com.app.livewave.utils.Constants.EVENT_TIME;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.MY_EVENTS;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.USER_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.tabs.TabLayout;

public class EventsFragment extends Fragment implements PlayerStateListener {

    //    private Toolbar toolbar;
    private TabLayout tl_going_myevents;
    private ViewPager viewpager_events;
    private String userName, userId;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events, container, false);
        setHasOptionsMenu(true);
        getBundleData();
        initComponents(view);
        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_events);
//        getIntentData();
//        initComponents();
//
//
//    }

    private void getBundleData() {
//        Intent intent = getIntent();
        Bundle bundle = getArguments();
        if (bundle.containsKey(USER_ID)) {
            userId = bundle.getString(USER_ID);
            System.out.println("userIduserId");
            System.out.println(userId);
        }
        if (bundle.containsKey(USER_NAME)) {
            userName = bundle.getString(USER_NAME);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViewPager(viewpager_events);

    }

    private void initComponents(View view) {
//        toolbar = view.findViewById(R.id.toolbar);
        viewpager_events = view.findViewById(R.id.viewpager_event_list);
        tl_going_myevents = view.findViewById(R.id.tl_going_myevents);

//        CollapsingToolbarLayout collapsingToolbarLayout =
//                view.findViewById(R.id.collapsing_toolbar);
//        collapsingToolbarLayout.setTitle(getString(R.string.events));
//        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.buttercup));
//        this.setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tl_going_myevents.setupWithViewPager(viewpager_events);
    }

    private void setUpViewPager(ViewPager viewpager_event_list) {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());

        adapter.addFragment(EventListFragment.newInstance(MY_EVENTS), getActivity().getString(R.string.my_events));
        adapter.addFragment(EventListFragment.newInstance(CHECKED_IN), getActivity().getString(R.string.going));

        viewpager_event_list.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            getActivity().onBackPressed();
//            return true;
//        } else
        if (item.getItemId() == R.id.action_add_event) {
//            startActivity(new Intent(this, CreateEventFragment.class));
            Bundle bundle = new Bundle();
            bundle.putBoolean(HIDE_HEADER, false);
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_event_create, bundle);
            return true;
        } else if (item.getItemId() == R.id.action_calender) {
            showDateFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
//                        intent.putExtra(USER_NAME, userName);
//                        intent.putExtra(USER_ID, userId);
//                        startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(EVENT_TIME, BaseUtils.convertMilliSecToUTCWithCustomFormat(selection, "yyyy-MM-dd"));
                        bundle.putString(USER_NAME, userName);
                        bundle.putString(USER_ID, userId);
                        ((HomeActivity) getActivity()).loadFragment(R.string.tag_other_person_and_time_specific_events, bundle);


//                        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
//                        adapter.addFragment(EventListFragment.newInstance(DATE_TIME, BaseUtils.convertMilliSecToUTCWithCustomFormat(selection, "yyyy-MM-dd")), getString(R.string.specific_date));
//                        viewpager_events.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//
//                        tl_going_myevents.setVisibility(View.GONE);

                        materialDatePicker.dismiss();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.event_menu, menu);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.event_menu, menu);
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initComponents(view);

    }

    @Override
    public void updatePlayerState() {

    }
}