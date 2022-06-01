package com.app.livewave.BottomDialogSheets;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.livewave.R;
import com.app.livewave.interfaces.PostOptionInterface;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SelectMediaForPostDialogFragment extends BottomSheetDialogFragment {

    private PostOptionInterface postOptionInterface;
    private TextView tv_video_gallery, tv_gallery, tv_camera;
    private int numberOfOptions;

    public SelectMediaForPostDialogFragment(int mNumberOfOptions) {
        this.numberOfOptions = mNumberOfOptions;


    }
    public void addClickListener( PostOptionInterface postOptionInterface){
        this.postOptionInterface = postOptionInterface;

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_media_for_post_options_dialog, container, false);
        initViews(view);
        initClickListeners();

        return view;
    }

    private void initClickListeners() {
        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.take_photo_from_camera));
                dismiss();

            }
        });
        tv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.select_picture_from_gallery));
                dismiss();

            }
        });
        tv_video_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.video_gallery));
                dismiss();

            }
        });


    }

    private void initViews(View view) {
        tv_camera = view.findViewById(R.id.tv_camera);
        tv_gallery = view.findViewById(R.id.tv_gallery);
        tv_video_gallery = view.findViewById(R.id.tv_video_gallery);
        if (numberOfOptions == 2) {
            tv_video_gallery.setVisibility(View.GONE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }


}
