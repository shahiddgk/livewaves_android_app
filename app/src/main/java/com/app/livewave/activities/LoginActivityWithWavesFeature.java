package com.app.livewave.activities;

import static com.app.livewave.utils.Constants.FRAGMENT_TITLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.app.livewave.DialogSheets.ExitDialogueSheet;
import com.app.livewave.DialogSheets.PrivacyPolicyDialogueSheet;
import com.app.livewave.R;
import com.app.livewave.adapters.FragmentPageAdapter;
import com.app.livewave.fragments.ArtistPublicPlaylistFragment;
import com.app.livewave.fragments.CreateNewSubscription;
import com.app.livewave.fragments.DashboardFragment;
import com.app.livewave.fragments.Events.CreateEventFragment;
import com.app.livewave.fragments.Events.EventDetailFragment;
import com.app.livewave.fragments.Events.EventsFragment;
import com.app.livewave.fragments.Events.OtherPersonAndTimeSpecificEventsFragment;
import com.app.livewave.fragments.LoginFragment;
import com.app.livewave.fragments.SearchFragment;
import com.app.livewave.fragments.Subscription.SubscriptionListFragment;
import com.app.livewave.fragments.UserProfileFragment;
import com.app.livewave.fragments.UserStoreFragment;
import com.app.livewave.fragments.UserSubscriptionFragment;
import com.app.livewave.fragments.WavesFeature;
import com.app.livewave.fragments.WavesFeatureOutside_login;
import com.app.livewave.fragments.WavesPlayerUserStoreFragment;
import com.app.livewave.fragments.chat.ChatFragment;
import com.app.livewave.fragments.chat.FollowFragment;
import com.app.livewave.fragments.chat.FriendRequestFragment;
import com.app.livewave.fragments.chat.GroupInfoFragment;
import com.app.livewave.fragments.chat.HashtagFragment;
import com.app.livewave.fragments.chat.InboxFragment;
import com.app.livewave.fragments.live.CreateStreamFragment;
import com.app.livewave.fragments.live.OnSwipeTouchListener;
import com.app.livewave.fragments.newsfeed.PaidEventAndPaidPostFragment;
import com.app.livewave.fragments.newsfeed.PostDetailFragment;
import com.app.livewave.fragments.settings.AnalyticsFragment;
import com.app.livewave.fragments.settings.EditProfileFragment;
import com.app.livewave.fragments.settings.WalletFragment;
import com.app.livewave.fragments.wavesplayer.PlaylistDetailFragment;
import com.app.livewave.fragments.wavesplayer.WavesPlayerStoreFragment;
import com.app.livewave.fragments.wavesplayer.YourFollowingsFragment;
import com.app.livewave.fragments.wavesplayer.YourPlaylistFragment;
import com.app.livewave.fragments.wavesplayer.YourStoreFragment;
import com.app.livewave.fragments.wavesplayer.YourTrendingFragment;
import com.app.livewave.settings.MySubscriptionListFragment;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;

public class LoginActivityWithWavesFeature extends AppCompatActivity {
    private static final String TAG = "LoginActivityWithWavesF";

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