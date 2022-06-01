package com.app.livewave.BottomDialogSheets;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.interfaces.PostOptionInterface;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class VideoSelectionOption extends BottomSheetDialogFragment {

    private PostOptionInterface postOptionInterface;
    private TextView tv_video_gallery, tv_video_camera;
    private int numberOfOptions;

    public VideoSelectionOption(int mNumberOfOptions) {
       this.numberOfOptions = mNumberOfOptions;
    }

    public void addClickListener( PostOptionInterface postOptionInterface){
        this.postOptionInterface = postOptionInterface;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_selection_option, container, false);
        initViews(view);
        initClickListeners();

        return view;
    }

    private void initClickListeners() {
        tv_video_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.take_video_from_camera));
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
        tv_video_camera = view.findViewById(R.id.tv_camera);
        tv_video_gallery = view.findViewById(R.id.tv_video_gallery);
    }
}