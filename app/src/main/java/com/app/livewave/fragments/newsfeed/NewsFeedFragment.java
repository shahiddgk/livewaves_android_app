package com.app.livewave.fragments.newsfeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.LoginActivity;
import com.app.livewave.activities.LoginActivityWithWavesFeature;
import com.app.livewave.activities.PaymentActivity;
import com.app.livewave.activities.RegisterActivity;
import com.app.livewave.activities.TikTokReels;
import com.app.livewave.adapters.HashtagTrendListAdapter;
import com.app.livewave.adapters.PostAdapter;
import com.app.livewave.adapters.SuggestedPeopleListAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ParameterModels.HashtagsModel;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.SuggestedPeopleModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.errorprone.annotations.Var;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.HEADER_TITLE;
import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.NEWS_FEED;

public class NewsFeedFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    private RecyclerView rv_news_feed;
    private PostAdapter adapter;
    private RecyclerView rv_trends,rv_Suggested_People;
    private HashtagTrendListAdapter hashtagTrendListAdapter;
    private SuggestedPeopleListAdapter suggestedPeopleListAdapter;

    private Toolbar toolbar;
    private ProgressBar progress_bar;
    private AppBarLayout app_bar;
    private List<PostModel> posts = new ArrayList<>();
    private List<HashtagsModel> hashtagsModelList = new ArrayList<>();
    private List<SuggestedPeopleModel> suggestedPeopleModelList = new ArrayList<>();
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    //    private int totalPages = 0;
    String nextPageUrl = null;
    private NestedScrollView nested_scroll_view;
    private SwipeRefreshLayout swipe_to_refresh;
    private UserModel userModel;
    KProgressHUD dialog;
    Button btPaid, bt_payment;
    TextView btReels;

    List myList = new ArrayList();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        initViews(view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("News Feed");
        collapsingToolbarLayout.setContentScrimColor(getContext().getResources().getColor(R.color.buttercup));
        return view;
    }

    private void initClickListener() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
//                    startActivity(new Intent(getContext(), SearchFragment.class));

                    ((HomeActivity)getActivity()).loadFragment(R.string.tag_search, null);
                    return true;
                }
                if (item.getItemId() == R.id.button_waves) {

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(HIDE_HEADER, false);
                    ((HomeActivity)getActivity()).loadFragment(R.string.tag_waves_Features, bundle);
                    return true;
                }
                return false;
            }
        });
    }

    private void initViews(View view) {
        Paper.init(getContext());
        userModel = Paper.book().read(Constants.currentUser);
        app_bar = view.findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(this);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        dialog = BaseUtils.progressDialog(getContext());
        rv_trends = view.findViewById(R.id.rv_trends);
        rv_Suggested_People = view.findViewById(R.id.rv_suggested_people);
        rv_trends.setHasFixedSize(false);
        rv_Suggested_People.setHasFixedSize(false);
        LinearLayoutManager trendLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_trends.setLayoutManager(trendLinearLayoutManager);
        LinearLayoutManager suggestLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_Suggested_People.setLayoutManager(suggestLinearLayoutManager);
        hashtagTrendListAdapter = new HashtagTrendListAdapter(getContext(), hashtagsModelList, 0);
        suggestedPeopleListAdapter = new SuggestedPeopleListAdapter(getContext(),suggestedPeopleModelList);

        rv_trends.setAdapter(hashtagTrendListAdapter);
        rv_trends.setNestedScrollingEnabled(false);
        rv_Suggested_People.setAdapter(suggestedPeopleListAdapter);
        rv_Suggested_People.setNestedScrollingEnabled(false);
        btPaid = view.findViewById(R.id.bt_Paid);
//        btReels = view.findViewById(R.id.bt_Reels);
        bt_payment = view.findViewById(R.id.bt_payment);

        rv_news_feed = view.findViewById(R.id.rv_news_feed);
////        ViewGroup.LayoutParams params=rv_news_feed.getLayoutParams();
////        params.height= (int) Constants.screenHeight+1;
//        rv_news_feed.setLayoutParams(params);
        rv_news_feed.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_news_feed.setLayoutManager(layoutManager);
        rv_news_feed.setNestedScrollingEnabled(false);
        nested_scroll_view.setNestedScrollingEnabled(true);
        adapter = new PostAdapter(getContext(), userModel.getId(), NEWS_FEED, rv_news_feed);
        rv_news_feed.setAdapter(adapter);


        loadTags();
        loadSuggestedPeople();
        loadFeed();
        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    currentItems = layoutManager.getChildCount();
                    totalItems = layoutManager.getItemCount();
                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
                    if ((currentItems + scrollOutItems) >= totalItems) {
                        if (nextPageUrl != null) {
                            progress_bar.setVisibility(View.VISIBLE);
                            currentPageNumber++;
                            loadFeed();
                        }
                    }
                }
            }
        });
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                posts.clear();
                suggestedPeopleModelList.clear();
                loadFeed();
                loadSuggestedPeople();
                swipe_to_refresh.setRefreshing(false);
            }
        });

        bt_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });

        btPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), PaidEventAndPaidPostFragment.class);
