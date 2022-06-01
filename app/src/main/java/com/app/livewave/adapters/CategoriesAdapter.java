package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.SelectCategoryDialog;
import com.app.livewave.R;
import com.app.livewave.interfaces.OnCategorySelect;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.models.ResponseModels.SubcategoryModel;

import java.util.ArrayList;
import java.util.List;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    List<CategoryModel> categoryModelList = new ArrayList<>();
    Context context;
    SubCategoryListAdapter subCategoryListAdapter;
    OnCategorySelect onCategorySelect;
    SelectCategoryDialog selectCategoryDialog;

    public CategoriesAdapter(Context context, OnCategorySelect onCategorySelect, SelectCategoryDialog selectCategoryDialog) {
        this.context = context;
        this.onCategorySelect = onCategorySelect;
        this.selectCategoryDialog = selectCategoryDialog;
    }

    @NonNull
    @Override
    public CategoriesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoriesAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_item_category, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.MyViewHolder holder, int position) {
        holder.tv_category.setText(categoryModelList.get(position).getTitle());
        if (categoryModelList.get(position).isSelected()){
            holder.rv_subcategory.setVisibility(View.VISIBLE);
        }else {
            holder.rv_subcategory.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryModelList.get(position).isSelected()){
                    categoryModelList.get(position).setSelected(false);
                }else {
                    for (int i = 0; i < categoryModelList.size(); i++) {
                        categoryModelList.get(i).setSelected(false);
                    }
                    categoryModelList.get(position).setSelected(true);
                }
                notifyDataSetChanged();
            }
        });
        setRecyclerData(holder.rv_subcategory, categoryModelList.get(position).getSubcategoryModelList());
    }

    private void setRecyclerData(RecyclerView rv_subcategory, List<SubcategoryModel> subcategoryModelList) {
        rv_subcategory.setHasFixedSize(false);
        rv_subcategory.setLayoutManager(new LinearLayoutManager(context));
        subCategoryListAdapter = new SubCategoryListAdapter(context, onCategorySelect, selectCategoryDialog);
        rv_subcategory.setAdapter(subCategoryListAdapter);
        subCategoryListAdapter.setList(subcategoryModelList);
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public void setList(List<CategoryModel> categoryModels) {
        this.categoryModelList = new ArrayList<>();
        this.categoryModelList = categoryModels;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category;
        RecyclerView rv_subcategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category = itemView.findViewById(R.id.tv_category);
            rv_subcategory = itemView.findViewById(R.id.rv_subcategory);
        }
    }
}