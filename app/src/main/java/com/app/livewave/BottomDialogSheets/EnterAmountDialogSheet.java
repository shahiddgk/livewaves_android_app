package com.app.livewave.BottomDialogSheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.livewave.R;
import com.app.livewave.interfaces.MessageInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EnterAmountDialogSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextInputLayout til_amount;
    private TextInputEditText et_amount;
    private MaterialButton btn_done, btn_cancel;
    private MessageInterface messageInterface;

    public EnterAmountDialogSheet(MessageInterface messageInterface) {
        this.messageInterface = messageInterface;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_amount_dialog, container, false);
        initViews(view);
        initClickListeners();
        setCancelable(false);
        return view;
    }

    private void initClickListeners() {
        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void initViews(View view) {
        til_amount = view.findViewById(R.id.til_amount);
        et_amount = view.findViewById(R.id.et_amount);
        btn_done = view.findViewById(R.id.btn_done);
        btn_cancel = view.findViewById(R.id.btn_cancel);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private int getWindowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_done) {
            validationCheck();
        } else if (id == R.id.btn_cancel) {
            messageInterface.IAmountEnter(null);
            this.dismiss();
        }
    }

    private void validationCheck() {
        String amount = et_amount.getText().toString();
        if (amount.isEmpty()) {
            til_amount.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (amount.equals("0")) {
            til_amount.setError(getString(R.string.cant_submit_with_amount_zero));
            return;
        }
        messageInterface.IAmountEnter(amount);
        dismiss();

    }

}
