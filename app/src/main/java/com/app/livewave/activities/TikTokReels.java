package com.app.livewave.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.adapters.TikTokReelsAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.WavesModelResponse;
import com.app.livewave.models.VideoItem;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.webrtc.EglBase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static java.security.AccessController.getContext;

public class TikTokReels extends AppCompatActivity {

    ViewPager2 videosViewPager;
    ArrayList<VideoItem> videoItems;
    private List<WavesModelResponse> wavesVideoItems = new ArrayList<>();
    RelativeLayout no_videos_available;
    ImageView image_back_button;
    int currentPageNumber = 1;
    KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tik_tok_reels);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videosViewPager = (ViewPager2)findViewById(R.id.viewPagerVideos);
        image_back_button = (ImageView)findViewById(R.id.img_back_button);
        no_videos_available = (RelativeLayout) findViewById(R.id.no_videos_available);
        videoItems = new ArrayList<>();
        dialog = BaseUtils.progressDialog(getApplicationContext());

        getWavesItems(currentPageNumber);

        image_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TikTokReels.super.onBackPressed();
            }
        });

        videosViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                if (position == wavesVideoItems.size() - 2) {
                    System.out.println("LISTINDEXLISTINDEX");
                    System.out.println(position);

                    currentPageNumber = currentPageNumber + 1;
                    getWavesItems(currentPageNumber);
                }

                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

//        VideoItem obj1 = new VideoItem(Uri.parse("https://firebasestorage.googleapis.com/v0/b/todolist-6729b.appspot.com/o/Reels%2FThe%20Best%20Defender--%20-Shorts.mp4?alt=media&token=d2591836-432d-4ef8-bdb6-98f50ee8b8a7"),"The Best Defender","Ramos is one the best defender in the world");
//        videoItems.add(obj1);
//        VideoItem obj2 = new VideoItem(Uri.parse("https://firebasestorage.googleapis.com/v0/b/todolist-6729b.appspot.com/o/Reels%2FYOUR%20FAVOURITE%20OPNER%20NAME%20--%20SUBSCRIBE%20FOR%20MORE%20-cricket%20-viral%20-todaymatch.mp4?alt=media&token=05369aef-0664-4c73-b9f9-f1d92cf9a42b"),"The Best Player","Kohli is one the best cricket player in the world");
//        videoItems.add(obj2);
//        VideoItem obj3 = new VideoItem(Uri.parse("https://firebasestorage.googleapis.com/v0/b/todolist-6729b.appspot.com/o/Reels%2F--%20Ronaldo%20kick%20destroyed%20lukuku%20-shorts%20-subscribe.mp4?alt=media&token=6261b951-ea46-478a-a489-396e46ee5434" +
//                ""),"The Best Striker","Ronaldo is one the best Striker in the world");
//        videoItems.add(obj3);

   //     videosViewPager.setAdapter(new TikTokReelsAdapter(videoItems,getApplicationContext()));


    }

    private void getWavesItems(int pageNumber) {
        System.out.println("pageNumber");
        System.out.println(pageNumber);
//        if (currentPageNumber == 1) {
//            dialog.show();
//        }
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getWavesListItems(pageNumber), this, new ApiResponseHandler<List<WavesModelResponse>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<WavesModelResponse>>> data) {
                if (data != null) {

                    wavesVideoItems = data.body().getData();
                    System.out.println(data.body().getData());
                    System.out.println(currentPageNumber);
                    System.out.println(data.body().getData());



                        wavesVideoItems = new ArrayList<>();
                        wavesVideoItems = data.body().getData();

                    Context context = TikTokReels.this;

                    if (wavesVideoItems.size()>0) {
                        no_videos_available.setVisibility(View.GONE);
                    } else {
                        no_videos_available.setVisibility(View.VISIBLE);
                    }

                    videosViewPager.setAdapter(new TikTokReelsAdapter(wavesVideoItems,context,true));
//                    if (currentPageNumber == 1) {
//                        dialog.dismiss();
//                    }
                }
            }
        });

    }
}