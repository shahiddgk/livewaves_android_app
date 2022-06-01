package com.app.livewave.BottomDialogSheets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.adapters.CategoriesAdapter;
import com.app.livewave.interfaces.OnCategorySelect;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SelectCategoryDialog extends BottomSheetDialogFragment {

    RecyclerView rv_categories;
    CategoriesAdapter adapter;
    OnCategorySelect onCategorySelect;

    public SelectCategoryDialog(OnCategorySelect onCategorySelect) {
        this.onCategorySelect = onCategorySelect;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_category_dialog_sheet, container, false);
        initViews(v);
        return v;
    }

    private void initViews(View v) {
        Paper.init(getContext());
        List<CategoryModel> categoryModelList = new ArrayList<>();
        categoryModelList = Paper.book().read(Constants.categoryList);
        rv_categories = v.findViewById(R.id.rv_categories);
        rv_categories.setHasFixedSize(false);
        rv_categories.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoriesAdapter(getContext(), onCategorySelect, this);
        rv_categories.setAdapter(adapter);
        adapter.setList(categoryModelList);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }
}