//                startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString(HEADER_TITLE, "Paid");
                ((HomeActivity)getActivity()).loadFragment(R.string.tag_paid_event_paid_post, bundle);
            }
        });

//        btReels.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putBoolean(HIDE_HEADER, false);
//                ((HomeActivity)getActivity()).loadFragment(R.string.tag_waves_Features, bundle);
//  //              startActivity(new Intent(getActivity(), WavesFeatureActivity.class));
//            }
//        });
    }

    private void loadTags() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getTrendingHashtags(), getContext(), new ApiResponseHandler<List<HashtagsModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<HashtagsModel>>> data) {
                if (data != null && data.body().getData().size() > 0) {
                    setTrendsData(data.body().getData());
                }
            }
        });

    }
    private void loadSuggestedPeople() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getSuggestPeople(), getContext(), new ApiResponseHandler<List<SuggestedPeopleModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SuggestedPeopleModel>>> data) {
                if (data != null && data.body().getData().size() > 0) {
                    setSuggestedPeopleData(data.body().getData());
                }
            }
        });

    }

    private void setTrendsData(List<HashtagsModel> data) {
        rv_trends.setVisibility(View.VISIBLE);
        hashtagsModelList.clear();
        hashtagsModelList.addAll(data);
        hashtagTrendListAdapter.notifyDataSetChanged();
    }

    private void setSuggestedPeopleData(List<SuggestedPeopleModel> data) {

        suggestedPeopleModelList.clear();
        suggestedPeopleModelList.addAll(data);
        suggestedPeopleListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        loadFeed();
        super.onResume();
    }

    private void loadFeed() {
        if (currentPageNumber <= 1)
            dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getNewsFeed(currentPageNumber), getContext(), new ApiResponseHandler<GenericDataModel<PostModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<PostModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
//                    totalPages = data.body().getData().getLastPage();
                    if (currentPageNumber == 1) {
                        posts = new ArrayList<>();
                        posts = data.body().getData().getData();
                    } else {
                        posts.addAll(data.body().getData().getData());
                    }
                    Activity activity = getActivity();
                    if (isAdded() && activity!=null) {
                        adapter.setList(posts);
                        dialog.dismiss();
                        progress_bar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        if (dialog != null)
            dialog.dismiss();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeAllStickyEvents();
        ApiClient.getInstance().getInterface().getTrendingHashtags().cancel();
        ApiClient.getInstance().getInterface().getNewsFeed(currentPageNumber).cancel();
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRecyclerItemClick(OnTouch event) {
        if (event.isClick()) {
            rv_news_feed.requestDisallowInterceptTouchEvent(true);

        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipe_to_refresh.setEnabled(verticalOffset == 0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickListener();
    }
}