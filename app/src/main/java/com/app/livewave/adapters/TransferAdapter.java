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
import com.app.livewave.models.ResponseModels.TransferModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.MyViewHolder> {

    List<TransferModel> transferModelList;
    Context context;
    UserModel userModel;

    public TransferAdapter(Context context) {
        this.context = context;
        this.transferModelList = new ArrayList<>();
        Paper.init(context);
        this.userModel = Paper.book().read(Constants.currentUser);
    }

    @NonNull
    @Override
    public TransferAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransferAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.receivings_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull TransferAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(userModel.getName());
        holder.txt_account_number.setText(transferModelList.get(position).getTransactionID());
        holder.txt_amount.setText("$" + (transferModelList.get(position).getAmount() - transferModelList.get(position).getTax()));
        holder.txt_date.setText(BaseUtils.convertFromUTCTime(transferModelList.get(position).getCreatedAt()));
        Glide.with(context).load(BaseUtils.getUrlforPicture(userModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        holder.txt_status.setText(transferModelList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return transferModelList.size();
    }

    public void setList(List<TransferModel> transferModels) {
        this.transferModelList = new ArrayList<>();
        this.transferModelList = transferModels;
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
