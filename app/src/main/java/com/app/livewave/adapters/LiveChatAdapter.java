package com.app.livewave.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.models.ParameterModels.StreamChatModel;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;

public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.ViewHolder> {

    Context context;
    List<StreamChatModel> arrayList = new ArrayList<>();

    public LiveChatAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StreamChatModel chatModel = arrayList.get(position);

        Glide.with(context).load(getUrlforPicture(chatModel.getImage())).into(holder.img_chat_user);
        holder.tv_chat_message.setText(chatModel.getMessage());
        holder.tv_chat_user_name.setText(chatModel.getUsername());
    }

    public void setList(List<StreamChatModel> streamChatModels) {
        this.arrayList = new ArrayList<>();
        this.arrayList = streamChatModels;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_chat_user;
        TextView tv_chat_message, tv_chat_user_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_chat_user = itemView.findViewById(R.id.img_chat_user);
            tv_chat_message = itemView.findViewById(R.id.tv_chat_message);
            tv_chat_user_name = itemView.findViewById(R.id.tv_chat_user_name);
        }
    }
}
