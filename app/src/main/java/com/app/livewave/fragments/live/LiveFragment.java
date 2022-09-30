package com.app.livewave.fragments.live;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.DialogSheets.SaveStreamDialog;
import com.app.livewave.DialogSheets.SaveStreamListener;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.PublisherActivity;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ParameterModels.AttachmentParams;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.SaveStreamAsPost;
import com.app.livewave.models.SaveVideoFromGallery;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import static com.app.livewave.utils.Constants.FOLLOWING;
import static com.app.livewave.utils.Constants.GLOBAL_STREAMS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;


public class LiveFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LiveFragment";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private SaveStreamAsPost saveStreamAsPost;
    private String path;
    private String duration;
    private UserModel userModel;
    private ArrayList<AttachmentParams> arrayListAttachments = new ArrayList<>();
    PublisherActivity activity;
    BroadcastReceiver broadcastReceiver;
    String amountPaid;
    String titlePost;
    boolean yesClicked;
    Button getVideoBtn;


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
        getVideoBtn = view.findViewById(R.id.getVideoFromGallery);


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
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    Log.e(TAG, "onReceive: ");
                    createDialog(intent.getStringExtra("title"), intent.getStringExtra("hostId"));
                }

            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("Stream-Ended-Broadcast"));
        getVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withContext(getContext()).withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "select video"), 1001);
                        } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                Uri uri = data.getData();
                Log.e(TAG, "onActivityResult: " +data.getClipData() );
                File file = new File(uri.getPath());


                ApiManager.apiCallWithFailure(ApiClient.getINSTANCEForMediaAnt().getInterface().postVideo(file), getActivity(), new ApiResponseHandlerWithFailure<SaveVideoFromGallery>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<SaveVideoFromGallery>> data) {
                        if (data != null) {
                            Log.e(TAG, "onSuccess: " + data.body().getStatus());
                            Log.e(TAG, "onSuccess: " + data.body().getData().getDataId());
                        }
                    }

                    @Override
                    public void onFailure(String failureCause) {
                        Log.e(TAG, "onFailure: " + failureCause );
                    }
                });
//                ApiManager.apiCallWithFailure(ApiClient.getINSTANCEForMediaAnt().getInterface().getUploadedVideo("611218410182317740895659"), getActivity(), new ApiResponseHandlerWithFailure<SaveVideoFromGallery>() {
//                    @Override
//                    public void onSuccess(Response<ApiResponse<SaveVideoFromGallery>> data) {
//                        Log.e(TAG, "onSuccess: " + data.body() );
//                    }
//
//                    @Override
//                    public void onFailure(String failureCause) {
//
//                    }
//                });
            }
        }
    }

    private void createDialog(String title, String hostId) {
//        BaseUtils.showAlertDialog("Alert!", "Do you want to post your stream on profile?", getActivity(), new DialogBtnClickInterface() {
//            @Override
//            public void onClick(boolean positive) {
//                if (positive) {
//                    path = hostId;
//                    String extension = BaseUtils.getMimeType(getActivity(), Uri.parse(path));
////                    AsyncTask.execute(new Runnable() {
////                        @Override
////                        public void run() {
////                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
////                            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
////                            retriever.release();
////
////
////                            getActivity().runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    duration = time;
////                                }
////                            });
////                        }
////                    });
////
////
////                    postModel = new CreatePostModel(title, userModel.getId());
////                    postModel.setExtension(extension);
////                    postModel.setProfile_id(userModel.getId());
////
////                    postModel.setThumbnail(path);
////
////
////                    Log.e("path", "onSuccess: " + path);
////                    arrayListAttachments.add(new AttachmentParams(path, extension
////                            , duration));
////                    postModel.setAttachments(arrayListAttachments);
//
//
//                } else {
//                    Log.e(TAG, "onClick: ");
//                }
//            }
//        });
        path = hostId;
        SaveStreamDialog saveStreamDialog = new SaveStreamDialog(new SaveStreamListener() {
            @Override
            public void onYesButtonClickListener(boolean yes) {

                yesClicked = yes;


            }

            @Override
            public void onNoButtonClickListener(boolean no) {
                //  LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
            }

            @Override
            public void paidAmount(String amount) {
                amountPaid = amount;

            }

            @Override
            public void getDescription(String des) {
                if (yesClicked) {
                    if (des != null) {
                        if (amountPaid == null) {
                            Log.e(TAG, "onYesButtonClickListener: " + titlePost);
                            saveStreamAsPost = new SaveStreamAsPost(path, des, 0);
                            createPost(saveStreamAsPost);
                        } else {
                            Log.e(TAG, "onYesButtonClickListener: " + titlePost);
                            saveStreamAsPost = new SaveStreamAsPost(path, des, Integer.parseInt(amountPaid));
                            createPost(saveStreamAsPost);
                        }

                    }

                }
            }
        }, getActivity());
    }

    private void createPost(SaveStreamAsPost saveStreamAsPost) {

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().saveStreamAsPost(saveStreamAsPost), getActivity(), new ApiResponseHandlerWithFailure<SaveStreamAsPost>() {
            @Override
            public void onSuccess(Response<ApiResponse<SaveStreamAsPost>> data) {
                Log.e(TAG, "onSuccess: " + data.body().getStatus());
                if (data.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(String failureCause) {
                Log.e(TAG, "onFailure: " + failureCause.toString());

            }
        });
//        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().createPost(postModel), getContext(), new ApiResponseHandlerWithFailure<PostModel>() {
//            @Override
//            public void onSuccess(Response<ApiResponse<PostModel>> data) {
//                Log.e("TAG", "onSuccess: " + data.isSuccessful());
//                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
//
//            }
//
//            @Override
//            public void onFailure(String failureCause) {
//                Log.e("failure reason", "onFailure: " + failureCause);
//            }
//        });


        //   ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().saveStreamAsPost());
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
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
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
