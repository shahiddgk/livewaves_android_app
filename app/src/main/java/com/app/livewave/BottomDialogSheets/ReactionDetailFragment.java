package com.app.livewave.BottomDialogSheets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.app.livewave.R;
import com.app.livewave.adapters.ReactionBottomSheetViewPagerAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.ReactionModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.ReactionsCategoryList;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ReactionDetailFragment extends BottomSheetDialogFragment {
    ViewPager2 vp_reactions;
    TabLayout tabLayout_reactions;
    ReactionBottomSheetViewPagerAdapter reactionBottomSheetViewPagerAdapter;
    ReactionsCategoryList reactionsCategoryLists;
    public static String postId;
    public static String commentId;
    public static List<UserModel> allReactions = new ArrayList<>();
    public static List<UserModel> smileReactions = new ArrayList<>();
    public static List<UserModel> mehReactions = new ArrayList<>();
    public static List<UserModel> sadReactions = new ArrayList<>();
    public static List<UserModel> wowReactions = new ArrayList<>();
    public static List<UserModel> angryReactions = new ArrayList<>();
    public static List<UserModel> laughingReactions = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reaction_detail, container, false);

        initTabLayout(view);
        if (commentId == "0" || commentId.equals("0")) {
            getReactionsData(postId);
        } else {
            getCommentReactionsData(commentId);
        }
        return view;
    }

    private void getReactionsData(String postId) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getPostReactions(postId), getContext(), new ApiResponseHandler<GenericDataModel<ReactionModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<ReactionModel>>> data) {
                appData(data.body().getData().getData());
            }
        });
    }

    private void getCommentReactionsData(String commentId) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getCommentReactions(commentId), getContext(), new ApiResponseHandler<GenericDataModel<ReactionModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<ReactionModel>>> data) {
                appData(data.body().getData().getData());
            }
        });
    }

    private void appData(List<ReactionModel> data) {
        clearReactions();
        for (int i = 0; i < data.size(); i++) {
            allReactions.add(data.get(i).getUser());
            switch (data.get(i).getReaction()) {
                case ("1"):
                    smileReactions.add(data.get(i).getUser());
                    break;
                case ("2"):
                    mehReactions.add(data.get(i).getUser());
                    break;
                case ("3"):
                    sadReactions.add(data.get(i).getUser());
                    break;
                case ("4"):
                    wowReactions.add(data.get(i).getUser());
                    break;
                case ("5"):
                    angryReactions.add(data.get(i).getUser());
                    break;
                case ("6"):
                    laughingReactions.add(data.get(i).getUser());
                    break;
                default:
                    break;
            }
        }
        reactionsCategoryLists = new ReactionsCategoryList(allReactions, smileReactions, mehReactions, sadReactions,wowReactions,angryReactions,laughingReactions);
        reactionBottomSheetViewPagerAdapter = new ReactionBottomSheetViewPagerAdapter(getContext(), reactionsCategoryLists);
        vp_reactions.setAdapter(reactionBottomSheetViewPagerAdapter);
        reactionBottomSheetViewPagerAdapter.notifyDataSetChanged();
    }

    private void clearReactions() {
        allReactions.clear();
        smileReactions.clear();
        mehReactions.clear();
        sadReactions.clear();
        wowReactions.clear();
        angryReactions.clear();
        laughingReactions.clear();
    }

    private void initTabLayout(View view) {
        tabLayout_reactions = view.findViewById(R.id.tabLayout_reactions);
        vp_reactions = view.findViewById(R.id.vp_reactions);
        reactionBottomSheetViewPagerAdapter = new ReactionBottomSheetViewPagerAdapter(getContext(), reactionsCategoryLists);
        vp_reactions.setAdapter(reactionBottomSheetViewPagerAdapter);

        new TabLayoutMediator(tabLayout_reactions, vp_reactions, true, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All");
                    break;
                case 1:
                    tab.setText("Happy");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_happy_reply));
                    break;

                case 2:
                    tab.setText("Meh");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_normal_reply));
                    break;
                case 3:
                    tab.setText("Sad");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_sad_bold));
                    break;
                case 4:
                    System.out.println("wowReactions");
                    tab.setText("Wow");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_wow_bold));
                    break;
                case 5:
                    System.out.println("angryReactions");
                    tab.setText("Angry");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_anygry_bold));
                    break;
                case 6:
                    System.out.println("laughingReactions");
                    tab.setText("Laugh");
                    tab.setIcon(getResources().getDrawable(R.drawable.ic_laughing));
                    break;
            }

        }).attach();
    }

    public ReactionDetailFragment(String mPostId,String mCommentId) {
        postId = mPostId;
        commentId = mCommentId;
        System.out.println("REACTIONS TYPE");
        System.out.println(postId);
        System.out.println(commentId);
    }
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
//    }

}