package com.app.livewave.adapters.wavesplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.widgets.CircleImageView;

import java.util.ArrayList;

public class WavesPlayerMySubscriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Track> songArrayList = new ArrayList<>();
    Activity activity;
    boolean isSummarized;

    public WavesPlayerMySubscriptionAdapter(ArrayList<Track> songArrayList, Activity activity, boolean isSummarized) {
        this.songArrayList = songArrayList;
        this.activity = activity;
        this.isSummarized = isSummarized;
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
//            Glide.with(activity).load(songArrayList.get(position).getAttachment())
//                    .placeholder(R.drawable.profile_place_holder)
//                    .into(classHolder.image_song);
//            classHolder.songTitle.setText(songArrayList.get(position).getTitle());
//            classHolder.artistName.setText(songArrayList.get(position).getName());
//            classHolder.settings_icon.setOnClickListener(v -> {
//
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return isSummarized ? 3 : songArrayList.size();
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView songTitle, artistName;
        ImageView settings_icon;
        CircleImageView image_song;

        public Holder(View itemView) {
            super(itemView);
            image_song = itemView.findViewById(R.id.image_song);
            songTitle = itemView.findViewById(R.id.songTitle);
            artistName = itemView.findViewById(R.id.artistName);
            settings_icon = itemView.findViewById(R.id.settings_icon);
        }
    }
}