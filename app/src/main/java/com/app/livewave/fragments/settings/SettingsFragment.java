package com.app.livewave.fragments.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.SettingsAdapter;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.SettingsModel;
import com.app.livewave.utils.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.app.livewave.utils.Constants.HIDE_HEADER;


public class SettingsFragment extends Fragment {
    RecyclerView rv_settings;
    SettingsAdapter adapter;
    Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("Settings");
        collapsingToolbarLayout.setContentScrimColor(getContext().getResources().getColor(R.color.buttercup));
        getSettings();
        return view;
    }

    private void getSettings() {
        Paper.init(getContext());
        UserModel userModel = Paper.book().read(Constants.currentUser);
        String balance = "0.00";
        if (userModel.getBalance() != 0.00) {
            String temp = userModel.getBalance() + "";
            DecimalFormat df = new DecimalFormat("0.00");
            balance = df.format(Double.parseDouble(temp));
        }
        List<SettingsModel> settingsModelList = new ArrayList<>();
        settingsModelList.add(new SettingsModel(1,"Edit Profile","You can edit your profile", R.drawable.ic_pencil));
//        settingsModelList.add(new SettingsModel(2,"Language","You can change your language here", R.drawable.language));
//        settingsModelList.add(new SettingsModel(3,"Subscriptions","You can change your subscriptions details here", R.drawable.ic_attach_money_black_24dp));
        settingsModelList.add(new SettingsModel(4,"Wallet","You have $"+balance+" balance", R.drawable.ic_credit_card));
        settingsModelList.add(new SettingsModel(5,"Analytics","Your account & payments analytics", R.drawable.ic_outline_analytics_24));
        settingsModelList.add(new SettingsModel(6,"Invite","Invite your love ones to Livewaves", R.drawable.ic_invite));
        settingsModelList.add(new SettingsModel(7,"Contact us","Feel free to contact us", R.drawable.ic_email));
        settingsModelList.add(new SettingsModel(8,"Help","We are willing to help you", R.drawable.ic_help));
        settingsModelList.add(new SettingsModel(9,"Terms and Conditions","Feel free to study our terms", R.drawable.ic_terms_and_conditions));
        settingsModelList.add(new SettingsModel(10,"Getting Started","Learn how to get started", R.drawable.ic_flag));
        settingsModelList.add(new SettingsModel(11,"Logout","Thanks for using our app", R.drawable.ic_logout));
        adapter.setList(settingsModelList);
    }

    private void initClickListener() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
//                    startActivity(new Intent(getContext(), SearchFragment.class));

                    ((HomeActivity)getActivity()).loadFragment(R.string.tag_search, null);
                    return true;
                } else if (item.getItemId() == R.id.button_waves) {
//                    startActivity(new Intent(getContext(), SearchFragment.class));

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

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        rv_settings = view.findViewById(R.id.rv_settings);
        rv_settings.setHasFixedSize(false);
        rv_settings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new SettingsAdapter(getContext());
        rv_settings.setAdapter(adapter);
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