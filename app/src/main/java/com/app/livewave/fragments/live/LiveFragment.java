package com.app.livewave.fragments.live;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.PublisherActivity;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ParameterModels.AttachmentParams;
import com.app.livewave.models.ParameterModels.CreatePostModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import static com.app.livewave.utils.Constants.FOLLOWING;
import static com.app.livewave.utils.Constants.GLOBAL_STREAMS;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Response;


public class LiveFragment extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private CreatePostModel postModel;
    private String path;
    private String duration;
    private UserModel userModel;
    private ArrayList<AttachmentParams> arrayListAttachments = new ArrayList<>();
    PublisherActivity activity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(getContext());

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_live, container, false);
    }

    private void initView(View view) {
        viewPager = view.findViewById(R.id.viewpager_event_list);
        tabLayout = view.findViewById(R.id.tl_global_following);
        toolbar = view.findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout =
                view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.Live));
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.buttercup));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        userModel = Paper.book().read(Constants.currentUser);







    }

    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());

        adapter.addFragment(LiveStreamsListFragment.newInstance(GLOBAL_STREAMS), GLOBAL_STREAMS);
        adapter.addFragment(LiveStreamsListFragment.newInstance(FOLLOWING), FOLLOWING);

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setClickListener();

    }

    private void setClickListener() {

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_live) {
//                    startActivity(new Intent(getContext(), CreateStreamFragment.class));

                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_create_stream, null);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.stream_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

//    @Override
//    public void onStreamEnded(boolean yes, String hostID, String title) {
//        if (yes) {
//            BaseUtils.showAlertDialog("Post Stream", "Do you want to post stream on profile", getContext(), new DialogBtnClickInterface() {
//                @Override
//                public void onClick(boolean positive) {
//                    if (positive) {
//                       // path = "https://poststream.s3.us-east-2.amazonaws.com/streams/" + hostID + ".mp4";
//                        String extension = BaseUtils.getMimeType(getContext(), Uri.parse(path));
//                        AsyncTask.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                                retriever.release();
//
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        duration = time;
//                                    }
//                                });
//                            }
//                        });
//
//
//                        postModel = new CreatePostModel(title, userModel.getId());
//                        postModel.setExtension(extension);
//                        postModel.setProfile_id(userModel.getId());
//
//                        postModel.setThumbnail(path);
//
//
//                        Log.e("path", "onSuccess: " + path);
//                        arrayListAttachments.add(new AttachmentParams(path, extension
//                                , duration));
//                        postModel.setAttachments(arrayListAttachments);
//                        createPost(postModel);
//                    }
//
//                }
//            });
//
//        }
//    }
//
//    private void createPost(CreatePostModel postModel) {
//        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().createPost(postModel), getContext(), new ApiResponseHandlerWithFailure<PostModel>() {
//            @Override
//            public void onSuccess(Response<ApiResponse<PostModel>> data) {
//                Log.e("TAG", "onSuccess: " + data.isSuccessful());
//            }
//
//            @Override
//            public void onFailure(String failureCause) {
//                Log.e("failure reason", "onFailure: " + failureCause);
//            }
//        });
//    }
}
