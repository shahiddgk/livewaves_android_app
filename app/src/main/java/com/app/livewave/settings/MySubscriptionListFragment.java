package com.app.livewave.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.wavesplayer.AddSubscriptionDialogSheet;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.wavesplayer.SubscriptionListAdapter;
import com.app.livewave.models.ResponseModels.SubscriptionPlan;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

public class MySubscriptionListFragment extends Fragment implements PlayerStateListener {
    private RecyclerView recyclerView;
    public ArrayList<SubscriptionPlan> subscriptionPlans = new ArrayList<>();
    SubscriptionListAdapter subscriptionListAdapter;
    KProgressHUD dialog;
    ImageView add_subscription;
    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list_subscription, container, false);
        // Add the following lines to create RecyclerView
        setHasOptionsMenu(true);
        dialog = BaseUtils.progressDialog(view.getContext());
        getBundleData();
        add_subscription = view.findViewById(R.id.add_subscription);
        add_subscription.setOnClickListener(v -> {
            addNewSubscription();
        });
        recyclerView = view.findViewById(R.id.list_subscription_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        subscriptionListAdapter = new SubscriptionListAdapter(this, subscriptionPlans, false);
        recyclerView.setAdapter(subscriptionListAdapter);
        getSubscriptionPlans();

        return view;
    }

    void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle.containsKey("userId")) {
            userId = bundle.getInt("userId");
        }
    }

    void addNewSubscription() {
        AddSubscriptionDialogSheet addSubscriptionDialogSheet = new AddSubscriptionDialogSheet(MySubscriptionListFragment.this);
        FragmentManager fm = (getActivity()).getSupportFragmentManager();
        addSubscriptionDialogSheet.show(fm, "selectDurationDialogSheet");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).showToolbar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            addNewSubscription();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getSubscriptionPlans() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        ApiManager.apiCall(ApiClient.getInstance().getInterface().subscriptionPlans(), getContext(), data -> {
            subscriptionPlans.clear();
            subscriptionPlans.addAll(data.body().getData());
            subscriptionListAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });
    }

    @Override
    public void updatePlayerState() {

    }
}
