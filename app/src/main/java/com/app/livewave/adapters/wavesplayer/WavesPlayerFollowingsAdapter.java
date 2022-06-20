package com.app.livewave.adapters.wavesplayer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.wavesplayer.CreatePlaylistDialog;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.WPAdapterOptionsListener;
import com.app.livewave.models.ParameterModels.HashtagsModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.retrofit.ApiInterface;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.wavesplayer.playback.PlayerState;
import com.app.livewave.widgets.CircleImageView;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class WavesPlayerFollowingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Track> songArrayList;
    Activity activity;
    Context context;
    boolean isSummarized;
    int viewId;
    WPAdapterOptionsListener wpAdapterOptionsListener;
    Integer id;
    Integer count;

    public WavesPlayerFollowingsAdapter(WPAdapterOptionsListener wpAdapterOptionsListener, ArrayList<Track> songArrayList,
                                        Activity activity,
                                        int viewId,
                                        boolean isSummarized) {
        this.wpAdapterOptionsListener = wpAdapterOptionsListener;
        this.songArrayList = songArrayList;
        this.activity = activity;
        this.viewId = viewId;
        this.isSummarized = isSummarized;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemeView = LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false);
        RecyclerView.ViewHolder holder = new Holder(itemeView);
        holder.setIsRecyclable(false);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        final Holder classHolder = (Holder) holder;
         id = Paper.book().read("CurrentUserId");
        try {
            classHolder.songTitle.setText(songArrayList.get(position).getTitle());
            classHolder.artistName.setText(songArrayList.get(position).getName());
            classHolder.uploadedBy.setText(songArrayList.get(position).getArtist_name());
            classHolder.songPricing.setText(songArrayList.get(position).getAmount() == null || songArrayList.get(position).getAmount() == "0" || songArrayList.get(position).getAmount().equals("0") ? "Free" : "$" + songArrayList.get(position).getAmount());
            System.out.println("TOTAL PRICE FOR FOLLOWING");
            System.out.println(songArrayList.get(position).getAmount());
            count = songArrayList.get(position).getTotalPlay();
            if (count != 0) {
                classHolder.linearLayout.setVisibility(View.VISIBLE);

                classHolder.songView.setText(count.toString());

            } else {

                classHolder.linearLayout.setVisibility(View.GONE);

            }

            classHolder.settings_icon.setOnClickListener(v -> {
                System.out.println(songArrayList.get(position).getAmount());

               // if (songArrayList.get(position).getAmount().equals("0")) {
                    wpAdapterOptionsListener.onTrackOptionsEvent(songArrayList.get(position), false);
//                }else if(songArrayList.get(position).getAmount()!="0"){
//                    System.out.println(songArrayList.get(position).getAmount());
//
//                    Intent intent = new Intent(v.getContext(), WebviewActivity.class);
//                    intent.putExtra("id", "track_id=" + songArrayList.get(position).getId());
//                    intent.putExtra("type", "tip");
//                    intent.putExtra("amount", songArrayList.get(position).getAmount());
//                    intent.putExtra("intent_type", "7");
//                    v.getContext().startActivity(intent);
//
//                    wpAdapterOptionsListener.onTrackOptionsEvent(songArrayList.get(position), false);
//                }
            });
            Glide.with(activity).load(songArrayList.get(position).getAttachment())
                    .placeholder(R.drawable.profile_place_holder)
                    .into(classHolder.image_song);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (((HomeActivity) activity).getCurrentPlayingSong() != null) {
            if (songArrayList.get(position).getId() == ((HomeActivity) activity).getCurrentPlayingSong().getId()) {
                classHolder.playPause.setImageResource(songArrayList.get(position).isPlaying() == PlayerState.PLAYING.ordinal() ?
                        R.drawable.ic_pause_filled : songArrayList.get(position).isPlaying() == PlayerState.PREPARING.ordinal() ?
                        R.drawable.ic_baseline_sync_24 : R.drawable.ic_play_filled);
            }
        }

        classHolder.playPause.setOnClickListener(v -> {
            Log.d("Play Button Click:", "Clicked in Adapter");

            if (classHolder.linearLayout.getVisibility() == View.GONE) {
                classHolder.linearLayout.setVisibility(View.VISIBLE);
            }

            addCount(songArrayList.get(position).getId());

            ((HomeActivity) activity).playOrResumeSong(songArrayList, songArrayList.get(position));
        });
    }

    private void addCount(Integer trackId) {

        ApiManager.apiCall(ApiClient.getInstance().getInterface().addCountToTracks(id,trackId), context, new ApiResponseHandler<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                System.out.println("Track count added");
                System.out.println(data.body().getMessage());
                if (data.body().getMessage().equals("Play count updated Successfully")) {
                    count = count+1;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return isSummarized ? songArrayList.size() > 3 ? 3 : songArrayList.size() : songArrayList.size();
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView songTitle, artistName, songPricing, uploadedBy,songView;
        ImageView settings_icon, image_song;
        ImageButton playPause;
        LinearLayout linearLayout;

        public Holder(View itemView) {
            super(itemView);
            image_song = itemView.findViewById(R.id.image_song);
            playPause = itemView.findViewById(R.id.buttonPlayPause);
            songTitle = itemView.findViewById(R.id.songTitle);
            artistName = itemView.findViewById(R.id.artistName);
            uploadedBy = itemView.findViewById(R.id.uploadedBy);
            songPricing = itemView.findViewById(R.id.flag_free);
            linearLayout = itemView.findViewById(R.id.song_view_laylout);
            songView = itemView.findViewById(R.id.song_view_text);
            settings_icon = itemView.findViewById(R.id.settings_icon_following);
        }
    }
}

