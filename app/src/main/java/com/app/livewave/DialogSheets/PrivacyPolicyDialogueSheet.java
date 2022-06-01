package com.app.livewave.DialogSheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.app.livewave.R;


public class PrivacyPolicyDialogueSheet extends DialogFragment implements View.OnClickListener {

    private ImageView btn_cancel;
    Context context;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.privacy_policy_dialogue_sheet, container, false);
        initViews(view);
        initClickListeners();
        context = getActivity();
        return view;
    }

    private void initViews(View view) {
        btn_cancel = view.findViewById(R.id.btn_close_pop_up);
    }

    private void initClickListeners() {
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            this.dismiss();
        }

    }
}
