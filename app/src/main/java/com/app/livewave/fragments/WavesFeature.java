package com.app.livewave.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.livewave.BottomDialogSheets.CreatePostDialogSheet;
import com.app.livewave.BottomDialogSheets.CreateWavesBottomSheet;
import com.app.livewave.R;
import com.app.livewave.activities.LoginActivityWithWavesFeature;
import com.app.livewave.activities.TikTokReels;
import com.app.livewave.adapters.TikTokReelsAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.WavesModelResponse;
import com.app.livewave.models.VideoItem;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.POST_CREATE_DIALOG;
import static com.app.livewave.utils.Constants.WAVE_CREATE_DIALOG;
import static org.webrtc.ContextUtils.getApplicationContext;


public class WavesFeature extends Fragment implements PlayerStateListener, View.OnClickListener {

    ViewPager2 videosViewPager;
    ArrayList<VideoItem> videoItems;
    private List<WavesModelResponse> wavesVideoItems = new ArrayList<>();
    RelativeLayout no_videos_available;
    ImageView image_back_button,image_create_wave;
    int currentPageNumber = 1;
    KProgressHUD dialog;
    int userId;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private CreateWavesBottomSheet bottomSheetDialog;

    Context context;


//    public static WavesFeature newInstance(String param1, String param2) {
//        WavesFeature fragment = new WavesFeature();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waves_feature, container, false);
        videosViewPager = view.findViewById(R.id.viewPagerVideos);
        no_videos_available = view.findViewById(R.id.no_videos_available);
        image_back_button = view.findViewById(R.id.img_back_button_waves_feature);
        image_create_wave = view.findViewById(R.id.img_add_button_waves_feature);
        userId = Paper.book().read("CurrentUserId");
        videoItems = new ArrayList<>();
        dialog = BaseUtils.progressDialog(getContext());
        getWavesItems(userId);

   //     image_back_button.setVisibility(View.GONE);

        image_back_button.setOnClickListener(this);
        image_create_wave.setOnClickListener(this);
//        image_back_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().onBackPressed();
//            }
//        });

//        if (isLogin == false) {
//            image_back_button.setVisibility(View.GONE);
//        } else {
//            image_back_button.setVisibility(View.VISIBLE);
//        }
//
//        image_back_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().onBackPressed();
//            }
//        });

//        if (isLogin == false) {
//            imageView.setVisibility(View.GONE);
//        } else {
//            imageView.setVisibility(View.VISIBLE);
//        }


//        videosViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//                if (position == wavesVideoItems.size() - 2) {
//                    System.out.println("LISTINDEXLISTINDEX");
//                    System.out.println(position);
//
//                    currentPageNumber = currentPageNumber + 1;
//                    getWavesItems(currentPageNumber);
//                }
//
//                super.onPageSelected(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                super.onPageScrollStateChanged(state);
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getWavesItems(currentPageNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().getInterface().getWavesListItems(currentPageNumber).cancel();
    }

    private void getWavesItems(int userID) {
        System.out.println("pageNumber");
        System.out.println(userID);
//        if (currentPageNumber == 1) {
//            dialog.show();
//        }
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getWavesListItemsWithId(userID), getApplicationContext(), new ApiResponseHandler<List<WavesModelResponse>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<WavesModelResponse>>> data) {
                if (data != null) {

                    wavesVideoItems = data.body().getData();
                    System.out.println(data.body().getData());
                    System.out.println(currentPageNumber);

                        wavesVideoItems = new ArrayList<>();
                        wavesVideoItems = data.body().getData();


                    if (wavesVideoItems.size()>0) {
                        no_videos_available.setVisibility(View.GONE);
                    } else {
                        no_videos_available.setVisibility(View.VISIBLE);
                    }

                    Context context = getContext();

                    videosViewPager.setAdapter(new TikTokReelsAdapter(wavesVideoItems,context,true));
//                    if (currentPageNumber == 1) {
//                        dialog.dismiss();
//                    }
                }
            }
        });

        if (wavesVideoItems.size()>0) {
            no_videos_available.setVisibility(View.GONE);
        } else {
            no_videos_available.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void updatePlayerState() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back_button_waves_feature) {
            getActivity().onBackPressed();
        }else if (view.getId() == R.id.img_add_button_waves_feature) {
            System.out.println("CURRENT USER ID");
            System.out.println(userId);
            bottomSheetDialog = new CreateWavesBottomSheet(WAVE_CREATE_DIALOG, userId);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            bottomSheetDialog.show(fm, "myDialog");

        }
    }
}