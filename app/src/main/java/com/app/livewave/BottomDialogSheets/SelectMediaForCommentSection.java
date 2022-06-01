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

public class SelectMediaForCommentSection extends BottomSheetDialogFragment {

    private PostOptionInterface postOptionInterface;
    private TextView tv_video_gallery, tv_gallery;
    private int numberOfOptions;

    public SelectMediaForCommentSection(int mNumberOfOptions) {
        this.numberOfOptions = mNumberOfOptions;


    }
    public void addClickListener( PostOptionInterface postOptionInterface){
        this.postOptionInterface = postOptionInterface;

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_media_for_comment_section, container, false);
        initViews(view);
        initClickListeners();

        return view;
    }

    private void initClickListeners() {

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
                postOptionInterface.pressed(getString(R.string.video_giff));
                dismiss();

            }
        });


    }

    private void initViews(View view) {

        tv_gallery = view.findViewById(R.id.tv_gallery_comment);
        tv_video_gallery = view.findViewById(R.id.tv_video_giff);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }


}
