package com.app.livewave.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.AlertModelNew;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements PlayerStateListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        setViews(view);
        return view;
    }

    void setViews(View view) {

        BottomNavigationView navView = view.findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getNotificationsCount(), getContext(),
                new ApiResponseHandlerWithFailure<Object>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<Object>> data) {
                        if (data != null) {
                            System.out.println("NOTIFICATION_COUNT");
                            //  System.out.println(data);
                           double count = ((Double) data.body().getData()).doubleValue();
                           int noti_count = (int) count;
                            System.out.println(noti_count);
                            if (noti_count != 0) {
                                  navView.getOrCreateBadge(R.id.navigation_alert).setNumber(noti_count);
                            }else {
                                navView.getOrCreateBadge(R.id.navigation_alert).setNumber(0);
                                navView.removeBadge(R.id.navigation_alert);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String failureCause) {
                        BaseUtils.showLottieDialog(getContext(), failureCause, R.raw.invalid, positive -> {
                        });

                    }
                });
        navView.getMenu().getItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                                    @Override
                                                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                                                        System.out.println(menuItem.getItemId());
                                                                        System.out.println("MENU ITEM CLICKED");
                                                                        navView.getOrCreateBadge(R.id.navigation_alert).setNumber(0);
                                                                        navView.removeBadge(R.id.navigation_alert);
                                                                        return false;
                                                                    }
                                                                }
        );


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_news_feed,
                R.id.navigation_live, R.id.navigation_alert,
                R.id.navigation_settings)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navCo = navHostFragment.getNavController();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navCo);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setUpPlayerPosition(true);

    }


    @Override
    public void onStop() {
        super.onStop();
        ((HomeActivity) getActivity()).setUpPlayerPosition(false);
    }

    @Override
    public void updatePlayerState() {
    }
}