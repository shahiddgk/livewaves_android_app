package com.app.livewave.activities;

import static com.app.livewave.utils.Constants.FRAGMENT_TITLE;
import static com.app.livewave.utils.Constants.HAS_EXTRA;
import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;
import static com.app.livewave.utils.Constants.USER_NAME;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.livewave.R;
import com.app.livewave.fragments.ArtistPublicPlaylistFragment;
import com.app.livewave.fragments.CreateNewSubscription;
import com.app.livewave.fragments.DashboardFragment;
import com.app.livewave.fragments.Events.CreateEventFragment;
import com.app.livewave.fragments.Events.EventDetailFragment;
import com.app.livewave.fragments.Events.EventsFragment;
import com.app.livewave.fragments.Events.OtherPersonAndTimeSpecificEventsFragment;
import com.app.livewave.fragments.SearchFragment;
import com.app.livewave.fragments.Subscription.SubscriptionListFragment;
import com.app.livewave.fragments.UserProfileFragment;
import com.app.livewave.fragments.UserStoreFragment;
import com.app.livewave.fragments.UserSubscriptionFragment;
import com.app.livewave.fragments.WavesFeature;
import com.app.livewave.fragments.WavesPlayerUserStoreFragment;
import com.app.livewave.fragments.chat.ChatFragment;
import com.app.livewave.fragments.chat.FollowFragment;
import com.app.livewave.fragments.chat.FriendRequestFragment;
import com.app.livewave.fragments.chat.GroupInfoFragment;
import com.app.livewave.fragments.chat.HashtagFragment;
import com.app.livewave.fragments.chat.InboxFragment;
import com.app.livewave.fragments.live.CreateStreamFragment;
import com.app.livewave.fragments.newsfeed.PaidEventAndPaidPostFragment;
import com.app.livewave.fragments.newsfeed.PostDetailFragment;
import com.app.livewave.fragments.settings.AnalyticsFragment;
import com.app.livewave.fragments.settings.EditProfileFragment;
import com.app.livewave.fragments.settings.WalletFragment;
import com.app.livewave.fragments.wavesplayer.ArtistSubscriptionsFragment;
import com.app.livewave.fragments.wavesplayer.PlaylistDetailFragment;
import com.app.livewave.fragments.wavesplayer.WavesPlayerStoreFragment;
import com.app.livewave.fragments.wavesplayer.YourFollowingsFragment;
import com.app.livewave.fragments.wavesplayer.YourPlaylistFragment;
import com.app.livewave.fragments.wavesplayer.YourPurchasedSubscriptionsFragment;
import com.app.livewave.fragments.wavesplayer.YourStoreFragment;
import com.app.livewave.fragments.wavesplayer.YourTrendingFragment;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.settings.MySubscriptionListFragment;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class HomeActivity extends WavesPlayerBaseActivity {
    private Handler mHandler;
    private Toolbar toolbar;
    AppBarLayout appBarLayout;
    int actionBarHeight;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ConstraintLayout fullscreen_audio_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        collapsingToolbarLayout =
                findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.buttercup));

        global_wavesplayer = findViewById(R.id.global_wavesplayer);
        fullscreen_audio_view = findViewById(R.id.fullscreen_audio_view);

//        global_wavesplayer.setOnClickListener(v -> {
//            loadFragment(R.string.tag_now_playing, null);
//        });

        actionBarHeight = getActionBarHeight();

        mHandler = new Handler();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setPlayerViews();
        initializeSeekBar();
        getCategories();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
            if (f != null) {
                if (f.getArguments() != null && f.getArguments().containsKey(HIDE_HEADER)) {
                    hideToolbar();
                } else {
                    showToolbar();
                }
                Bundle b = f.getArguments();
                if (f.getArguments() != null && f.getArguments().containsKey(HEADER_TITLE)) {
                    collapsingToolbarLayout.setTitle(f.getArguments().getString(HEADER_TITLE));
                } else {
                    collapsingToolbarLayout.setTitle( f.getArguments().getString(FRAGMENT_TITLE));
                }
            }
        });



