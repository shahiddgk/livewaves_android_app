package com.app.livewave.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;

import com.app.livewave.DialogSheets.ExitDialogueSheet;
import com.app.livewave.DialogSheets.PrivacyPolicyDialogueSheet;
import com.app.livewave.R;
import com.app.livewave.adapters.FragmentPageAdapter;

public class LoginActivityWithWavesFeature extends AppCompatActivity {

    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_login_with_waves_feature);
        viewPager2 = (ViewPager2)findViewById(R.id.slider_view_pager_for_login_and_waves_feature);

        FragmentPageAdapter fragmentPageAdapter = new FragmentPageAdapter(this);
        viewPager2.setAdapter(fragmentPageAdapter);

    }

    @Override
    public void onBackPressed() {
        ExitDialogueSheet exitDialogueSheet = new ExitDialogueSheet();
        FragmentManager fm = getSupportFragmentManager();
        exitDialogueSheet.show(fm, "Close App");
    }
}