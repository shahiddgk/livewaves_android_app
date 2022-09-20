package com.app.livewave.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.livewave.R;
import com.app.livewave.adapters.TikTokReelsAdapter;
import com.app.livewave.fragments.live.OnSwipeTouchListener;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.WavesModelResponse;
import com.app.livewave.models.VideoItem;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static org.webrtc.ContextUtils.getApplicationContext;

public class WavesFeatureOutside_login extends Fragment {
    private static final String TAG = "WavesFeatureOutside_log";
    ViewPager2 videosViewPager;
    ArrayList<VideoItem> videoItems;
    private List<WavesModelResponse> wavesVideoItems = new ArrayList<>();
    RelativeLayout no_videos_available;
    ImageView image_back_button;
    int currentPageNumber = 1;
    KProgressHUD dialog;
    CollapsingToolbarLayout collapsingToolbarLayout;

    public WavesFeatureOutside_login() {
        // Required empty public constructor
    }


//    public static WavesFeatureOutside_login newInstance(String param1, String param2) {
//        WavesFeatureOutside_login fragment = new WavesFeatureOutside_login();
//        Bundle args = new Bundle();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waves_feature_outside_login, container, false);
        videosViewPager = view.findViewById(R.id.viewPagerVideos);
        no_videos_available = view.findViewById(R.id.no_videos_available);
        image_back_button = view.findViewById(R.id.img_back_button_waves_feature);
        videoItems = new ArrayList<>();
        dialog = BaseUtils.progressDialog(getContext());
        getWavesItems(currentPageNumber);

        videosViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.e(TAG, "onPageScrollStateChanged: " + state );
                if ( state == videosViewPager.SCROLL_STATE_DRAGGING ){
                    videosViewPager.setUserInputEnabled(true);
                } else if (state == videosViewPager.SCROLL_AXIS_VERTICAL) {
                    videosViewPager.setUserInputEnabled(true);
                }
            }
        });


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


    private void getWavesItems(int currentPageNumber) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getWavesListItems(currentPageNumber), getApplicationContext(), new ApiResponseHandler<List<WavesModelResponse>>() {
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
                    videosViewPager.requestDisallowInterceptTouchEvent(true);
                    videosViewPager.setAdapter(new TikTokReelsAdapter(wavesVideoItems,context,false));
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
}