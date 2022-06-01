package com.app.livewave.DialogSheets.wavesplayer;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;

import com.app.livewave.R;
import com.app.livewave.utils.BaseUtils;
import com.google.android.material.button.MaterialButton;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;

public class PremiumDailog extends DialogFragment implements View.OnClickListener {
    RelativeLayout premium_starter, premium_standard, premium_gold;
    MaterialButton btn_premium_starter, btn_premium_standard, btn_premium_gold;
    CardView collapsed_premium_starter, collapsed_premium_standard, collapsed_premium_gold;
    ImageView btn_cancel;
    KProgressHUD dialog;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.premium_dailog, container, false);
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
        btn_premium_starter = view.findViewById(R.id.btn_subscribe_package1);
        btn_premium_standard = view.findViewById(R.id.btn_subscribe_package2);
        btn_premium_gold = view.findViewById(R.id.btn_subscribe_package3);

        btn_cancel = view.findViewById(R.id.btn_cancel);

        premium_starter = view.findViewById(R.id.premium_starter);
        premium_standard = view.findViewById(R.id.premium_standard);
        premium_gold = view.findViewById(R.id.premium_gold);

        collapsed_premium_starter = view.findViewById(R.id.premium_starter_collapsed);
        collapsed_premium_standard = view.findViewById(R.id.premium_standard_collapsed);
        collapsed_premium_gold = view.findViewById(R.id.premium_gold_collapsed);

        btn_premium_starter.setOnClickListener(this::onClick);
        btn_premium_standard.setOnClickListener(this::onClick);
        btn_premium_gold.setOnClickListener(this::onClick);
        btn_cancel.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            this.dismiss();
        } else if (v.getId() == R.id.btn_subscribe_package1) {
            toggleView(collapsed_premium_starter, collapsed_premium_starter);
        } else if (v.getId() == R.id.btn_subscribe_package2) {
            toggleView(collapsed_premium_standard, collapsed_premium_standard);
        } else if (v.getId() == R.id.btn_subscribe_package3) {
            toggleView(collapsed_premium_gold, collapsed_premium_gold);
        }
    }

    void toggleView(ViewGroup baseView, View hiddenView) {
        if (hiddenView.getVisibility() == View.VISIBLE) {

            Animation slideOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.down);
            slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }
            });
            //Now Set your animation
            baseView.startAnimation(slideOutAnimation);

//            TransitionManager.beginDelayedTransition(baseView,
//                    new Slide(Gravity.BOTTOM));
            hiddenView.setVisibility(View.GONE);
        } else {
            TransitionManager.beginDelayedTransition(baseView,
                    new Slide(Gravity.TOP));
            hiddenView.setVisibility(View.VISIBLE);
        }
    }
}
