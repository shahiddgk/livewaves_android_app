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
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserTagAdapter extends RecyclerView.Adapter<UserTagAdapter.Holder> {


    Context context;
    List<FollowModel> followModelList = new ArrayList<>();
    IuserSelected iuserSelected;

    public UserTagAdapter(Context context, List<FollowModel> followModelList) {
        this.context = context;
        this.followModelList = followModelList;
    }

    public void initInterface(IuserSelected iuserSelected) {
        this.iuserSelected = iuserSelected;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.tagg_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        FollowModel followModel = followModelList.get(position);
        holder.txt_name.setText(followModel.getName());
        holder.txt_username.setText(followModel.getUsername());
        Glide.with(context).load(BaseUtils.getUrlforPicture(followModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iuserSelected != null) {
                    iuserSelected.selected(followModel);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return followModelList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_name;
        ImageView img_profile;

        public Holder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_profile = itemView.findViewById(R.id.img_profile);

        }
    }

    public interface IuserSelected {
        void selected(FollowModel follow);
    }

}
