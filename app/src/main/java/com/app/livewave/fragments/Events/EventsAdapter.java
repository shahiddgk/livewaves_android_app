package com.app.livewave.fragments.Events;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;
import static com.app.livewave.utils.Constants.EVENT_ID;
import static com.app.livewave.utils.Constants.EVENT_OBJ;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.Holder> {
    private Context context;
    private List<EventModel> eventModelList = new ArrayList<>();

    public EventsAdapter(Context context, List<EventModel> eventModelList) {
        this.context = context;
        this.eventModelList = eventModelList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        EventModel eventModel = eventModelList.get(position);
        if (eventModel.getPaid().equals("1"))
            holder.img_paid.setVisibility(View.VISIBLE);
        else
            holder.img_paid.setVisibility(View.GONE);
        Glide.with(context).load(getUrlforPicture(eventModel.getAttachment())).placeholder(R.drawable.cover_place_holder).into(holder.img_event);
        Glide.with(context).load(getUrlforPicture(eventModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_user);
//        holder.tv_date.setText(BaseUtils.parseDate(eventModel.getStartAt()));
        holder.tv_event_title.setText(eventModel.getTitle());
        holder.tv_name.setText(eventModel.getName());

        if (eventModel.getType().equals("2")) {
            holder.img_stream_event.setVisibility(View.VISIBLE);
        } else {
            holder.img_stream_event.setVisibility(View.GONE);
        }
        try {


            if (eventModel.getLatitude() != null && eventModel.getLongitude() != null || eventModel.getAddress() != null) {
               // holder.tv_location.setText(eventModel.getAddress() == null ? getAddress(context, Double.parseDouble(eventModel.getLatitude()), Double.parseDouble(eventModel.getLongitude())) : eventModel.getAddress());
                holder.tv_location.setText(eventModel.getAddress());
//                holder.tv_location.setText(getAddress(context,Double.parseDouble(eventModel.getLatitude()),Double.parseDouble(eventModel.getLongitude())));
                holder.tv_location.setVisibility(View.VISIBLE);
            } else {
                holder.tv_location.setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (Constants.APPENV == ENV.review){
            holder.img_paid.setVisibility(View.GONE);
        }
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).openUserProfile(eventModel.getUserId().toString());
//                BaseUtils.openUserProfile(eventModel.getUserId().toString(), context);
            }
        });
        holder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).openUserProfile(eventModel.getUserId().toString());
//                BaseUtils.openUserProfile(eventModel.getUserId().toString(), context);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailFragment.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(holder.img_event, context.getResources().getString(R.string.event_image));
                pairs[1] = new Pair<View, String>(holder.tv_event_title, context.getResources().getString(R.string.event_title));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

//                intent.putExtra(EVENT_ID, eventModel.getId().toString());
//                intent.putExtra(EVENT_OBJ, eventModel);
//                context.startActivity(intent, options.toBundle());

                Bundle bundle = new Bundle();
                bundle.putString(EVENT_ID, eventModel.getId().toString());
                bundle.putString(EVENT_OBJ, new Gson().toJson(eventModel));
                ((HomeActivity) context).loadFragment(R.string.tag_events_detail, bundle);

//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventModelList.size();
    }

    public void setList(List<EventModel> eventList) {
        this.eventModelList = new ArrayList<>();
        this.eventModelList = eventList;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ImageView img_event;
        private final ImageView img_user;
        private ImageView  img_paid;
        MaterialCardView img_stream_event;
        private final TextView tv_name;
        //        private final TextView tv_date;
        private final TextView tv_event_title;
        private final TextView tv_location;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img_event = itemView.findViewById(R.id.img_event);
            img_user = itemView.findViewById(R.id.img_user);
            img_paid = itemView.findViewById(R.id.img_paid);
            img_stream_event = itemView.findViewById(R.id.img_stream_event);
            tv_name = itemView.findViewById(R.id.tv_name);
//            tv_date = itemView.findViewById(R.id.tv_date);
            tv_event_title = itemView.findViewById(R.id.tv_event_title);
            tv_location = itemView.findViewById(R.id.tv_location);
        }
    }
}
