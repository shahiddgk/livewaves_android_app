package com.app.livewave.BottomDialogSheets;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.models.ResponseModels.PostModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TimeLinePostDialogueOptions extends BottomSheetDialogFragment {

    private PostOptionInterface postOptionInterface;
    private TextView tv_delete, tv_edit;
    private boolean myProfile;
    private int post_id;
    private Integer myId;
    private Integer currentProfileUserId;


    public void addListener(PostOptionInterface postOptionInterface) {
        this.postOptionInterface = postOptionInterface;
    }

    public TimeLinePostDialogueOptions(int post_id) {

        this.post_id = post_id;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_line_post_dialogue_options, container, false);
        initViews(view);
        initClickListeners();
        return view;
    }

    private void initClickListeners() {
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.delete));
            }
        });
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed(getString(R.string.add));

            }
        });
    }

    private void initViews(View view) {
        tv_delete = view.findViewById(R.id.tv_delete_from_notification);
        tv_edit = view.findViewById(R.id.tv_add_notification_add);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }
}