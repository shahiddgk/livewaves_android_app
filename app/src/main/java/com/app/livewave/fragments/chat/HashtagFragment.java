package com.app.livewave.fragments.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.BottomDialogSheets.CreatePostDialogSheet;
import com.app.livewave.R;
import com.app.livewave.adapters.PostAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.onClickInterfaceForEditPost;
import com.app.livewave.models.ParameterModels.OnTouch;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import io.paperdb.Paper;
import retrofit2.Response;
import static com.app.livewave.utils.Constants.POST_EDIT_DIALOG;

public class HashtagFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener, PlayerStateListener {

    RecyclerView rv_hashtag;
    PostAdapter adapter;
    ProgressBar progress_bar;
    NestedScrollView nested_scroll_view;
//    Toolbar toolbar;
//    CollapsingToolbarLayout collapsingToolbarLayout;
    SwipeRefreshLayout swipe_to_refresh;
//    AppBarLayout app_bar;
    private int currentItems, totalItems, scrollOutItems;
    private int currentPageNumber = 1;
    String nextPageUrl = null;
    List<PostModel> postModelList = new ArrayList<>();
    UserModel userModel;
    String title;
    KProgressHUD dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hashtag, container, false);

        initViews(view);
//        app_bar.addOnOffsetChangedListener(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        title = getIntent().getStringExtra(Constants.HASH_TAG);
//        collapsingToolbarLayout.setTitle(title);
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));

        setHasOptionsMenu(true);
        title = getArguments().getString(Constants.HASH_TAG);

        nested_scroll_view = view.findViewById(R.id.nested_scroll_view);
        progress_bar = view.findViewById(R.id.progress_bar);
        rv_hashtag = view.findViewById(R.id.rv_hashtag);
//        ViewGroup.LayoutParams params=rv_hashtag.getLayoutParams();
//        params.height= (int) Constants.screenHeight+1;
//        rv_hashtag.setLayoutParams(params);
        rv_hashtag.setHasFixedSize(false);
        rv_hashtag.setNestedScrollingEnabled(false);
        nested_scroll_view.setNestedScrollingEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_hashtag.setLayoutManager(layoutManager);
        adapter = new PostAdapter(getActivity(), 0, Constants.HASH_TAG , rv_hashtag);
        rv_hashtag.setAdapter(adapter);
        loadPost();

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
                            loadPost();
                        }
                    }
                }
            }
        });
        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPageNumber = 1;
                postModelList.clear();
                loadPost();
                swipe_to_refresh.setRefreshing(false);
            }
        });

        adapter.setUpOnclickInterfaceForEditPost(new onClickInterfaceForEditPost() {
            @Override
            public void onClickEdit(PostModel postModel) {
                CreatePostDialogSheet createPostDialogSheet = new CreatePostDialogSheet(Constants.POST_EDIT_DIALOG, userModel.getId(), postModel);
                FragmentManager fragmentManager = getChildFragmentManager();
                createPostDialogSheet.show(fragmentManager, POST_EDIT_DIALOG);
            }
        });

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_hashtag);
//        initViews();
//        app_bar.addOnOffsetChangedListener(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        title = getIntent().getStringExtra(Constants.HASH_TAG);
//        collapsingToolbarLayout.setTitle(title);
//        collapsingToolbarLayout.setContentScrimColor(this.getResources().getColor(R.color.buttercup));
//        nested_scroll_view = findViewById(R.id.nested_scroll_view);
//        progress_bar = findViewById(R.id.progress_bar);
//        rv_hashtag = findViewById(R.id.rv_hashtag);
////        ViewGroup.LayoutParams params=rv_hashtag.getLayoutParams();
////        params.height= (int) Constants.screenHeight+1;
////        rv_hashtag.setLayoutParams(params);
//        rv_hashtag.setHasFixedSize(true);
//        rv_hashtag.setNestedScrollingEnabled(false);
//        nested_scroll_view.setNestedScrollingEnabled(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(HashtagActivity.this, LinearLayoutManager.VERTICAL, false);
//        rv_hashtag.setLayoutManager(layoutManager);
//        adapter = new PostAdapter(HashtagActivity.this, 0, Constants.HASH_TAG , rv_hashtag);
//        rv_hashtag.setAdapter(adapter);
//        loadPost();
//
//        nested_scroll_view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (v.getChildAt(v.getChildCount() - 1) != null) {
//                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                        scrollY > oldScrollY) {
//                    currentItems = layoutManager.getChildCount();
//                    totalItems = layoutManager.getItemCount();
//                    scrollOutItems = layoutManager.findFirstVisibleItemPosition();
//                    if ((currentItems + scrollOutItems) >= totalItems) {
//                        if (nextPageUrl != null) {
//                            progress_bar.setVisibility(View.VISIBLE);
//                            currentPageNumber++;
//                            loadPost();
//                        }
//                    }
//                }
//            }
//        });
//        swipe_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                currentPageNumber = 1;
//                postModelList.clear();
//                loadPost();
//                swipe_to_refresh.setRefreshing(false);
//            }
//        });
//
//        adapter.setUpOnclickInterfaceForEditPost(new onClickInterfaceForEditPost() {
//            @Override
//            public void onClickEdit(PostModel postModel) {
//                CreatePostDialogSheet createPostDialogSheet = new CreatePostDialogSheet(Constants.POST_EDIT_DIALOG, userModel.getId(), postModel);
//                FragmentManager fragmentManager = getChildFragmentManager();
//                createPostDialogSheet.show(fragmentManager, POST_EDIT_DIALOG);
//            }
//        });
//    }

    private void initViews(View view) {
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
        dialog = BaseUtils.progressDialog(getActivity());
//        app_bar = view.findViewById(R.id.app_bar);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
//        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        collapsingToolbarLayout =
//                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
    }

    private void loadPost() {
        if (currentPageNumber <= 1)
            dialog.show();
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().getPostByHashTag(title, currentPageNumber), getActivity(), new ApiResponseHandlerWithFailure<GenericDataModel<PostModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<PostModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    nextPageUrl = data.body().getData().getNextPageUrl();
                    if (currentPageNumber == 1) {
                        postModelList = new ArrayList<>();
                        postModelList = data.body().getData().getData();
                    } else {
                        postModelList.addAll(data.body().getData().getData());
                    }
                    adapter.setList(postModelList);
                    dialog.dismiss();
                    progress_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        dialog.dismiss();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeAllStickyEvents();
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRecyclerItemClick(OnTouch event) {
        if (event.isClick()) {
            rv_hashtag.requestDisallowInterceptTouchEvent(true);

        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipe_to_refresh.setEnabled(verticalOffset == 0);
    }

    @Override
    public void updatePlayerState() {

    }
}