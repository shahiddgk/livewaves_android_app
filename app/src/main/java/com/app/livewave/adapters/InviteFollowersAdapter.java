package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.InviteUserDialogSheet;
import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.InviteToStreamEvent;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;

public class InviteFollowersAdapter extends RecyclerView.Adapter<InviteFollowersAdapter.MyViewHolder> {

    List<FollowModel> followingList = new ArrayList<>();
    Context context;
    int streamId;
    boolean isEvent;
    InviteUserDialogSheet inviteUserDialogSheet;

    public InviteFollowersAdapter(Context context, int sId, InviteUserDialogSheet inviteUserDialogSheet, boolean isEvent) {
        this.context = context;
        this.streamId = sId;
        this.inviteUserDialogSheet = inviteUserDialogSheet;
        this.isEvent = isEvent;
    }

    @NonNull
    @Override
    public InviteFollowersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InviteFollowersAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.following_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull InviteFollowersAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(followingList.get(position).getName());
        BaseUtils.setVerifiedAccount(followingList.get(position).getVerified(), holder.txt_name);
        holder.txt_username.setText(followingList.get(position).getUsername());
        Glide.with(context).load(BaseUtils.getUrlforPicture(followingList.get(position).getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteUserToStream(position);
            }
        });
    }

    private void inviteUserToStream(int position) {
        String type;
        if (isEvent){
            type = "event";
        }else {
            type = "stream";
        }
        ApiManager.apiCall(ApiClient.getInstance().getInterface().inviteToStream(streamId, followingList.get(position).getId(), type), context, new ApiResponseHandler<GenericDataModel<String>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<String>>> data) {
                EventBus.getDefault().post(new InviteToStreamEvent(followingList.get(position)));
                inviteUserDialogSheet.dismiss();
                BaseUtils.showLottieDialog(context, "User Invited Successfully", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
//                Toast.makeText(context, "User Invited Successfully", Toast.LENGTH_SHORT).show();
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