//
//       if (getIntent().getExtras() != null){
//           if (getIntent().getBooleanExtra("fromNotification",false)){
//               if(getIntent().getExtras() != null){
//                   if (getIntent().getBooleanExtra("fromNotification",false)){
//                       try {
//                           Log.e("Home", "onCreate: " );
//
//                           loadFragment(R.string.tag_inbox,null);
//                       }catch (IllegalStateException illegalStateException){
//                           Log.e("error with fragment", "onCreate: "+ illegalStateException.getMessage() );
//                       }
//                   }
//               }
//
//
//           }else {
//
//           }

      // }
        Log.e("TAG", "onCreate: " + "not from notification");
        if (getIntent().hasExtra(HAS_EXTRA)) {
            Bundle bundle = getIntent().getExtras();
            loadFragment(getIntent().getIntExtra(HAS_EXTRA, R.string.tag_dashboard), bundle);
        } else {
            //For Current Store Items Only
            Bundle bundle = new Bundle();
            bundle.putBoolean(HIDE_HEADER, false);
            try {
                if (getIntent().getBooleanExtra("fromNotification",false)){
                    loadFragment(R.string.tag_inbox,null);
                }else {
                    loadFragment(R.string.tag_dashboard, bundle);
                }

            }catch (IllegalStateException e){
                Log.e("TAG", "onCreate: " + e.getMessage() );
            }


            // loadFragment(R.string.tag_wp_store, bundle);
        }
    }

    int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public boolean isTaskRoot(Fragment currentFragment) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1 && f.getClass() == currentFragment.getClass()) {
            return true;
        }
        return false;
    }

    public void showToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        appBarLayout.setVisibility(View.VISIBLE);
        ((CoordinatorLayout.LayoutParams)
                findViewById(R.id.fragment_container_view).getLayoutParams()).
                setBehavior(new AppBarLayout.ScrollingViewBehavior(this, null));
    }

    public void hideToolbar() {
        appBarLayout.setVisibility(View.GONE);
        ((CoordinatorLayout.LayoutParams)
                findViewById(R.id.fragment_container_view).getLayoutParams()).
                setBehavior(null);
    }

    public void setHeaderTitle(String title) {
        collapsingToolbarLayout.setTitle(title);
    }

    public void setUpPlayerPosition(boolean addMargin) {
        if (addMargin) {
            findViewById(R.id.root_view).getLayoutParams()
                    .height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            ((ConstraintLayout.LayoutParams) global_wavesplayer.getLayoutParams())
                    .setMargins(0, 0, 0, actionBarHeight);
        } else {
            ((ConstraintLayout.LayoutParams) global_wavesplayer.getLayoutParams())
                    .setMargins(0, 0, 0, 0);
            findViewById(R.id.root_view).getLayoutParams()
                    .height = 0;
        }
    }

    public void loadFragment(final int fTagId, Bundle b) {
        System.out.println("FRAGEMENT ID");
        System.out.println(fTagId);

        final String tagString = Integer.toString(fTagId);
        Log.e("for tag checking ", "loadFragment: " + tagString );
        performFragmentTransaction(tagString, fTagId, b);
    }

    public void performFragmentTransaction(final String tagString, final int fTagId, Bundle b) {
        final FragmentManager fm = getSupportFragmentManager();
        Fragment cFragment = fm.findFragmentByTag(tagString);
        System.out.println("CFRAGMENT");
        System.out.println(cFragment);
        if (cFragment == null) {
            final Fragment fragment = getFragmentByTag(fTagId);
            String fTitle = getString(fTagId);
            playerStateListener = (PlayerStateListener) fragment;
            if (b != null) {
                b.putString(FRAGMENT_TITLE, fTitle);
            }else{
                b = new Bundle();
                b.putString(FRAGMENT_TITLE, fTitle);
            }
            fragment.setArguments(b);

            Runnable mPendingRunnable = () -> {
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left, R.anim.slide_in_left,
                        R.anim.slide_out_right);
                if (fTagId == R.string.tag_events_list || fTagId == R.string.tag_event_create || fTagId == R.string.tag_purchased_subscriptions || fTagId == R.string.tag_subscription_create) {
                    System.out.println("EVENTS EVENTS EVENTS");
                    fragmentTransaction.replace(R.id.fragment_container_view, fragment, tagString);
                }else {
                    fragmentTransaction.add(R.id.fragment_container_view, fragment, tagString);
                }
                fragmentTransaction.addToBackStack(tagString);
                fragmentTransaction.commitAllowingStateLoss();
                fm.executePendingTransactions();
            };
            if (mPendingRunnable != null) {
                mHandler.post(mPendingRunnable);
            }
        } else {
            if(b!=null)
            {
                cFragment.setArguments(b);

                fm.popBackStackImmediate(tagString, 0);
            }

        }
    }

    Fragment getFragmentByTag(int tag) {
        Fragment fragment = null;
        if (tag == R.string.tag_dashboard) {
            fragment = new DashboardFragment();
        } else if(tag == R.string.tag_waves_Features) {
            fragment = new WavesFeature();
        } else if (tag == R.string.tag_my_store) {
            fragment = new YourStoreFragment();
        } else if (tag == R.string.tag_Artist_store) {
            fragment = new UserStoreFragment();
        } else if (tag == R.string.tag_your_playlist) {
            fragment = new YourPlaylistFragment();
        } else if (tag == R.string.tag_artist_playlist) {
            fragment = new ArtistPublicPlaylistFragment();
        } else if (tag == R.string.tag_playlist_detail) {
            fragment = new PlaylistDetailFragment();
        } else if (tag == R.string.tag_your_followings) {
            fragment = new YourFollowingsFragment();
        } else if (tag == R.string.tag_your_trendings) {
            fragment = new YourTrendingFragment();
        } else if (tag == R.string.tag_wp_store) {
            fragment = new WavesPlayerStoreFragment();
        } else if (tag == R.string.tag_user_wp_store) {
            fragment = new WavesPlayerUserStoreFragment();
        } else if (tag == R.string.tag_subscription) {
            fragment = new MySubscriptionListFragment();
        } else if (tag == R.string.tag_subscription_create) {
          fragment = new CreateNewSubscription();
        } else if (tag == R.string.tag_purchased_subscriptions) {
            fragment = new SubscriptionListFragment();
        } else if (tag == R.string.tag_artist_subscriptions) {
            fragment = new UserSubscriptionFragment();
        } else if (tag == R.string.tag_events_list) {
            fragment = new EventsFragment();
        } else if (tag == R.string.tag_event_create) {
            fragment = new CreateEventFragment();
        } else if (tag == R.string.tag_inbox) {
            fragment = new InboxFragment();
        } else if (tag == R.string.tag_chat) {
            fragment = new ChatFragment();
        } else if (tag == R.string.tag_user_profile) {
            fragment = new UserProfileFragment();
        } else if (tag == R.string.tag_search) {
            fragment = new SearchFragment();
        } else if (tag == R.string.tag_edit_profile) {
            fragment = new EditProfileFragment();
        } else if (tag == R.string.tag_follow) {
            fragment = new FollowFragment();
        } else if (tag == R.string.tag_group_info) {
            fragment = new GroupInfoFragment();
        } else if (tag == R.string.tag_hashtag) {
            fragment = new HashtagFragment();
        } else if (tag == R.string.tag_post_detail) {
            fragment = new PostDetailFragment();
        } else if (tag == R.string.tag_friend_request) {
            fragment = new FriendRequestFragment();
        } else if (tag == R.string.tag_wallet) {
            fragment = new WalletFragment();
        } else if (tag == R.string.tag_analytics) {
            fragment = new AnalyticsFragment();
        } else if (tag == R.string.tag_gettting_started) {
            fragment = new GettingStartedFragment();
        } else if (tag == R.string.tag_create_stream) {
            fragment = new CreateStreamFragment();
            //            case R.string.tag_guest_publisher:
//                fragment = new GuestPublisherFragment();
//                break;
//            case R.string.tag_subscriber:
//                fragment = new SubscriberFragment();
//                break;
//            case R.string.tag_webview:
//                fragment = new WebviewFragment();
//                break;
        } else if (tag == R.string.tag_events_detail) {
            fragment = new EventDetailFragment();
            //            case R.string.tag_publisher:
//                fragment = new PublisherFragment();
//                break;
        } else if (tag == R.string.tag_other_person_and_time_specific_events) {
            fragment = new OtherPersonAndTimeSpecificEventsFragment();
            //            case R.string.tag_now_playing:
//                fragment = new NowPlayingFragment();
//                break;
        } else if (tag == R.string.tag_paid_event_paid_post) {
            fragment = new PaidEventAndPaidPostFragment();
        }
        return fragment;
    }

    public void openUserProfile(String id) {
        Paper.init(this);
        UserModel userModel = Paper.book().read(Constants.currentUser);
        if (!id.equals(userModel.getId().toString()) && !id.equals(userModel.getUsername())) {
            String tagString = R.string.tag_user_profile + id;
            Bundle bundle = new Bundle();
            bundle.putString(SPECIFIC_USER_ID, id);
            bundle.putBoolean(HIDE_HEADER, false);
            performFragmentTransaction(tagString, R.string.tag_user_profile, bundle);
//            Intent intent = new Intent(context, UserProfileFragment.class);
//            intent.putExtra(SPECIFIC_USER_ID, id);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        // Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        int fragments = getSupportFragmentManager().getBackStackEntryCount();

        if (fullscreen_audio_view.getVisibility() == View.VISIBLE) {
            fullscreen_audio_view.setVisibility(View.GONE);
            findViewById(R.id.btn_close_full_screen_player).setVisibility(View.GONE);
        } else if (fragments > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    private void setPlayerViews() {
        playPause = findViewById(R.id.buttonPlayPause);
        playPauseFullScreen = findViewById(R.id.play_current_song);
        next = findViewById(R.id.buttonNext);
        nextSongFullScreen = findViewById(R.id.play_next);
        previousSongFullSong = findViewById(R.id.play_privious);
        closePlayer = findViewById(R.id.btn_close_player);
        timerText = findViewById(R.id.timer_text);
        timerTextFullScreen = findViewById(R.id.song_duration);
        previous = findViewById(R.id.buttonPrevious);
        seekBar = findViewById(R.id.song_progress_circular);
        seekBarFullScreen = findViewById(R.id.song_progress_full_screen_circular);
        songTitle = findViewById(R.id.songTitle);
        songTitleFullScreen = findViewById(R.id.fullscreen_song_title);
        songArtist = findViewById(R.id.songArtist);
        songArtistFullScreen = findViewById(R.id.fullscreen_artist_name);
        playPause.setOnClickListener(this);
        playPauseFullScreen.setOnClickListener(this);
        next.setOnClickListener(this);
        nextSongFullScreen.setOnClickListener(this);
        previous.setOnClickListener(this);
        previousSongFullSong.setOnClickListener(this);
        closePlayer.setOnClickListener(this);
    }

    private void getCategories() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getCategories(), this, new ApiResponseHandler<List<CategoryModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<CategoryModel>>> data) {
                List<CategoryModel> categoryList = data.body().getData();
                Paper.init(HomeActivity.this);
                Paper.book().write(Constants.categoryList, categoryList);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

//public class HomeActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_news_feed,
//                R.id.navigation_live, R.id.navigation_alert,
//                R.id.navigation_settings)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
//
//        getCategories();
//    }
//
//    private void getCategories() {
//        ApiManager.apiCall(ApiClient.getInstance().getInterface().getCategories(), this, new ApiResponseHandler<List<CategoryModel>>() {
//            @Override
//            public void onSuccess(Response<ApiResponse<List<CategoryModel>>> data) {
//                List<CategoryModel> categoryList = data.body().getData();
//                Paper.init(HomeActivity.this);
//                Paper.book().write(Constants.categoryList, categoryList);
//            }
//        });
//    }
//
//}