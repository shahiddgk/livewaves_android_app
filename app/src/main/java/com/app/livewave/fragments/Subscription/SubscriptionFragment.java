package com.app.livewave.fragments.Subscription;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.livewave.R;
import com.app.livewave.fragments.Events.EventsAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.PurchasedSubscriptionModel;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.MY_SUBSCRIPTIONS;
import static com.app.livewave.utils.Constants.PURCHASED_SUBSCRIPTIONS;
import static com.app.livewave.utils.Constants.USER_ID;

public class SubscriptionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private RecyclerView rv_events;
    private LinearLayoutManager linearLayoutManager;

    private SubscriptionListAdapter subscriptionListAdapter;
    private PurchasedSubscriptionAdapter purchasedSubscriptionAdapter;

    private List<SubscriptionModel> subscriptionModelList = new ArrayList<>();
    private List<PurchasedSubscriptionModel> purchasedSubscriptionModelList = new ArrayList<>();

    private String userId, dateTime, status;
    private Context context;
    private UserModel userModel;
    private TextView tv_no_events;

    private SwipeRefreshLayout swipe_to_refresh;
    KProgressHUD dialog;

//    public SubscriptionFragment() {
//        // Required empty public constructor
//    }


    public static SubscriptionFragment newInstance(String param1) {
        SubscriptionFragment fragment = new SubscriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);
        initComponents(view);
        getIntentData();
        checkEventTypeAndSetData();
        return view;
    }

    private void getIntentData() {
        if (requireActivity().getIntent().hasExtra(USER_ID)) {
            userId = Paper.book().read("CurrentUserId");
            System.out.println(userId);
        }
        if (mParam1.equals(PURCHASED_SUBSCRIPTIONS)) {
            status = mParam1;
        }
    }

    private void checkEventTypeAndSetData() {
        dialog.show();
        //If the user click on other person event it will not get other person checked in events
        if (userModel != null && userModel.getId() != null) {
            if (userModel.getId().toString().equals(userId)) {
                getMySubscriptions();
            } else if (mParam1.equals(MY_SUBSCRIPTIONS)) {
                getMySubscriptions();
            }  else if (mParam1.equals(PURCHASED_SUBSCRIPTIONS)) {
                getPurchasedSubscriptions();
            }
        } else if (mParam1.equals(MY_SUBSCRIPTIONS)) {
            getMySubscriptions();
        } else if (mParam1.equals(PURCHASED_SUBSCRIPTIONS)) {
            getPurchasedSubscriptions();
        }
    }

    private void setData(List<SubscriptionModel> data) {
        if (data.size() > 0) {
            subscriptionModelList.clear();
            tv_no_events.setVisibility(View.GONE);
            subscriptionModelList.addAll(data);
            subscriptionListAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }

    }

    private void setPurchaseData(List<PurchasedSubscriptionModel> data) {
        if (data.size() > 0) {
            purchasedSubscriptionModelList.clear();
            tv_no_events.setVisibility(View.GONE);
            purchasedSubscriptionModelList.addAll(data);
            purchasedSubscriptionAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }

    }

    private void getMySubscriptions() {
        System.out.println("COMPILER IS HERE");
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getSubscription(userModel.getId().toString()), context, new ApiResponseHandler<List<SubscriptionModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionModel>>> data) {
                System.out.println("COMPILER IS HERE");
                swipe_to_refresh.setRefreshing(false);
                if (data != null) {
                    tv_no_events.setVisibility(View.GONE);
                    setData(data.body().getData());
                    dialog.dismiss();
                } else {
                    subscriptionModelList = new ArrayList<>();
                    subscriptionListAdapter.setList(subscriptionModelList);
                    tv_no_events.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        dialog.dismiss();
    }

    private void getPurchasedSubscriptions() {
        System.out.println("COMPILER IS HERE");
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getPurchasedSubscription(), context, new ApiResponseHandler<List<PurchasedSubscriptionModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<PurchasedSubscriptionModel>>> data) {
                System.out.println("COMPILER IS HERE");
                swipe_to_refresh.setRefreshing(false);
                if (data != null) {
                    tv_no_events.setVisibility(View.GONE);
                    setPurchaseData(data.body().getData());
                    dialog.dismiss();
                } else {
                    purchasedSubscriptionModelList = new ArrayList<>();
                    purchasedSubscriptionAdapter.setList(purchasedSubscriptionModelList);
                    tv_no_events.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        dialog.dismiss();
    }


    private void initComponents(View view) {
        Paper.init(context);
        userModel = Paper.book().read(Constants.currentUser);
        dialog = BaseUtils.progressDialog(getContext());
        rv_events = view.findViewById(R.id.rv_subscription_plans);
        tv_no_events = view.findViewById(R.id.tv_no_subscription);
        swipe_to_refresh = view.findViewById(R.id.swipe_to_refresh);
        subscriptionListAdapter = new SubscriptionListAdapter(context, subscriptionModelList);
        purchasedSubscriptionAdapter = new PurchasedSubscriptionAdapter(context,purchasedSubscriptionModelList);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv_events.setLayoutManager(linearLayoutManager);
        if (mParam1.equals(PURCHASED_SUBSCRIPTIONS)) {
            rv_events.setAdapter(purchasedSubscriptionAdapter);
        } else {
            rv_events.setAdapter(subscriptionListAdapter);
        }
        swipe_to_refresh.setOnRefreshListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onRefresh() {
        checkEventTypeAndSetData();
    }

    @Override
    public void onDestroy() {

        ApiClient.getInstance().getInterface().getPurchasedSubscription().cancel();
        ApiClient.getInstance().getInterface().getSubscription(userModel.getId().toString()).cancel();

        dialog.dismiss();
        super.onDestroy();
    }
}