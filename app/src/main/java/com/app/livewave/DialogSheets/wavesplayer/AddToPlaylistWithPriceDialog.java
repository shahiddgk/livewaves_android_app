package com.app.livewave.DialogSheets.wavesplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.app.livewave.R;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.button.MaterialButton;
import com.kaopiz.kprogresshud.KProgressHUD;

public class AddToPlaylistWithPriceDialog extends DialogFragment implements View.OnClickListener {


    MaterialButton btn_subscribe_now;
    KProgressHUD dialog;
    ImageView btn_cancel;

    public AddToPlaylistWithPriceDialog() {
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.added_to_playlist_with_price_dailog, container, false);
        initViews(view);
        initClickListeners();
        return view;
    }

    private void initClickListeners() {
        btn_subscribe_now.setOnClickListener(this::onClick);
    }

    private void initViews(View view) {
        btn_subscribe_now = view.findViewById(R.id.btn_subscribe_now);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        dialog = BaseUtils.progressDialog(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.RelativeDialog);
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            this.dismiss();
        } else if (id == R.id.btn_subscribe_now) {
            premiumDialogSheet();
        }
    }

    void premiumDialogSheet() {
        PremiumDailog premiumDailog = new PremiumDailog();
        FragmentManager fm = getChildFragmentManager();
        premiumDailog.show(fm, "premiumDialogSheet");
    }
}
