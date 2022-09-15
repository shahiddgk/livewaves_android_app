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
import com.app.livewave.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kaopiz.kprogresshud.KProgressHUD;

public class ReportOptionsDialogSheet extends BottomSheetDialogFragment {

    TextView tv_block, tv_report;
    int id;
    UserModel userModel;
    private KProgressHUD dialog;
    private PostOptionInterface postOptionInterface;
    String type;

    public ReportOptionsDialogSheet(UserModel otherUserModel, int userId,String type) {
        this.id = userId;
        this.userModel = otherUserModel;
        this.type = type;
    }
    public void addListener(PostOptionInterface postOptionInterface) {
        this.postOptionInterface = postOptionInterface;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report_oprtions, container, false);
        initViews(v);
        initListeners();
        return v;
    }

    private void initListeners() {

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

        if (type.equals(Constants.USER_PROFILE)) {

            tv_block.setVisibility(View.VISIBLE);
        } else {
            tv_block.setVisibility(View.GONE);
        }

        if (userModel.getIsBlocked() == 1){
            tv_block.setText("Unblock");
        } else if (userModel.getAmIBlocked() == 1){
        } else {
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