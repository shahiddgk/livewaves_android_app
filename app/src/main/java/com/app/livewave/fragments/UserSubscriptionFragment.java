package com.app.livewave.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.adapters.UserSubscriptionListAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;
import static com.app.livewave.utils.Constants.USER_ID;
import static com.app.livewave.utils.Constants.USER_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSubscriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class UserSubscriptionFragment extends Fragment implements PlayerStateListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userId;
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    ArrayList<SubscriptionModel> userSubscriptionList = new ArrayList<>();;
    KProgressHUD dialog;
    UserSubscriptionListAdapter userSubscriptionListAdapter;
    private SwipeRefreshLayout swipe_to_refresh;
    TextView tv_no_subscription;


    // TODO: Rename and change types and number of parameters
    public static UserSubscriptionFragment newInstance(String param1, String param2) {
        UserSubscriptionFragment fragment = new UserSubscriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserSubscriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_subscription, container, false);

        initComponents(view);
        getBundleData();
        getMySubscriptions();
        return view;
    }

    private void initComponents(View view) {

        recyclerView = view.findViewById(R.id.subscription_plan_list);
        tv_no_subscription = view.findViewById(R.id.tv_no_subscription);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        dialog = BaseUtils.progressDialog(getContext());
        userSubscriptionListAdapter = new UserSubscriptionListAdapter(getContext(),userSubscriptionList);

        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userSubscriptionListAdapter);
        swipe_to_refresh.setOnRefreshListener(this);

    }

    private void getBundleData() {
//        Intent intent = getIntent();
        Bundle bundle = getArguments();
        if (bundle.containsKey(SPECIFIC_USER_ID)) {
            userId = bundle.getString(SPECIFIC_USER_ID);
        }
    }

    private void setData(List<SubscriptionModel> data) {
        System.out.println("COMPILER IS HERE AFTER SUCCESS");
        if (data.size() > 0) {
            userSubscriptionList.clear();
            tv_no_subscription.setVisibility(View.GONE);
            userSubscriptionList.addAll(data);
            userSubscriptionListAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().getInterface().getSubscription(userId).cancel();
    }

    private void getMySubscriptions() {

        ApiManager.apiCall(ApiClient.getInstance().getInterface().getSubscription(userId), getContext(), new ApiResponseHandler<List<SubscriptionModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionModel>>> data) {

                swipe_to_refresh.setRefreshing(false);
                if (data != null) {
                    tv_no_subscription.setVisibility(View.GONE);
                    setData(data.body().getData());
                    dialog.dismiss();
                } else {
                    userSubscriptionList = new ArrayList<>();
                    userSubscriptionListAdapter.setList(userSubscriptionList);
                    tv_no_subscription.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public void updatePlayerState() {

    }

    @Override
    public void onRefresh() {
        getMySubscriptions();
    }
}