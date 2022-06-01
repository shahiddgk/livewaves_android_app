package com.app.livewave.BottomDialogSheets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.livewave.R;
import com.app.livewave.activities.GridViewAdapter;
import com.app.livewave.adapters.DurationListAdapter;
import com.app.livewave.interfaces.DurationOptionInterface;
import com.app.livewave.interfaces.PostOptionInterface;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class DaurationCategoryForSubscription extends BottomSheetDialogFragment {

    private PostOptionInterface durationOptionInterface;
    GridView gridView;

    String[] numberOfItems = {"1","2","3","4","5","6","7"};
    String[] durationList = {"1 Day","1 Week (7 Days)","1 Month (30 Days)","3 Month (90 Days)","6 Month (180 Days)","1 year (365 Days)","Lifetime"};

    public void addListener(PostOptionInterface durationOptionInterface) {
        this.durationOptionInterface = durationOptionInterface;
    }

    public DaurationCategoryForSubscription() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dauration_category_for_subscription, container, false);
        initViews(view);
        initClickListeners();
        DurationListAdapter durationListAdapter = new DurationListAdapter(getContext(),numberOfItems,durationList);
        gridView.setAdapter(durationListAdapter);
    return view;
    }

    private void initClickListeners() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("YOU CLICKED");
                System.out.println(durationList[i]);
                durationOptionInterface.pressed(durationList[i]);
            }
        });
    }

    private void initViews(View view) {
        gridView = view.findViewById(R.id.giff_duration_grid_view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }
}