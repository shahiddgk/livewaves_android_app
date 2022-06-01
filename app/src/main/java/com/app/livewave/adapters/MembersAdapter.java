package com.app.livewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.interfaces.RemoveMemberInterface;
import com.app.livewave.models.MembersInfo;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {

    List<MembersInfo> membersInfoList;
    Context context;
    RemoveMemberInterface removeMemberInterface;
    int currentUserId;

    public MembersAdapter(Context context, Integer userId, RemoveMemberInterface removeMemberInterface) {
        this.context = context;
        this.membersInfoList = new ArrayList<>();
        this.removeMemberInterface = removeMemberInterface;
        this.currentUserId = userId;
    }

    @NonNull
    @Override
    public MembersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembersAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.members_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.MyViewHolder holder, int position) {

        if (membersInfoList.get(position).getId() ==  currentUserId){
            holder.img_remove.setVisibility(View.INVISIBLE);
        }else {
            holder.img_remove.setVisibility(View.VISIBLE);
        }
        holder.txt_name.setText(membersInfoList.get(position).name);
        Glide.with(context).load(membersInfoList.get(position).getPhoto()).placeholder(R.drawable.profile_place_holder).into(holder.img_picture);

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMemberInterface.selected(membersInfoList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return membersInfoList.size();
    }

    public void setList(List<MembersInfo> membersInfo) {
        this.membersInfoList = new ArrayList<>();
        this.membersInfoList = membersInfo;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name;
        CircleImageView img_picture;
        ImageView img_remove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_picture = itemView.findViewById(R.id.img_picture);
            img_remove = itemView.findViewById(R.id.img_remove);
        }
    }
}