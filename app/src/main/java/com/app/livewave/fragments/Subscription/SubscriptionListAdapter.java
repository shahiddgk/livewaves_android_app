package com.app.livewave.fragments.Subscription;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.RequestModels.ChangeSubscriptionStatus;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.app.livewave.utils.Constants.EDIT_SUBSCRIPTION_PLAN;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.SUBSCRIPTION_OBJ;


public class SubscriptionListAdapter extends RecyclerView.Adapter<SubscriptionListAdapter.Holder> {
    private Context context;
    private List<SubscriptionModel> eventModelList = new ArrayList<>();

    public SubscriptionListAdapter(Context context, List<SubscriptionModel> eventModelList) {
        this.context = context;
        this.eventModelList = eventModelList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subscription, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        SubscriptionModel eventModel = eventModelList.get(position);
        holder.iv_edit_subscription.setVisibility(View.VISIBLE);
        holder.iv_delete_subscription.setVisibility(View.VISIBLE);
        holder.switchMaterial.setVisibility(View.VISIBLE);
        holder.buy_button_subscription.setVisibility(View.GONE);
        String duration = "";

      holder.tv_subscription_title.setText(eventModel.getTitle());

        String date = eventModel.getCreatedAt().substring(0,10);
        System.out.println("datedate");
        System.out.println(date);

        if (eventModel.getDuration() == 1) {

            duration = "1 Day";

        }else if (eventModel.getDuration() == 2){
            duration = "1 Week";
        } else if (eventModel.getDuration() == 3){
            duration = "1 Month";
        } else if (eventModel.getDuration() == 4){
            duration = "3 Months";
        } else if (eventModel.getDuration() == 5){
            duration = "6 Months";
        } else if (eventModel.getDuration() == 6){
            duration = "1 Year";
        } else if (eventModel.getDuration() == 7){
            duration = "Lifetime";
        }

        int amount1 = (int) eventModel.getAmount();
        System.out.println(amount1);
        holder.tv_subscription_date.setText(date);
        holder.tv_amount.setText(String.valueOf(amount1)+" $");
        holder.tv_permission_duration.setText(duration);

      holder.iv_delete_subscription.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              DeleteSubscription(eventModel,position);
          }
      });

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

        holder.iv_edit_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(SUBSCRIPTION_OBJ, new Gson().toJson(eventModel));
                bundle.putBoolean(EDIT_SUBSCRIPTION_PLAN, true);
                bundle.putBoolean(HIDE_HEADER, false);
                ((HomeActivity) context).loadFragment(R.string.tag_subscription_create, bundle);
            }
        });

    //  holder.tv_subscription_date.setText(eventModel.getCreatedAt());
      if (eventModel.getTrack_access().equals("1") || eventModel.getTrack_access() == "1") {
          holder.tv_permission_tracks.setVisibility(View.VISIBLE);
      } else {
        holder.tv_permission_tracks.setVisibility(View.GONE);
      }
        if (eventModel.getPost_access().equals("1") || eventModel.getPost_access() == "1") {
            holder.tv_permission_posts.setVisibility(View.VISIBLE);
        } else {
            holder.tv_permission_posts.setVisibility(View.GONE);
        }
        if (eventModel.getEvent_access().equals("1") || eventModel.getEvent_access() == "1") {
            holder.tv_permission_events.setVisibility(View.VISIBLE);
        } else {
            holder.tv_permission_events.setVisibility(View.GONE);
        }
        if (eventModel.getLivestream_access().equals("1") || eventModel.getLivestream_access() == "1") {
            holder.subscription_live_stream.setVisibility(View.VISIBLE);
        } else {
            holder.subscription_live_stream.setVisibility(View.GONE);
        }
        if (eventModel.getTicket_access().equals("1") || eventModel.getTicket_access() == "1") {
         //   holder.tv_permission_tracks.setVisibility(View.VISIBLE);
        } else {
          //  holder.tv_permission_tracks.setVisibility(View.GONE);
        }
    }

    private void SubscritionStatus(SubscriptionModel eventModel) {
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

        ApiManager.apiCall(ApiClient.getInstance().getInterface().statusSubscriptionPlan(changeSubscriptionStatus), context, new ApiResponseHandler<Object>() {
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

    private void DeleteSubscription(SubscriptionModel eventModel,int position) {
        KProgressHUD dialog;
        dialog = BaseUtils.progressDialog(context);

        dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().deleteSubscriptionPlan(eventModel.getId()), context, new ApiResponseHandler<List<Object>>() {
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
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventModelList.size();
    }

    public void setList(List<SubscriptionModel> eventList) {
        this.eventModelList = new ArrayList<>();
        this.eventModelList = eventList;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private final TextView tv_subscription_title,subscription_live_stream,tv_permission_tracks,tv_permission_events,tv_permission_posts,tv_permission_duration,tv_amount,tv_subscription_date,buy_button_subscription;
        private final ImageView iv_edit_subscription,iv_delete_subscription;
        SwitchMaterial switchMaterial;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_subscription_title = itemView.findViewById(R.id.subscription_title_simple);
            subscription_live_stream = itemView.findViewById(R.id.subscription_live_stream);
            tv_permission_tracks = itemView.findViewById(R.id.permission_tracks);
            tv_permission_events = itemView.findViewById(R.id.permission_events);
            tv_permission_posts = itemView.findViewById(R.id.permission_posts);
            tv_permission_duration = itemView.findViewById(R.id.permission_duration);
            tv_amount = itemView.findViewById(R.id.subscription_amount);
            tv_subscription_date = itemView.findViewById(R.id.subscription_date);
            buy_button_subscription = itemView.findViewById(R.id.buy_button_subscription);
            iv_edit_subscription = itemView.findViewById(R.id.iv_edit_subscription);
            iv_delete_subscription = itemView.findViewById(R.id.iv_delete_subscription);
            switchMaterial = itemView.findViewById(R.id.switch_subscription_status);
              }
    }
}
