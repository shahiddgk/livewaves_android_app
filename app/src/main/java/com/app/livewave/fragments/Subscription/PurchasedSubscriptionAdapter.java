package com.app.livewave.fragments.Subscription;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.RequestModels.ChangeSubscriptionStatus;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PurchasedSubscriptionModel;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PurchasedSubscriptionAdapter extends RecyclerView.Adapter<PurchasedSubscriptionAdapter.Holder> {
    private Context context;
    private List<PurchasedSubscriptionModel> eventModelList = new ArrayList<>();

    public PurchasedSubscriptionAdapter(Context context, List<PurchasedSubscriptionModel> eventModelList) {
        this.context = context;
        this.eventModelList = eventModelList;
    }

    @NonNull
    @Override
    public PurchasedSubscriptionAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchased_subscrition, parent, false);
        return new PurchasedSubscriptionAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasedSubscriptionAdapter.Holder holder, int position) {

        PurchasedSubscriptionModel eventModel = eventModelList.get(position);
        holder.tv_subscription_title.setText(eventModel.getTitle());
        System.out.println("TITLETITLE");
        System.out.println(eventModel.getTitle());

        String date = eventModel.getCreatedAt().substring(0,10);
        System.out.println("datedate");
        System.out.println(date);

        holder.tv_subscription_date.setText(date);

        if (eventModel.getStatus().equals("0") || eventModel.getStatus() == "0") {
            holder.switchMaterial.setChecked(true);
        }else {
            holder.switchMaterial.setChecked(false);
        }

        holder.switchMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventModel.getStatus() == "0" || eventModel.getStatus().equals("0")) {
                    holder.switchMaterial.setChecked(false);
                }else {
                    holder.switchMaterial.setChecked(true);
                }
                SubscritionStatus(eventModel);
            }
        });

        holder.iv_delete_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteSubscription(eventModel,position);
            }
        });

     //   holder.tv_subscription_date.setText(eventModel.getCreatedAt());

    }

    private void SubscritionStatus(PurchasedSubscriptionModel eventModel) {
        ChangeSubscriptionStatus changeSubscriptionStatus = new ChangeSubscriptionStatus();
        changeSubscriptionStatus.setId(eventModel.getId());
        if (eventModel.getStatus() == "0" || eventModel.getStatus().equals("0")) {
            changeSubscriptionStatus.setStatus("1");
            eventModel.setStatus("1");
        }else {
            changeSubscriptionStatus.setStatus("0");
            eventModel.setStatus("0");
        }

        KProgressHUD dialog;
        dialog = BaseUtils.progressDialog(context);

        dialog.show();

        ApiManager.apiCall(ApiClient.getInstance().getInterface().statusPurchasedSubscriptionPlan(changeSubscriptionStatus), context, new ApiResponseHandler<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(context, "Subscription Status Changed Successfully", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
//                        finish();
                    }
                });
                notifyDataSetChanged();
            }
        });

    }

    private void DeleteSubscription(PurchasedSubscriptionModel eventModel,int position) {
            KProgressHUD dialog;
            dialog = BaseUtils.progressDialog(context);

            dialog.show();
            ApiManager.apiCall(ApiClient.getInstance().getInterface().deletePurchasedSubscriptionPlan(eventModel.getId()), context, new ApiResponseHandler<List<Object>>() {
                @Override
                public void onSuccess(Response<ApiResponse<List<Object>>> data) {
                    dialog.dismiss();
                    eventModelList.remove(position);
                    BaseUtils.showLottieDialog(context, "Subscription Deleted Successfully", R.raw.check, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {
//                        finish();
                        }
                    });

//                    eventModelList.notify();
                    notifyDataSetChanged();
                }
            });



    }



    @Override
    public int getItemCount() {
        return eventModelList.size();
    }

    public void setList(List<PurchasedSubscriptionModel> eventList) {
        this.eventModelList = new ArrayList<>();
        this.eventModelList = eventList;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private final TextView tv_subscription_title,tv_subscription_date;
        private final ImageView iv_delete_subscription;
        SwitchMaterial switchMaterial;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_subscription_title = itemView.findViewById(R.id.subscription_title);
            tv_subscription_date = itemView.findViewById(R.id.subscription_date);
            switchMaterial = itemView.findViewById(R.id.switch_subscription_status);
            iv_delete_subscription = itemView.findViewById(R.id.iv_delete_subscription);
        }
    }
}
