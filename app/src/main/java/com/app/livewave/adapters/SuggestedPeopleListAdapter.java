package com.app.livewave.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.SuggestedPeopleModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;

public class SuggestedPeopleListAdapter extends RecyclerView.Adapter<SuggestedPeopleListAdapter.Holder> {
    Context context;
    List<SuggestedPeopleModel> suggestedPeopleModels = new ArrayList<>();
    //type 0 means from newsfeed 1 means from search
    int type;


    public SuggestedPeopleListAdapter(Context context, List<SuggestedPeopleModel> suggestedPeopleModels) {
        this.context = context;
        this.suggestedPeopleModels = suggestedPeopleModels;
        this.type = type;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SuggestedPeopleListAdapter.Holder(LayoutInflater.from(context).inflate(R.layout.suggested_people, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        SuggestedPeopleModel suggestedPeopleModel = suggestedPeopleModels.get(position);
        Glide.with(context).load(getUrlforPicture(suggestedPeopleModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.txt_name.setText(suggestedPeopleModel.getName());
        holder.followers.setText(suggestedPeopleModel.getTotal_followers()+ " followers");
        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                followUnFollowUser(suggestedPeopleModel.getId(),position);
            }

            private void followUnFollowUser(int userId,int postion) {
//        dialog.show();
                System.out.println("Followed Clicked");

                ApiManager.apiCallWithFailure(new ApiClient().getInterface().followUnfollowUser(userId), context, new ApiResponseHandlerWithFailure<String>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<String>> data) {
//                dialog.dismiss();

                        if (data.body().getMessage().equals("Followed Successfully")) {
                            // userModel.setFollow(1);
                            suggestedPeopleModels.remove(position);
                            notifyDataSetChanged();

                        } else if (data.body().getMessage().equals("unfollowed Successfully")) {
                            //   userModel.setFollow(0);

                        }

                    }

                    @Override
                    public void onFailure(String failureCause) {
//                dialog.dismiss();

//                BaseUtils.showToast(UserProfileActivity.this, failureCause);
                        BaseUtils.showLottieDialog(context, failureCause, R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {

                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestedPeopleModels.size();
    }

    public void setList(List<SuggestedPeopleModel> suggestedPeopleModels) {
        suggestedPeopleModels = new ArrayList<>();
        suggestedPeopleModels = suggestedPeopleModels;
        notifyDataSetChanged();

    }

    public class Holder extends RecyclerView.ViewHolder {
        private CircleImageView img_profile;
        TextView txt_name,followers,followButton;
        public Holder(@NonNull View itemView) {
            super(itemView);
            img_profile = itemView.findViewById(R.id.img_profile);
            txt_name = itemView.findViewById(R.id.txt_name);
            followers = itemView.findViewById(R.id.followers);
            followButton = itemView.findViewById(R.id.Follow);
        }
    }
}
