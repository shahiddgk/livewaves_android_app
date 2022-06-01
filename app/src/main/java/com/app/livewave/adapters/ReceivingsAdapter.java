package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.models.ResponseModels.ReceivingModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ReceivingsAdapter extends RecyclerView.Adapter<ReceivingsAdapter.MyViewHolder> {

    List<ReceivingModel> receivingModelList;
    Context context;
    UserModel userModel;

    public ReceivingsAdapter(Context context) {
        this.context = context;
        this.receivingModelList = new ArrayList<>();
        Paper.init(context);
        this.userModel = Paper.book().read(Constants.currentUser);
    }

    @NonNull
    @Override
    public ReceivingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReceivingsAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.receivings_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ReceivingsAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(receivingModelList.get(position).getName());
        holder.txt_account_number.setText(receivingModelList.get(position).getTransactionID());
        holder.txt_amount.setText("$" + (receivingModelList.get(position).getAmount() - receivingModelList.get(position).getTax()));
        holder.txt_date.setText(BaseUtils.convertFromUTCTime(receivingModelList.get(position).getCreatedAt()));
        Glide.with(context).load(BaseUtils.getUrlforPicture(receivingModelList.get(position).getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.txt_status.setText(receivingModelList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return receivingModelList.size();
    }

    public void setList(List<ReceivingModel> receivingModels) {
        this.receivingModelList = new ArrayList<>();
        this.receivingModelList = receivingModels;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_account_number, txt_amount, txt_date, txt_status;
        CircleImageView img_profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            img_profile = itemView.findViewById(R.id.img_profile);
            txt_account_number = itemView.findViewById(R.id.txt_account_number);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_status = itemView.findViewById(R.id.txt_status);
        }
    }
}
