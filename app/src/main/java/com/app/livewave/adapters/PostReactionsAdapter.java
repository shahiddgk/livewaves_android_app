package com.app.livewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostReactionsAdapter extends RecyclerView.Adapter<PostReactionsAdapter.TravelBuddyViewHolder> {
    private List<UserModel> data;
    Context context;

    public PostReactionsAdapter(List<UserModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.following_item, parent, false);
        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, int position) {
        UserModel userData = data.get(position);

        holder.txt_name.setText(userData.getName());
        BaseUtils.setVerifiedAccount(userData.getVerified(), holder.txt_name);
        holder.txt_username.setText(userData.getUsername());
        Glide.with(context).load(BaseUtils.getUrlforPicture(userData.getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).openUserProfile(userData.getId().toString());
//                BaseUtils.openUserProfile(userData.getId().toString(), context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_username;
        CircleImageView img_profile;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_profile = itemView.findViewById(R.id.img_profile);
            txt_username = itemView.findViewById(R.id.txt_username);

        }
    }
}