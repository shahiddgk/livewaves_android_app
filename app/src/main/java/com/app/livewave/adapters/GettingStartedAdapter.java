package com.app.livewave.adapters;

import android.annotation.SuppressLint;
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
import com.app.livewave.activities.VideoPlayerActivity;
import com.app.livewave.models.VideoModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.app.livewave.utils.Constants.VIDEO_SHARE_COUNT;
import static com.app.livewave.utils.Constants.VIDEO_VIEW_COUNT;


public class GettingStartedAdapter extends RecyclerView.Adapter<GettingStartedAdapter.MyViewHolder> {

    List<VideoModel> videoModelList;
    Context context;

    public GettingStartedAdapter(Context context) {
        this.context = context;
        this.videoModelList = new ArrayList<>();
    }

    @NonNull
    @Override
    public GettingStartedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GettingStartedAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.getting_started_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GettingStartedAdapter.MyViewHolder holder, int position) {
        holder.title.setText(videoModelList.get(position).getTitle());
        Glide.with(context).load(BaseUtils.getUrlforPicture(videoModelList.get(position).getImage())).placeholder(R.drawable.cover_place_holder).into(holder.iv_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);

                intent.putExtra(VIDEO_SHARE_COUNT,"0");

                intent.putExtra(VIDEO_VIEW_COUNT,"0");
                intent.putExtra(Constants.URL, videoModelList.get(position).getVideo());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoModelList.size();
    }

    public void setList(List<VideoModel> videoList) {
        this.videoModelList = new ArrayList<>();
        this.videoModelList = videoList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView iv_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            title = itemView.findViewById(R.id.title);
        }
    }
}