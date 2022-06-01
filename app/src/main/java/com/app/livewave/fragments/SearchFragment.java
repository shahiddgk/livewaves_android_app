package com.app.livewave.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.R;
import com.app.livewave.adapters.PagerAdapter;
import com.app.livewave.fragments.HashtagsListFragment;
import com.app.livewave.fragments.PeopleFragment;
import com.app.livewave.fragments.SearchEventFragment;
import com.app.livewave.interfaces.SearchQueryInterface;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

public class SearchFragment extends Fragment implements PlayerStateListener {
    NestedScrollView nested_scroll_view;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout app_bar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextInputEditText et_search;
    SearchQueryInterface searchQueryInterface;
    PeopleFragment peopleFragment = new PeopleFragment();
    SearchEventFragment searchEventFragment = new SearchEventFragment();
    HashtagsListFragment hashtagsListFragment = new HashtagsListFragment();
    String query;
    Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        setHasOptionsMenu(true);
        initViews(view);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        collapsingToolbarLayout.setTitle("Search");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//        collapsingToolbarLayout.setExpandedTitleMarginTop(15);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        peopleFragment.filter(query);
                        searchEventFragment.filter(query);
                        hashtagsListFragment.filter(query);
                    }
                }, 500);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//        initViews();
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        collapsingToolbarLayout.setTitle("Search");
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//        collapsingToolbarLayout.setExpandedTitleMarginTop(15);
//
//        et_search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                query = s.toString();
//                    mHandler.removeCallbacksAndMessages(null);
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            peopleFragment.filter(query);
//                            searchEventFragment.filter(query);
//                            hashtagsListFragment.filter(query);
//                        }
//                    }, 500);
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
////                EventBus.getDefault().postSticky(new OnTextChange(true, s.toString()));
////                searchQueryInterface.onQuery(s.toString());
//            }
//        });
//    }

    public void searchData(SearchQueryInterface searchQueryInterface) {
        this.searchQueryInterface = searchQueryInterface;
    }

    private void initViews(View view) {
//        Paper.init(this);
//        userModel =Paper.book().read(Constants.currentUser);
        app_bar = view.findViewById(R.id.app_bar);
        et_search = view.findViewById(R.id.et_search);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        collapsingToolbarLayout =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        tabLayout = view.findViewById(R.id.tab);
        viewPager = view.findViewById(R.id.viewpager);
        setUpViewPager();
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager() {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());


        adapter.addFragment(peopleFragment, getActivity().getString(R.string.users));
        adapter.addFragment(searchEventFragment, getActivity().getString(R.string.events));
        adapter.addFragment(hashtagsListFragment, getActivity().getString(R.string.hashtags));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updatePlayerState() {

    }
}