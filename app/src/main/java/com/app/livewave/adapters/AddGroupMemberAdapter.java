package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.models.InboxModel;
import com.app.livewave.models.MembersInfo;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.MyViewHolder> {

    List<FollowModel> followingList = new ArrayList<>();
    ArrayList<FollowModel> selectedMembers = new ArrayList<>();
    List<Integer> currentMembers = new ArrayList<>();
    List<MembersInfo> currentMembersInfoList = new ArrayList<>();
    Context context;

    public AddGroupMemberAdapter(Context context, InboxModel inboxModel) {
        this.context = context;
        this.currentMembers = inboxModel.getMembers();
        this.currentMembersInfoList = inboxModel.getMembersInfo();
        for (int i = 0; i < inboxModel.getMembersInfo().size(); i++) {
            FollowModel followModel = new FollowModel();
            followModel.setId(inboxModel.getMembersInfo().get(i).getId());
            followModel.setName(inboxModel.getMembersInfo().get(i).getName());
            followModel.setPhoto(inboxModel.getMembersInfo().get(i).getPhoto());
            followModel.setUsername(inboxModel.getMembersInfo().get(i).getUsername());
            selectedMembers.add(followModel);
        }
    }

    @NonNull
    @Override
    public AddGroupMemberAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddGroupMemberAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.select_member_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull AddGroupMemberAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (currentMembers.size() > 0) {
            if (currentMembers.contains(followingList.get(position).getId())) {
                followingList.get(position).setSelected(true);
            }
        }
        if (followingList.get(position).isSelected()) {
            holder.img_selected.setVisibility(View.VISIBLE);
        } else {
            holder.img_selected.setVisibility(View.GONE);
        }

        holder.txt_name.setText(followingList.get(position).getName());
        BaseUtils.setVerifiedAccount(followingList.get(position).getVerified(), holder.txt_name);
        holder.txt_username.setText(followingList.get(position).getUsername());
        Glide.with(context).load(BaseUtils.getUrlforPicture(followingList.get(position).getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followingList.get(position).isSelected()) {
                    followingList.get(position).setSelected(false);
                    for (int i = 0; i < selectedMembers.size(); i++) {
                        if (selectedMembers.get(i).getId() == followingList.get(position).getId()) {
                            selectedMembers.remove(i);
                        }
                    }
                } else {
                    followingList.get(position).setSelected(true);
                    selectedMembers.add(followingList.get(position));
                }
                notifyDataSetChanged();
            }
        });

    }

    public ArrayList<FollowModel> getSelected() {
        return selectedMembers;
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
        ImageView img_selected;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_profile = itemView.findViewById(R.id.img_profile);
            txt_username = itemView.findViewById(R.id.txt_username);
            img_selected = itemView.findViewById(R.id.img_selected);
        }
    }
}
