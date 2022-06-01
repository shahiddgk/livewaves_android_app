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

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.models.ParameterModels.HashtagsModel;
import com.app.livewave.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class HashtagSearchAdapter extends RecyclerView.Adapter<HashtagSearchAdapter.Holder> {
    Context context;
    List<HashtagsModel> hashtagsModelList = new ArrayList<>();


    public HashtagSearchAdapter(Context context, List<HashtagsModel> hashtagsModelList) {
        this.context = context;
        this.hashtagsModelList = hashtagsModelList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HashtagSearchAdapter.Holder(LayoutInflater.from(context).inflate(R.layout.hashtag_search_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        HashtagsModel hashtagsModel = hashtagsModelList.get(position);
        holder.txt_hashtag.setText(hashtagsModel.getTag());
        holder.txt_count.setText(hashtagsModel.getCount().toString());

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
        private TextView txt_hashtag,txt_count;

        public Holder(@NonNull View itemView) {
            super(itemView);
            txt_hashtag = itemView.findViewById(R.id.txt_hashtag);
            txt_count = itemView.findViewById(R.id.txt_count);
        }
    }
}
