package com.app.livewave.fragments.wavesplayer;

import static com.app.livewave.utils.Constants.currentUser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.wavesplayer.WavesPlayerArtistSubscriptionAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.SubscriptionPlan;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class ArtistSubscriptionsFragment extends Fragment implements PlayerStateListener, WPAdapterOptionsListener {
    // Add RecyclerView member
    private RecyclerView recyclerView;
    ArrayList<SubscriptionPlan> artistSubscriptions = new ArrayList<>();
    WavesPlayerArtistSubscriptionAdapter wavesPlayerArtistSubscriptionAdapter;
    String artistId;
    private UserModel userModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_subscription, container, false);
        getBundleData();
        userModel = Paper.book().read(currentUser);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.yoursubscriptionitems_seeall_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        wavesPlayerArtistSubscriptionAdapter = new WavesPlayerArtistSubscriptionAdapter(this, userModel,artistSubscriptions, getActivity());
        recyclerView.setAdapter(wavesPlayerArtistSubscriptionAdapter);

        getSubscriptionPlans();

        return view;
    }

    void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle.containsKey(Constants.SPECIFIC_USER_ID)) {
            artistId = bundle.getString(Constants.SPECIFIC_USER_ID);
        }
    }

    public void getSubscriptionPlans() {
        ((HomeActivity) getContext()).showProgressDialog();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().subscriptionPlans(artistId), getActivity(), new ApiResponseHandlerWithFailure<List<SubscriptionPlan>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionPlan>>> data) {
                appData(data.body().getData());
            }

            @Override
            public void onFailure(String failureCause) {
                ((HomeActivity) getActivity()).hideProgressDialog();
            }
        });
    }

    private void appData(List<SubscriptionPlan> data) {
        artistSubscriptions.clear();
        for (int i = 0; i < data.size(); i++) {
            artistSubscriptions.add(data.get(i));
        }
        wavesPlayerArtistSubscriptionAdapter.notifyDataSetChanged();
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
        getSubscriptionPlans();
    }

    @Override
    public void onPlaylistSelectEvent(int position) {

    }

    @Override
    public void onPlaylistOptionsEvent(PlayListModel playListModel) {

    }
}
