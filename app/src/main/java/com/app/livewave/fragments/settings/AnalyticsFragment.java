package com.app.livewave.fragments.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.livewave.R;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.fragments.EarningFragment;
import com.app.livewave.fragments.ProfileFragment;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import io.paperdb.Paper;

public class AnalyticsFragment extends Fragment implements PlayerStateListener {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout app_bar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ProfileFragment profileFragment = new ProfileFragment();
    EarningFragment earningFragment = new EarningFragment();
    UserModel userModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        initViews(view);
        setHasOptionsMenu(true);

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_analytics);
//        initViews();
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Analytics");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//    }

    private void initViews(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
        app_bar = view.findViewById(R.id.app_bar);
        toolbar = view.findViewById(R.id.toolbar);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);

        tabLayout = view.findViewById(R.id.tab);
        viewPager = view.findViewById(R.id.viewpager);
        setUpViewPager();
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager() {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(profileFragment, getActivity().getString(R.string.profile));
        adapter.addFragment(earningFragment, getActivity().getString(R.string.earning));
        viewPager.setAdapter(adapter);
    }
    @SuppressLint("NonConstantResourceId")
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