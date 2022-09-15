package com.app.livewave.DialogSheets;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.utils.BaseUtils;

public class report_content_list extends DialogFragment implements View.OnClickListener {

    TextView cancelBtn,submitBtn;
    Context context;
    private PostOptionInterface postOptionInterface;
    RadioGroup reportContentGroup;
    RadioButton radioButton;

    public void addListener(PostOptionInterface postOptionInterface) {
        this.postOptionInterface = postOptionInterface;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_report_content_list, container, false);
        initViews(view);
        initClickListeners();
        context = getActivity();
        return  view;
    }

    private void initClickListeners() {
        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    private void initViews(View view) {
        cancelBtn = view.findViewById(R.id.btn_no);
        submitBtn = view.findViewById(R.id.btn_yes);
        reportContentGroup = view.findViewById(R.id.report_user);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_yes) {
            int selectedId = reportContentGroup.getCheckedRadioButtonId();
            radioButton = v.findViewById(selectedId);
            if (selectedId == -1) {
                BaseUtils.showLottieDialog(context, "No option selected!", R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {

                    }
                });
            } else {
                postOptionInterface.pressed("Report");
            }
        } else {
            this.dismiss();
        }
    }
}