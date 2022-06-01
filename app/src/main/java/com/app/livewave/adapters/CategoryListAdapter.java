package com.app.livewave.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.models.ResponseModels.SubcategoryModel;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.app.livewave.utils.Constants.CATEGORY;
import static com.app.livewave.utils.Constants.CATEGORY_ID;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.viewHolder> {

    private Activity activity;
    private List<CategoryModel> list;

    private final String[] COLORS = {"#EDBB1F", "#64c2ca", "#f38051", "#8060a9", "#ab4c9d", "#da1f26", "#7bc043"};


    public CategoryListAdapter(List<CategoryModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        CategoryModel categoryData = list.get(position);
        String color = COLORS[position % 7];
        holder.ll_back.setBackgroundColor(Color.parseColor(color));
        List<SubcategoryModel> subCategoryList = categoryData.getSubcategoryModelList();
        holder.tv_category.setText(categoryData.getTitle());

//        if (subCategoryList.size() > 0) {
//            for (int i = 0; i < subCategoryList.size(); i++) {
//                SubCategoryListAdapter adapter = new SubCategoryListAdapter(subCategoryList);
//                holder.rv_subcategory.hasFixedSize();
//                holder.rv_subcategory.setLayoutManager(new LinearLayoutManager(activity));
//                holder.rv_subcategory.setAdapter(adapter);
//                adapter.setOnCategorySelectedListener(new SubCategoryListAdapter.ICategorySelected() {
//                    @Override
//                    public void onCategorySelectedListener(View view, SubcategoryModel subCategory) {
//                        Intent intent = new Intent();
//                        intent.putExtra(CATEGORY, categoryData.getTitle());
//                        intent.putExtra(CATEGORY_ID, categoryData.getId().toString());
//                        intent.putExtra(SUB_CATEGORY_ID, subCategory.getId().toString());
//                        intent.putExtra(SUB_CATEGORY, subCategory.getTitle());
//
//                        activity.setResult(RESULT_OK, intent);
//                        activity.finish();
//                    }
//                });
//            }
//        }

        holder.tv_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(CATEGORY, categoryData.getTitle());
                intent.putExtra(CATEGORY_ID, categoryData.getId().toString());
                activity.setResult(RESULT_OK, intent);
                activity.finish();

//                if (holder.rv_subcategory.getVisibility() == View.VISIBLE)
//                    holder.rv_subcategory.setVisibility(View.GONE);
//                else
//                    holder.rv_subcategory.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        TextView tv_category;
        RecyclerView rv_subcategory;
        LinearLayout ll_back;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category = itemView.findViewById(R.id.tv_category);
            rv_subcategory = itemView.findViewById(R.id.rv_subcategory);
            ll_back = itemView.findViewById(R.id.ll_back);

        }


    }
}
