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
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.ResponseModels.WithdrawalModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> {

    List<WithdrawalModel> withdrawalModelList;
    Context context;
    UserModel userModel;
    public WalletAdapter(Context context) {
        this.context = context;
        this.withdrawalModelList = new ArrayList<>();
        Paper.init(context);
        this.userModel = Paper.book().read(Constants.currentUser);
    }

    @NonNull
    @Override
    public WalletAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WalletAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.withdrawal_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull WalletAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(withdrawalModelList.get(position).getAccountTitle());
        holder.txt_account_number.setText(withdrawalModelList.get(position).getAccount());
        holder.txt_amount.setText("$"+withdrawalModelList.get(position).getAmount());
        holder.txt_date.setText(BaseUtils.convertFromUTCTime(withdrawalModelList.get(position).getCreatedAt()));
        Glide.with(context).load(BaseUtils.getUrlforPicture(userModel.getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_profile);
        if (withdrawalModelList.get(position).getStatus() == 1){
            holder.txt_status.setText("Pending");
            holder.txt_status.setTextColor(context.getResources().getColor(R.color.buttercup));
        }else if (withdrawalModelList.get(position).getStatus() == 2){
            holder.txt_status.setText("Declined");
            holder.txt_status.setTextColor(context.getResources().getColor(R.color.quantum_vanillaredA700));
        }else if (withdrawalModelList.get(position).getStatus() == 3){
            holder.txt_status.setText("Approved");
            holder.txt_status.setTextColor(context.getResources().getColor(R.color.quantum_vanillagreenA700));
        }
    }
    @Override
    public int getItemCount() {
        return withdrawalModelList.size();
    }

    public void setList(List<WithdrawalModel> withdrawalModels) {
        this.withdrawalModelList = new ArrayList<>();
        this.withdrawalModelList = withdrawalModels;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_account_number,txt_amount,txt_date,txt_status;
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
