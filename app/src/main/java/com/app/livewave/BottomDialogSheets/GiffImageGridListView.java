package com.app.livewave.BottomDialogSheets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.activities.GiffImageGridView;
import com.app.livewave.activities.GridViewAdapter;
import com.app.livewave.interfaces.PostOptionInterface;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import io.paperdb.Paper;

public class GiffImageGridListView extends BottomSheetDialogFragment {

    private PostOptionInterface postOptionInterface;
    GridView gridView;

    String[] numberOfItems = {"One","Two","Three","Four","Five","Six","Seven","eight","nine"};
    String[] ImageUrl = {"https://media.giphy.com/media/3oriO13KTkzPwTykp2/giphy.gif",
            "https://media.giphy.com/media/MwOuiiTfWfWgM/giphy.gif",
            "https://media.giphy.com/media/l0HlVV6usFV4VzOPC/giphy.gif",
            "https://media.giphy.com/media/12P29BwtrvsbbW/giphy.gif",
            "https://media.giphy.com/media/fp3h1wpfp7kFa/giphy.gif",
    "https://media.giphy.com/media/oCmX7HGo1qnzhEBiRF/giphy.gif",
    "https://media.giphy.com/media/Guccz4Oq87bncsm1j4/giphy.gif",
    "https://media.giphy.com/media/R459x856IfF6w/giphy.gif",
    "https://media.giphy.com/media/RgfGmnVvt8Pfy/giphy.gif"};

    public void addListener(PostOptionInterface postOptionInterface) {
        this.postOptionInterface = postOptionInterface;
    }

    public GiffImageGridListView() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_giff_image_grid_list_view, container, false);
        initViews(view);
        initClickListeners();
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getContext(),numberOfItems,ImageUrl);
        gridView.setAdapter(gridViewAdapter);
        return view;
    }

    private void initViews(View view) {
        gridView = view.findViewById(R.id.giff_image_grid_view);

    }
    private void initClickListeners() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("YOU CLICKED");
                System.out.println(ImageUrl[i]);
                postOptionInterface.pressed(ImageUrl[i]);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }
}