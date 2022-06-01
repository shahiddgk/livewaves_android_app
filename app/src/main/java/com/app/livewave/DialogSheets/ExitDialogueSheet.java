package com.app.livewave.DialogSheets;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.livewave.R;


public class ExitDialogueSheet extends DialogFragment implements View.OnClickListener {


    // TODO: Rename and change types of parameters
    TextView cancelBtn,okayBtn;
    Context context;

    public ExitDialogueSheet() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exit_dialogue_sheet, container, false);
        initViews(view);
        initClickListeners();
        context = getActivity();
        return view;
    }

    private void initClickListeners() {
        cancelBtn.setOnClickListener(this);
        okayBtn.setOnClickListener(this);
    }

    private void initViews(View view) {
        cancelBtn = view.findViewById(R.id.btn_no);
        okayBtn = view.findViewById(R.id.btn_yes);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_yes){
            getActivity().finishAffinity();
        }else {
            this.dismiss();
        }

    }
}