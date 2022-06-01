package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.InviteUserDialogSheet;
import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.RemoveImage;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.InviteToStreamEvent;
import com.app.livewave.utils.MyApplication;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;

public class MultipleImageAdapter extends RecyclerView.Adapter<MultipleImageAdapter.MyViewHolder> {

    List<Uri> uriList = new ArrayList<>();
    Context context;
    RemoveImage removeImage;
    public MultipleImageAdapter(Context context, RemoveImage removeImage) {
        this.context = context;
        this.removeImage = removeImage;
    }

    @NonNull
    @Override
    public MultipleImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MultipleImageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull MultipleImageAdapter.MyViewHolder holder, int position) {
        Glide.with(MyApplication.getAppContext()).load(uriList.get(position)).into(holder.img_uri);
        holder.img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage.removedUri(uriList.get(position));
                uriList.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public void setList(List<Uri> uris) {
        this.uriList = new ArrayList<>();
        this.uriList = uris;
        notifyDataSetChanged();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_uri, img_cancel;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_uri = itemView.findViewById(R.id.img_uri);
            img_cancel = itemView.findViewById(R.id.img_cancel);
        }
    }
}
