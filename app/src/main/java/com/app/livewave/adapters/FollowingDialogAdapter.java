package com.app.livewave.adapters;

import static com.app.livewave.utils.Constants.HEADER_TITLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.FollowingDialogSheet;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingDialogAdapter extends RecyclerView.Adapter<FollowingDialogAdapter.MyViewHolder> {

    FollowingDialogSheet followingDialogSheet;
    List<FollowModel> followingList = new ArrayList<>();
    Context context;

    public FollowingDialogAdapter(Context context, FollowingDialogSheet followingDialogSheet) {
        this.context = context;
        this.followingDialogSheet = followingDialogSheet;
    }

    @NonNull
    @Override
    public FollowingDialogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowingDialogAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.following_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull FollowingDialogAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(followingList.get(position).getName());
        BaseUtils.setVerifiedAccount(followingList.get(position).getVerified(), holder.txt_name);
        holder.txt_username.setText(followingList.get(position).getUsername());
        Glide.with(context).load(BaseUtils.getUrlforPicture(followingList.get(position).getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followingDialogSheet.dismiss();
                Gson gson = new Gson();
                String myJson = gson.toJson(followingList.get(position));
//                Intent intent = new Intent(context, ChatFragment.class);
//                intent.putExtra("followModel", myJson);
//                context.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString("followModel", myJson);
                bundle.putString(HEADER_TITLE, followingList.get(position).getName());
                ((HomeActivity) context).loadFragment(R.string.tag_chat, bundle);
            }
        });

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
