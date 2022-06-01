package com.app.livewave.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.fragments.Subscription.SubscriptionListAdapter;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.Constants;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

public class UserSubscriptionListAdapter extends RecyclerView.Adapter<UserSubscriptionListAdapter.Holder> {

    private Context context;
    private List<SubscriptionModel> userSubscriptionList = new ArrayList<>();
    UserModel userModel = Paper.book().read(Constants.currentUser);

    public UserSubscriptionListAdapter(Context context, List<SubscriptionModel> userSubscriptionList) {
        this.context = context;
        this.userSubscriptionList = userSubscriptionList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subscription, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.iv_edit_subscription.setVisibility(View.GONE);
        holder.iv_delete_subscription.setVisibility(View.GONE);
        holder.switchMaterial.setVisibility(View.GONE);
        holder.buy_button_subscription.setVisibility(View.VISIBLE);
        String duration = "";

        SubscriptionModel eventModel = userSubscriptionList.get(position);

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

        holder.tv_subscription_title.setText(eventModel.getTitle());
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
        holder.buy_button_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewNavigation(eventModel);
            }
        });

    }

    private void WebViewNavigation(SubscriptionModel eventModel) {
        System.out.println("SUBSCRIPTION DATA");

        int amount = (int) eventModel.getAmount();
        String subscription_id = eventModel.getId().toString();
        String subscriber_id = userModel.getId().toString();

        System.out.println(amount);
        System.out.println(subscription_id);
        System.out.println(subscriber_id);

        Intent intent = new Intent(((HomeActivity) context).getBaseContext(), WebviewActivity.class);
        intent.putExtra("type", "subscription");
        intent.putExtra("amount", String.valueOf(amount));
        intent.putExtra("subscription_id", subscription_id);
        intent.putExtra("intent_type", "9");
        context.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return userSubscriptionList.size();
    }

    public void setList(List<SubscriptionModel> eventList) {
        this.userSubscriptionList = new ArrayList<>();
        this.userSubscriptionList = eventList;
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
