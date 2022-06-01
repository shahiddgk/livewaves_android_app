package com.app.livewave.adapters.wavesplayer;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.wavesplayer.playback.PlayerState;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class WavesPlayerUSerPlayStoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Track> songArrayList;
    Activity activity;
    boolean isSummarized;


    public WavesPlayerUSerPlayStoreAdapter(ArrayList<Track> songArrayList, Activity activity, boolean isSummarized) {
        this.songArrayList = songArrayList;
        this.activity = activity;
        this.isSummarized = isSummarized;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waves_player_user_paly_store, parent, false);
        RecyclerView.ViewHolder holder = new WavesPlayerUSerPlayStoreAdapter.Holder(itemeView, activity);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final WavesPlayerUSerPlayStoreAdapter.Holder classHolder = (WavesPlayerUSerPlayStoreAdapter.Holder) holder;
        try {
            classHolder.songTitle.setText(songArrayList.get(position).getTitle());
            classHolder.artistName.setText(songArrayList.get(position).getArtist_name());
            classHolder.uploadedBy.setText(songArrayList.get(position).getArtist_name());
            classHolder.songLikes.setText(songArrayList.get(position).getStatus());
            classHolder.freeorpaid.setText(Integer.parseInt(songArrayList.get(position).getPaid()) == 0 ? "Free" : songArrayList.get(position).getAmount() + "$");
            classHolder.isPaid = position % 2 == 0 ? false : true;

//            classHolder.settings_icon.setOnClickListener(v -> {
//                optionsListener.onTrackOptionsEvent(songArrayList.get(position), true);
//            });

            if (((HomeActivity) activity).getCurrentPlayingSong() != null) {
                if (songArrayList.get(position).getId() == ((HomeActivity) activity).getCurrentPlayingSong().getId()) {
                    classHolder.playPause.setImageResource(songArrayList.get(position).isPlaying() == PlayerState.PLAYING.ordinal() ?
                            R.drawable.ic_pause_filled : songArrayList.get(position).isPlaying() == PlayerState.PREPARING.ordinal() ?
                            R.drawable.ic_baseline_sync_24 : R.drawable.ic_play_filled);
                }
            }

            classHolder.playPause.setOnClickListener(v -> {
                Log.d("Play Button Click:", "Clicked in Adapter");
                ((HomeActivity) activity).playOrResumeSong(songArrayList, songArrayList.get(position));
            });

            Glide.with(activity).load(songArrayList.get(position).getAttachment()).placeholder(R.drawable.profile_place_holder).into(classHolder.image_song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return isSummarized ? songArrayList.size() > 3 ? 3 : songArrayList.size() : songArrayList.size();//songArrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView freeorpaid, songTitle, artistName, songLikes, uploadedBy;
        ImageView settings_icon, image_song;
        ImageButton playPause;
        Activity activity;
        boolean isPaid;


        public Holder(View itemView, Activity activity) {
            super(itemView);
            this.activity = activity;

            image_song = itemView.findViewById(R.id.image_song);
            songTitle = itemView.findViewById(R.id.songTitle);
            artistName = itemView.findViewById(R.id.artistName);
            songLikes = itemView.findViewById(R.id.songLikes);
            uploadedBy = itemView.findViewById(R.id.uploadedBy);
            settings_icon = itemView.findViewById(R.id.settings_icon);
            playPause = itemView.findViewById(R.id.buttonPlayPause);
            freeorpaid = itemView.findViewById(R.id.freeorpaid);
        }
    }

}
