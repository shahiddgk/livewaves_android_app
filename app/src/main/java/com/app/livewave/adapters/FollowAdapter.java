package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    List<FollowModel> followingList;
    Context context;
    int id; // 0 for following & 1 for followers -1 is for search screen

    public FollowAdapter(Context context, int id) {
        this.context = context;
        this.followingList = new ArrayList<>();
        this.id = id;
    }

    @NonNull
    @Override
    public FollowAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.following_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(followingList.get(position).getName());
        BaseUtils.setVerifiedAccount(followingList.get(position).getVerified(), holder.txt_name);
        holder.txt_username.setText(followingList.get(position).getUsername());
        Glide.with(context).load(BaseUtils.getUrlforPicture(followingList.get(position).getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).openUserProfile(String.valueOf(followingList.get(position).getId()));
//                BaseUtils.openUserProfile(String.valueOf(followingList.get(position).getId()), context);
            }
        });
        if (id == -1) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity)context).openUserProfile(String.valueOf(followingList.get(position).getId()));
//                    BaseUtils.openUserProfile(String.valueOf(followingList.get(position).getId()), context);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return followingList.size();
    }

    public void setList(List<FollowModel> followModelList) {
        this.followingList = new ArrayList<>();
        this.followingList = followModelList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_username;
        CircleImageView img_profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_profile = itemView.findViewById(R.id.img_profile);
            txt_username = itemView.findViewById(R.id.txt_username);
        }
    }
}
