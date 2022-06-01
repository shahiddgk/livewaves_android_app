package com.app.livewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.ReactionsCategoryList;

import java.util.List;

public class ReactionBottomSheetViewPagerAdapter extends RecyclerView.Adapter<ReactionBottomSheetViewPagerAdapter.ViewPagerViewHolder> {

    Context context;
    ReactionsCategoryList userReactions;
    PostReactionsAdapter postReactionsAdapter;
    LinearLayoutManager linearLayoutManager;

    public ReactionBottomSheetViewPagerAdapter(Context context, ReactionsCategoryList mReactionsCategoryLists) {
        this.context = context;
        this.userReactions = mReactionsCategoryLists;
    }

    @NonNull
    @Override
    public ReactionBottomSheetViewPagerAdapter.ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerViewHolder(LayoutInflater.from(context).inflate(R.layout.reactions_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionBottomSheetViewPagerAdapter.ViewPagerViewHolder holder, int position) {

        if (userReactions != null) {
            switch (position) {
                case 0:
                    postReactionsAdapter = new PostReactionsAdapter(userReactions.getAllReactions(), context);
                    noReactionsVisibility(userReactions.getAllReactions(), "Reactions", holder.tv_no_reactions);

                    break;
                case 1:
                    postReactionsAdapter = new PostReactionsAdapter(userReactions.getSmileReactions(), context);
                    noReactionsVisibility(userReactions.getSmileReactions(), "Smile Reactions", holder.tv_no_reactions);

                    break;
                case 2:
                    postReactionsAdapter = new PostReactionsAdapter(userReactions.getMehReactions(), context);
                    noReactionsVisibility(userReactions.getMehReactions(), "Meh Reactions", holder.tv_no_reactions);

                    break;
                case 3:
                    postReactionsAdapter = new PostReactionsAdapter(userReactions.getSadReactions(), context);
                    noReactionsVisibility(userReactions.getSadReactions(), "Sad Reactions", holder.tv_no_reactions);

                    break;
                case 4:
                    postReactionsAdapter = new PostReactionsAdapter(userReactions.getWowReactions(), context);
                    noReactionsVisibility(userReactions.getWowReactions(), "Wow Reactions", holder.tv_no_reactions);

                    break;
                case 5:
                    postReactionsAdapter = new PostReactionsAdapter(userReactions.getAngryReactions(), context);
                    noReactionsVisibility(userReactions.getAngryReactions(), "Angry Reactions", holder.tv_no_reactions);

                    break;
            }

            linearLayoutManager = new LinearLayoutManager(context);
            holder.rv_reactions.setLayoutManager(linearLayoutManager);
            holder.rv_reactions.setAdapter(postReactionsAdapter);
            postReactionsAdapter.notifyDataSetChanged();
        } else {

            holder.tv_no_reactions.setVisibility(View.VISIBLE);
            holder.tv_no_reactions.setText(" No Reactions ");
        }
    }

    private void noReactionsVisibility(List<UserModel> smileReactions, String noReactionsText, TextView tv_no_reactions) {
        if (smileReactions.size() < 1) {
            tv_no_reactions.setVisibility(View.VISIBLE);
            tv_no_reactions.setText("No " + noReactionsText + " Reactions ");
        } else {
            tv_no_reactions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public static class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_reactions;
        TextView tv_no_reactions;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_reactions = itemView.findViewById(R.id.rv_reactions);
            tv_no_reactions = itemView.findViewById(R.id.tv_no_reactions);

            rv_reactions.hasFixedSize();
            rv_reactions.isNestedScrollingEnabled();
            rv_reactions.hasNestedScrollingParent();
        }
    }
}
