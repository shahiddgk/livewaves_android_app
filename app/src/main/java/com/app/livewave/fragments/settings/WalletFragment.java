package com.app.livewave.fragments.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.R;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.fragments.ReceivedFragment;
import com.app.livewave.fragments.TransferredFragment;
import com.app.livewave.fragments.WithdrawalFragment;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import io.paperdb.Paper;

public class WalletFragment extends Fragment implements PlayerStateListener {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout app_bar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ReceivedFragment receivedFragment = new ReceivedFragment();
    TransferredFragment transferredFragment = new TransferredFragment();
    WithdrawalFragment withdrawalFragment = new WithdrawalFragment();
    UserModel userModel;
    TextView txt_balance;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        initViews(view);
        setHasOptionsMenu(true);

        return view;
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wallet);
//
//        initViews();
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Wallet");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//    }


    private void initViews(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);

        txt_balance = view.findViewById(R.id.txt_balance);
        app_bar = view.findViewById(R.id.app_bar);
        toolbar = view.findViewById(R.id.toolbar);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        txt_balance.setText("$"+userModel.getBalance());

        tabLayout = view.findViewById(R.id.tab);
        viewPager = view.findViewById(R.id.viewpager);
        setUpViewPager();
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setUpViewPager() {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(receivedFragment, getActivity().getString(R.string.received));
        adapter.addFragment(transferredFragment, getActivity().getString(R.string.transferred));
        adapter.addFragment(withdrawalFragment, getActivity().getString(R.string.withdrawal));
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