package com.app.livewave.fragments.Subscription;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.tabs.TabLayout;

import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.MY_SUBSCRIPTIONS;
import static com.app.livewave.utils.Constants.PURCHASED_SUBSCRIPTIONS;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.USER_NAME;


public class SubscriptionListFragment extends Fragment implements PlayerStateListener {

    private TabLayout tl_going_myevents;
    private ViewPager viewpager_events;
    private String userName, userId;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscription_list, container, false);

        setHasOptionsMenu(true);
        getBundleData();
        initComponents(view);

        return  view;

    }

    private void getBundleData() {
//        Intent intent = getIntent();
        Bundle bundle = getArguments();
        if (bundle.containsKey(USER_ID)) {
            userId = bundle.getString(USER_ID);
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
        viewpager_events = view.findViewById(R.id.viewpager_subscription_plan);
        tl_going_myevents = view.findViewById(R.id.tl_purchased_subscription);

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

        adapter.addFragment(SubscriptionFragment.newInstance(MY_SUBSCRIPTIONS), getActivity().getString(R.string.my_subscription));
        adapter.addFragment(SubscriptionFragment.newInstance(PURCHASED_SUBSCRIPTIONS), getActivity().getString(R.string.purchased_subscription));

        viewpager_event_list.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            getActivity().onBackPressed();
//            return true;
//        } else
        if (item.getItemId() == R.id.add_subscription_plan) {
//            startActivity(new Intent(this, CreateEventFragment.class));
            Bundle bundle = new Bundle();
            bundle.putBoolean(HIDE_HEADER, false);
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_subscription_create, bundle);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.subscription_list_menu, menu);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initComponents(view);

    }


    @Override
    public void updatePlayerState() {

    }
}