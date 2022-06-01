package com.app.livewave.BottomDialogSheets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.livewave.R;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kaopiz.kprogresshud.KProgressHUD;

public class OptionsDialogSheet extends BottomSheetDialogFragment {

    TextView tv_block, tv_report, tv_follow;
    int id;
    UserModel userModel;
    private KProgressHUD dialog;
    private PostOptionInterface postOptionInterface;

    public OptionsDialogSheet(UserModel otherUserModel, int userId) {
        this.id = userId;
        this.userModel = otherUserModel;
    }
    public void addListener(PostOptionInterface postOptionInterface) {
        this.postOptionInterface = postOptionInterface;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_options_dialog_sheet, container, false);
        initViews(v);
        initListeners();
        return v;
    }

    private void initListeners() {
        tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed("Follow");
            }
        });
        tv_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed("Block");
            }
        });
        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOptionInterface.pressed("Report");
            }
        });
    }

//

    private void initViews(View v) {
//        Paper.init(getContext());
//        userModel = Paper.book().read(Constants.currentUser);
        dialog = BaseUtils.progressDialog(getContext());
        tv_block = v.findViewById(R.id.tv_block);
        tv_report = v.findViewById(R.id.tv_report);
        tv_follow = v.findViewById(R.id.tv_follow);

        if (userModel.getFollow().equals(1)) {
            tv_follow.setText("Unfollow");
        } else if (userModel.getPrivate().equals("1") && userModel.getFollow().equals(0) && userModel.getRequested() == 1) {
            tv_follow.setText("Cancel Request");
        } else {
            tv_follow.setText("Follow");
        }
        if (userModel.getIsBlocked() == 1){
            tv_follow.setVisibility(View.GONE);
            tv_block.setText("Unblock");
        } else if (userModel.getAmIBlocked() == 1){
            tv_follow.setVisibility(View.GONE);
        } else {
            tv_follow.setVisibility(View.VISIBLE);
            tv_block.setText("Block");
        }
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }
}