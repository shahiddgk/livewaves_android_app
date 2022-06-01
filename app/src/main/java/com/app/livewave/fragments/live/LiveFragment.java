package com.app.livewave.fragments.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.PagerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import static com.app.livewave.utils.Constants.FOLLOWING;
import static com.app.livewave.utils.Constants.GLOBAL_STREAMS;


public class LiveFragment extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_live, container, false);
    }

    private void initView(View view) {
        viewPager = view.findViewById(R.id.viewpager_event_list);
        tabLayout = view.findViewById(R.id.tl_global_following);
        toolbar = view.findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout =
                view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.Live));
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.buttercup));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());

        adapter.addFragment(LiveStreamsListFragment.newInstance(GLOBAL_STREAMS), GLOBAL_STREAMS);
        adapter.addFragment(LiveStreamsListFragment.newInstance(FOLLOWING), FOLLOWING);

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setClickListener();

    }

    private void setClickListener() {

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_live) {
//                    startActivity(new Intent(getContext(), CreateStreamFragment.class));

                    ((HomeActivity) getActivity()).loadFragment(R.string.tag_create_stream, null);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.stream_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

}
