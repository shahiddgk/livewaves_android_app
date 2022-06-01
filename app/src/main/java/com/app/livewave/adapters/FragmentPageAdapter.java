package com.app.livewave.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.livewave.fragments.LoginFragment;
import com.app.livewave.fragments.WavesFeature;
import com.app.livewave.fragments.WavesFeatureOutside_login;

public class FragmentPageAdapter extends FragmentStateAdapter{

    public FragmentPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LoginFragment();
        } else {
            return new WavesFeatureOutside_login();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
