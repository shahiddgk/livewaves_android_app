package com.app.livewave.fragments.newsfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.livewave.R;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.fragments.PaidEventFragment;
import com.app.livewave.fragments.PaidPostFragment;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

public class PaidEventAndPaidPostFragment extends Fragment implements PlayerStateListener {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout app_bar;
    PaidEventFragment paidEventFragment = new PaidEventFragment();
    PaidPostFragment paidPostFragment = new PaidPostFragment();
    TabLayout tabLayout;
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paid_event_and_paid_post, container, false);
        setHasOptionsMenu(true);
        initView(view);
//        setToolbar();
        setUpViewPager();

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_paid_event_and_paid_post);
//
//        initView();
//        setToolbar();
//        setUpViewPager();
//    }

    private void setUpViewPager() {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(paidPostFragment, "Posts");
        adapter.addFragment(paidEventFragment, "Events");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }

//    private void setToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Paid");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//    }

    private void initView(View view) {
        app_bar = view.findViewById(R.id.app_bar);
        toolbar = view.findViewById(R.id.toolbar);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        tabLayout = view.findViewById(R.id.tab);
        viewPager = view.findViewById(R.id.viewpager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updatePlayerState() {

    }
}