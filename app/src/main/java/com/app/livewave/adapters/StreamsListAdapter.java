package com.app.livewave.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.PublisherActivity;
import com.app.livewave.activities.SubscriberActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;

public class StreamsListAdapter extends RecyclerView.Adapter<StreamsListAdapter.Holder> {
    private Context context;
    private List<StreamModel> streamModelList;
    private UserModel userModel;
    KProgressHUD dialog;
    Activity activity;

    public StreamsListAdapter(Context context, Activity activity) {
        this.context = context;
        this.streamModelList = new ArrayList<>();
        Paper.init(context);
        userModel = Paper.book().read(Constants.currentUser);
        dialog = BaseUtils.progressDialog(context);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_streams, parent, false);
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        StreamModel streamModel = streamModelList.get(position);

        //Set stream name title etc
        holder.tv_name.setText(streamModel.getStreamerName());
//        BaseUtils.setVerifiedAccount(streamModel);

        holder.tv_stream_title.setText(streamModel.getTitle());
        Glide.with(context).load(getUrlforPicture(streamModel.getStreamerPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.img_user);

        //If stream is  free show join  on button else show amount
        if (streamModel.getAmount() > 0 && streamModel.getIsPaid() == 0 && Constants.APPENV != ENV.review) {
            holder.iv_paid.setVisibility(View.VISIBLE);
            holder.btn_join.setText(context.getString(R.string.pay_paid_amount, String.valueOf(streamModel.getAmount())));
        } else {
            holder.btn_join.setText(R.string.join);
            holder.iv_paid.setVisibility(View.GONE);

        }

        //If stream is created by the person searching he can resume it's stream
        if (streamModel.getUserId().equals(userModel.getId())) {
            holder.btn_join.setText(R.string.resume);
        }

        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).openUserProfile(streamModel.getUserId().toString());
//                BaseUtils.openUserProfile(streamModel.getUserId().toString(), context);
            }
        });

        holder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).openUserProfile(streamModel.getUserId().toString());
//                BaseUtils.openUserProfile(streamModel.getUserId().toString(), context);
            }
        });

        holder.btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                    handleClickOnJoin(streamModel);
                } else {
                    Toast.makeText(context, "Permission Denied -> Go to Setting -> Allow Permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{
                                    android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.RECORD_AUDIO},
                            1);
                }
            }
        });
    }


    public void setList(List<StreamModel> streamModelList) {
        this.streamModelList = new ArrayList<>();
        this.streamModelList = streamModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return streamModelList.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        ImageView img_user, iv_paid;
        TextView tv_name, tv_stream_title;
        Button btn_join;

        public Holder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_user);
            iv_paid = itemView.findViewById(R.id.iv_paid);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_stream_title = itemView.findViewById(R.id.tv_stream_title);
            btn_join = itemView.findViewById(R.id.btn_join);
        }
    }

    private void handleClickOnJoin(StreamModel streamModel) {
//        dialog.show();
        ((HomeActivity)context).showProgressDialog();
        //if cheater of stream click on button
        if (streamModel.getUserId().equals(userModel.getId())) {
            Intent intent = new Intent(context, PublisherActivity.class);
            intent.putExtra("ID", streamModel.getId());
            intent.putExtra("TITLE", streamModel.getTitle());
            intent.putExtra("PLATFORM_ID", streamModel.getPlatformID());
//            dialog.dismiss();
            ((HomeActivity)context).hideProgressDialog();
            context.startActivity(intent);
//            Toast.makeText(context, "Resume Your Stream", Toast.LENGTH_SHORT).show();
        }
        //if stream is paid
        else if (streamModel.getAmount() > 0 && streamModel.getIsPaid() == 0) {
            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra("id", "stream_id=" + streamModel.getId());
            intent.putExtra("type", "stream");
            intent.putExtra("intent_type", "6");
            dialog.dismiss();
            context.startActivity(intent);

//            Bundle bundle = new Bundle();
//            bundle.putString("id", "stream_id=" + streamModel.getId());
//            bundle.putString("type", "stream");
//            bundle.putString("intent_type", "6");
//            ((HomeActivity) context).loadFragment(R.string.tag_webview, bundle);
            //((HomeActivity)context).hideProgressDialog();
        }
        //if stream is free or the person already paid of the stream
        else {
            Intent intent = new Intent(context, SubscriberActivity.class);
            intent.putExtra("ID", streamModel.getId().toString());
            intent.putExtra("TITLE", streamModel.getTitle());
            intent.putExtra("PLATFORM_ID", streamModel.getPlatformID());
            intent.putExtra("STREAM_ID_TYPE", "stream_id");
            intent.putExtra("Subscriber", "Subscriber");
            dialog.dismiss();
            context.startActivity(intent);
            Toast.makeText(context, "Stream is already paid or free", Toast.LENGTH_SHORT).show();

//            Bundle bundle = new Bundle();
//            bundle.putString("ID", streamModel.getId().toString());
//            bundle.putString("TITLE", streamModel.getTitle());
//            bundle.putString("PLATFORM_ID", streamModel.getPlatformID());
//            bundle.putString("STREAM_ID_TYPE", "stream_id");
//            bundle.putString("Subscriber", "Subscriber");
//            bundle.putBoolean(Constants.SHOW_HEADER, false);
//
//            ((HomeActivity) context).loadFragment(R.string.tag_subscriber, bundle);
//            ((HomeActivity)context).hideProgressDialog();
        }
    }
}
