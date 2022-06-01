package com.app.livewave.adapters;

import static com.app.livewave.utils.Constants.HEADER_TITLE;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ParameterModels.HashtagsModel;
import com.app.livewave.utils.Constants;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HashtagTrendListAdapter extends RecyclerView.Adapter<HashtagTrendListAdapter.Holder> {
    Context context;
    List<HashtagsModel> hashtagsModelList = new ArrayList<>();
    //type 0 means from newsfeed 1 means from search
    int type;


    public HashtagTrendListAdapter(Context context, List<HashtagsModel> hashtagsModelList, int type) {
        this.context = context;
        this.hashtagsModelList = hashtagsModelList;
        this.type = type;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HashtagTrendListAdapter.Holder(LayoutInflater.from(context).inflate(R.layout.hashtag_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        HashtagsModel hashtagsModel = hashtagsModelList.get(position);
        holder.tv_hashtag.setText("#".concat(hashtagsModel.getTag()));

        if (type == 0) {
            holder.material_card.setCardBackgroundColor(context.getResources().getColor(R.color.buttercup));
        } else {
            holder.material_card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tv_hashtag.setTextColor(context.getResources().getColor(R.color.black));
            holder.material_card.setRadius(0);
            holder.tv_hashtag.setTextSize(18);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, HashtagFragment.class);
//                intent.putExtra(Constants.HASH_TAG, hashtagsModelList.get(position).getTag());
//                context.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(Constants.HASH_TAG, hashtagsModelList.get(position).getTag());
                bundle.putString(HEADER_TITLE, hashtagsModelList.get(position).getTag());
                ((HomeActivity)context).loadFragment(R.string.tag_hashtag,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return hashtagsModelList.size();
    }

    public void setList(List<HashtagsModel> mhashtagsModelList) {
        hashtagsModelList = new ArrayList<>();
        hashtagsModelList = mhashtagsModelList;
        notifyDataSetChanged();

    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tv_hashtag;
        private MaterialCardView material_card;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_hashtag = itemView.findViewById(R.id.tv_hashtag);
            material_card = itemView.findViewById(R.id.material_card);

        }
    }
}
