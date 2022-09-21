package com.app.livewave.adapters;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.livewave.fragments.LoginFragment;
import com.app.livewave.fragments.WavesFeature;
import com.app.livewave.fragments.WavesFeatureOutside_login;

public class FragmentPageAdapter extends FragmentStateAdapter {
    public int currentIndex;

    public FragmentPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    //    public FragmentPageAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//    }
//
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

//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        if (position == 0) {
//            return new LoginFragment();
//        } else if (position == 1){
//            return new WavesFeatureOutside_login();
//        }
//        return null;
//    }
//
//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        Log.e("TAG", "getItemPosition: " + object );
//        if (object instanceof LoginFragment){
//            return POSITION_UNCHANGED;
//        }else {
//            return POSITION_NONE;
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return 2;
//    }

}
