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
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class WavesPlayerPurchasedSubscriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<PurchasedSubscription> purchasedSubscriptions;
    Activity activity;
    WPAdapterOptionsListener wpAdapterOptionsListener;
    public WavesPlayerPurchasedSubscriptionAdapter(WPAdapterOptionsListener wpAdapterOptionsListener,ArrayList<PurchasedSubscription> purchasedSubscriptions, Activity activity) {
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
        this.purchasedSubscriptions = purchasedSubscriptions;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wavesplayer_purchased_subscription, parent, false);
        RecyclerView.ViewHolder holder = new Holder(itemeView);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder classHolder = (Holder) holder;
        try {
            classHolder.subscriptionTitle.setText(purchasedSubscriptions.get(position).getTitle());
            classHolder.subscriptionAmount.setText("$" + purchasedSubscriptions.get(position).getAmount());
            classHolder.btnSubscribe.setOnClickListener(v -> {
                unsubscribeSubscription(purchasedSubscriptions.get(position).getId());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return purchasedSubscriptions.size();
    }

    public void unsubscribeSubscription(int id) {
        ((HomeActivity) activity).showProgressDialog();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().unsubscribeSubscription(id), activity, new ApiResponseHandlerWithFailure<List<SubscriptionPlan>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionPlan>>> data) {
                ((HomeActivity) activity).hideProgressDialog();
                BaseUtils.showLottieDialog(activity, data.body().getMessage(), R.raw.check, positive -> {
                    wpAdapterOptionsListener.onPlaylistUpdateEvent(null);
                });
            }

            @Override
            public void onFailure(String failureCause) {
                ((HomeActivity) activity).hideProgressDialog();
                BaseUtils.showLottieDialog(activity, failureCause, R.raw.check, positive -> {
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