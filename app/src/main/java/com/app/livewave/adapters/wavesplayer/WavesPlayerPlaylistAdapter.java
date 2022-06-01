package com.app.livewave.adapters.wavesplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import retrofit2.Response;

public class WavesPlayerPlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<PlayListModel> playlists = new ArrayList<>();
    Activity activity;
    boolean isSummarized;
    int viewId;
    WPAdapterOptionsListener wpAdapterOptionsListener;

    public WavesPlayerPlaylistAdapter(WPAdapterOptionsListener wpAdapterOptionsListener, ArrayList<PlayListModel> playlists, Activity activity, int viewId, boolean isSummarized) {
        this.playlists = playlists;
        this.activity = activity;
        this.viewId = viewId;
        this.isSummarized = isSummarized;
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemeView = LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
        RecyclerView.ViewHolder holder = new Holder(itemeView);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder classHolder = (Holder) holder;
        try {

            classHolder.playlistTitle.setText(playlists.get(position).getTitle());
            classHolder.playListCount.setText(playlists.get(position).getTracks().size() + "");

            classHolder.playlist_base_view.setOnClickListener(v -> {
                wpAdapterOptionsListener.onPlaylistSelectEvent(position);
            });

            classHolder.settings_playlist.setOnClickListener(v -> {
                wpAdapterOptionsListener.onPlaylistOptionsEvent(playlists.get(position));
            });

            Glide.with(activity).load(playlists.get(position).getPlaylistThumbnail())
                    .placeholder(R.drawable.profile_place_holder)
                    .into(classHolder.image_song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return isSummarized ? playlists.size() : playlists.size();
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView playlistTitle, playListCount;
        ImageView settings_playlist, image_song;
        Activity activity;
        RelativeLayout playlist_base_view;

        public Holder(View itemView) {
            super(itemView);
            image_song = itemView.findViewById(R.id.image_song);
            playlistTitle = itemView.findViewById(R.id.playlistTitle);
            playListCount = itemView.findViewById(R.id.playlistCount);
            playlist_base_view = itemView.findViewById(R.id.playlist_base_view);
            settings_playlist = itemView.findViewById(R.id.settings_icon);
        }
    }
}