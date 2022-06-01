package com.app.livewave.DialogSheets.wavesplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.livewave.R;
import com.app.livewave.utils.BaseUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;

public class SubscriptionSettingsDailog extends DialogFragment implements View.OnClickListener {
    ImageView btn_cancel;
    KProgressHUD dialog;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.premium_settings_dailog, container, false);
        setUp(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.RelativeDialog);
    }

    void setUp(View view) {
        dialog = BaseUtils.progressDialog(getContext());
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            this.dismiss();
        }
    }
}
