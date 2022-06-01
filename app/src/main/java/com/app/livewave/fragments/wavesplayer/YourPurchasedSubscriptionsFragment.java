package com.app.livewave.fragments.wavesplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.wavesplayer.WavesPlayerPurchasedSubscriptionAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.PurchasedSubscription;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class YourPurchasedSubscriptionsFragment extends Fragment implements PlayerStateListener, WPAdapterOptionsListener {
    // Add RecyclerView member
    private RecyclerView recyclerView;
    ArrayList<PurchasedSubscription> purchasedSubscriptions = new ArrayList<>();
    WavesPlayerPurchasedSubscriptionAdapter wavesPlayerPurchasedSubscriptionAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_subscription, container, false);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.yoursubscriptionitems_seeall_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        wavesPlayerPurchasedSubscriptionAdapter = new WavesPlayerPurchasedSubscriptionAdapter(this, purchasedSubscriptions, getActivity());
        recyclerView.setAdapter(wavesPlayerPurchasedSubscriptionAdapter);

        getPurchasedSubscriptions();

        return view;
    }

    public void getPurchasedSubscriptions() {
        ((HomeActivity) getActivity()).showProgressDialog();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getPurchasedSubscriptions(), getActivity(), new ApiResponseHandlerWithFailure<List<PurchasedSubscription>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<PurchasedSubscription>>> data) {
                ((HomeActivity) getActivity()).hideProgressDialog();
                appData(data.body().getData());
            }

            @Override
            public void onFailure(String failureCause) {
                ((HomeActivity) getActivity()).hideProgressDialog();
//                BaseUtils.showLottieDialog(getActivity(), failureCause, R.raw.invalid, positive -> {
//                });
            }
        });
    }

    private void appData(List<PurchasedSubscription> data) {
        purchasedSubscriptions.clear();
        for (int i = 0; i < data.size(); i++) {
            purchasedSubscriptions.add(data.get(i));
        }
        wavesPlayerPurchasedSubscriptionAdapter.notifyDataSetChanged();
        ((HomeActivity) getActivity()).hideProgressDialog();
    }

    @Override
    public void updatePlayerState() {

    }

    @Override
    public void onTrackOptionsEvent(Track track, boolean isOwner) {

    }

    @Override
    public void onTrackListUpdateEvent() {

    }

    @Override
    public void onPlaylistUpdateEvent(PlayListModel playListModel) {
        getPurchasedSubscriptions();
    }

    @Override
    public void onPlaylistSelectEvent(int position) {

    }

    @Override
    public void onPlaylistOptionsEvent(PlayListModel playListModel) {

    }
}
