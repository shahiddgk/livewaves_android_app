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
import com.kaopiz.kprogresshud.KProgressHUD;

public class TrackUnlikeOptionsDialog extends DialogFragment implements View.OnClickListener {
    private ImageView btn_cancel;
    KProgressHUD dialog;

    public TrackUnlikeOptionsDialog() {
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_unlike_options_dialog, container, false);
        initViews(view);
        initClickListeners();
        return view;
    }

    private void initClickListeners() {
        btn_cancel.setOnClickListener(this);
    }

    private void initViews(View view) {
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
        if (id == R.id.btn_save_to_playlist) {
            this.dismiss();
        } else if (id == R.id.btn_cancel) {
            this.dismiss();
        } else if (id == R.id.btn_create_new) {
//            CreatePlaylistDialog createPlaylistDialog = new CreatePlaylistDialog();
//            FragmentManager fm = getChildFragmentManager();
//            createPlaylistDialog.show(fm, "createPlaylistDialog");
        }
    }
}
