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
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FriendRequestModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Response;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {

    List<UserModel> friendRequestList;
    Context context;

    public FriendRequestAdapter(Context context) {
        this.context = context;
        this.friendRequestList = new ArrayList<>();
        Paper.init(context);
    }

    @NonNull
    @Override
    public FriendRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendRequestAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.friend_request_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(friendRequestList.get(position).getName());
        holder.txt_username.setText(friendRequestList.get(position).getUsername());
        Glide.with(context).load(BaseUtils.getUrlforPicture(friendRequestList.get(position).getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.img_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApi(friendRequestList.get(position).getId(), "accept", position);
            }
        });
        holder.img_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApi(friendRequestList.get(position).getId(), "deny", position);
            }
        });
    }

    private void callApi(Integer id, String response, int position) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().acceptOrReject(id, response), context, new ApiResponseHandler<String>() {
            @Override
            public void onSuccess(Response<ApiResponse<String>> data) {
                friendRequestList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    public void setList(List<UserModel> friendRequestModels) {
        this.friendRequestList = new ArrayList<>();
        this.friendRequestList = friendRequestModels;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_username;
        CircleImageView img_profile;
        ImageView img_reject, img_accept;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_reject = itemView.findViewById(R.id.img_reject);
            img_accept = itemView.findViewById(R.id.img_accept);
            img_profile = itemView.findViewById(R.id.img_profile);
        }
    }
}