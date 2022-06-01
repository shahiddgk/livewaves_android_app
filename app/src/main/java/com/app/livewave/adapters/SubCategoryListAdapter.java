package com.app.livewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.SelectCategoryDialog;
import com.app.livewave.R;
import com.app.livewave.interfaces.OnCategorySelect;
import com.app.livewave.models.ResponseModels.SubcategoryModel;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryListAdapter extends RecyclerView.Adapter<SubCategoryListAdapter.viewHolder> {

    Context context;
    List<SubcategoryModel> list;
    OnCategorySelect onCategorySelect;
    SelectCategoryDialog selectCategoryDialog;
    public SubCategoryListAdapter(Context context, OnCategorySelect onCategorySelect, SelectCategoryDialog selectCategoryDialog) {
        this.context = context;
        this.onCategorySelect = onCategorySelect;
        this.selectCategoryDialog = selectCategoryDialog;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_category, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        SubcategoryModel data = list.get(position);
        holder.txt_sub_category.setText(data.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategorySelect.onSelect(list.get(position));
                selectCategoryDialog.dismiss();
            }
        });
//        holder.title.setText(data.getTitle());
//
//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                if (iCategorySelected != null) {
////                    iCategorySelected.onCategorySelectedListener(buttonView, data);
////                }
//            }
//        });

    }
    public void setList(List<SubcategoryModel> subcategoryModels){
        this.list = new ArrayList<>();
        this.list = subcategoryModels;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView txt_sub_category;
//        CheckBox checkBox;
//        MaterialRadioButton rb_subcategory;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
//            title = itemView.findViewById(R.id.custom_dialog_text);
//            checkBox = itemView.findViewById(R.id.custom_dialog_check_box);
            txt_sub_category = itemView.findViewById(R.id.txt_sub_category);
        }
    }
}
