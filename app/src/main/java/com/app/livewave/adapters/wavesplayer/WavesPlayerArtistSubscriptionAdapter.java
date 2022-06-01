package com.app.livewave.adapters.wavesplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PurchasedSubscription;
import com.app.livewave.models.ResponseModels.SubscriptionPlan;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class WavesPlayerArtistSubscriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SubscriptionPlan> subscriptionOffersList;
    Activity activity;
    WPAdapterOptionsListener wpAdapterOptionsListener;
    private UserModel userModel;

    public WavesPlayerArtistSubscriptionAdapter(WPAdapterOptionsListener wpAdapterOptionsListener, UserModel userModel,ArrayList<SubscriptionPlan> subscriptionOffersList, Activity activity) {
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
        this.subscriptionOffersList = subscriptionOffersList;
        this.activity = activity;
        this.userModel = userModel;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wavesplayer_artist_subscription, parent, false);
        RecyclerView.ViewHolder holder = new Holder(itemeView);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder classHolder = (Holder) holder;
        try {
            classHolder.subscriptionTitle.setText(subscriptionOffersList.get(position).getTitle());
            classHolder.subscriptionAmount.setText("$" + subscriptionOffersList.get(position).getAmount());
            classHolder.btnSubscribe.setOnClickListener(v -> {
                subscribeSubscription(subscriptionOffersList.get(position).getId(), userModel.getId());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return subscriptionOffersList.size();
    }


    public void subscribeSubscription(int sid, int uid) {
        ((HomeActivity) activity).showProgressDialog();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().subscribeSubscription(sid, uid),
                activity, new ApiResponseHandlerWithFailure<List<SubscriptionPlan>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionPlan>>> data) {
                ((HomeActivity) activity).hideProgressDialog();
                BaseUtils.showLottieDialog(activity, data.body().getMessage(), R.raw.check, positive -> {
                    //appFollowingData(data.body().getData());
                    wpAdapterOptionsListener.onPlaylistUpdateEvent(null);
                });
            }

            @Override
            public void onFailure(String failureCause) {
                ((HomeActivity) activity).hideProgressDialog();
                BaseUtils.showLottieDialog(activity, failureCause, R.raw.check, positive -> {
                    //appFollowingData(data.body().getData());
                    wpAdapterOptionsListener.onPlaylistUpdateEvent(null);
                });
            }
        });
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView subscriptionTitle, subscriptionAmount;
        MaterialButton btnSubscribe;

        public Holder(View itemView) {
            super(itemView);
            subscriptionTitle = itemView.findViewById(R.id.subscriptionTitle);
            subscriptionAmount = itemView.findViewById(R.id.subscriptionAmount);
            btnSubscribe = itemView.findViewById(R.id.btnSubscribe);
        }
    }
}