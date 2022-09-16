package com.app.livewave.adapters;

import static com.app.livewave.utils.Constants.HEADER_TITLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.fragments.chat.ChatFragment;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.InboxModel;
import com.app.livewave.models.MembersInfo;
import com.app.livewave.models.MessageModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

    List<InboxModel> inboxList;
    Context context;
    int currentUserId;
    FirebaseFirestore rootRef;
    public static int clickedChatPosition;


    public InboxAdapter(Context context, Integer id) {
        this.context = context;
        this.inboxList = new ArrayList<>();
        this.currentUserId = id;
        rootRef = FirebaseFirestore.getInstance();


    }

    @NonNull
    @Override
    public InboxAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InboxAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.inbox_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InboxAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txt_last_message.setText(inboxList.get(position).getLastMessage());
        holder.txt_date.setText(BaseUtils.getTimeFromMiliSecond(inboxList.get(position).getSentAt()));

        if (!inboxList.get(position).getTitle().equals("")) {
            Glide.with(context).load(R.drawable.profile_place_holder).into(holder.img_picture);
            holder.txt_name.setText(inboxList.get(position).getTitle());
        } else {
            for (int i = 0; i < inboxList.get(position).getMembersInfo().size(); i++) {
                if (inboxList.get(position).getMembersInfo().get(i).getId() != currentUserId) {
                    holder.txt_name.setText(inboxList.get(position).getMembersInfo().get(i).getName());
                    Glide.with(context).load(inboxList.get(position).getMembersInfo().get(i).getPhoto()).placeholder(R.drawable.profile_place_holder).into(holder.img_picture);
                }
            }
        }

        if (inboxList.get(position).title.equals("")) {
            holder.iv_delete.setVisibility(View.VISIBLE);
        } else {
            holder.iv_delete.setVisibility(View.GONE);
        }

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseUtils.showAlertDialog("Delete Chat", "Are you sure you want to delete this chat", context, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                        if (positive) {
                            rootRef.collection(Constants.firebaseDatabaseRoot).document(inboxList.get(position).getId()).collection("Messages").document(inboxList.get(position).lastMessageId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    MessageModel messageModel = value.toObject(MessageModel.class);

                                    if (messageModel.getDeleteMessage() != null) {
                                        for (int i = 0; i < messageModel.getDeleteMessage().size(); i++) {
                                            if (currentUserId == messageModel.getDeleteMessage().get(i)) {
                                                messageModel.getDeleteMessage().set(i, 0);
                                                List<Integer> deleteMessage = new ArrayList<>(messageModel.getDeleteMessage());
                                                rootRef.collection(Constants.firebaseDatabaseRoot).document(inboxList.get(position).getId()).collection("Messages").document(inboxList.get(position).lastMessageId).update("deleteMessage", deleteMessage);
                                                List<MembersInfo> membersInfo = new ArrayList<>();
                                                membersInfo.addAll(inboxList.get(position).getMembersInfo());
                                                for (int j = 0; j < membersInfo.size(); j++) {
                                                    if (currentUserId == membersInfo.get(j).getId()) {
                                                        membersInfo.get(j).setType("delete");
                                                    }
                                                }
                                                rootRef.collection(Constants.firebaseDatabaseRoot).document(inboxList.get(position).getId()).update("membersInfo", membersInfo);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String myJson = gson.toJson(inboxList.get(position));
//                Intent intent = new Intent(context, ChatFragment.class);
//                intent.putExtra("inboxModel", myJson);
//                context.startActivity(intent);

                clickedChatPosition = position;

                Bundle bundle = new Bundle();
                bundle.putString("inboxModel", myJson);
                if (!inboxList.get(position).getTitle().equals("")) {
                    bundle.putString(HEADER_TITLE, inboxList.get(position).getTitle());
                } else {
                    for (int i = 0; i < inboxList.get(position).getMembersInfo().size(); i++) {
                        UserModel userModel = Paper.book().read(Constants.currentUser);
                        if (inboxList.get(position).getMembersInfo().get(i).getId() != userModel.getId()) {
                            bundle.putString(HEADER_TITLE, inboxList.get(position).getMembersInfo().get(i).getName());
                            Log.e("name", "onClick: " + inboxList.get(position).getMembersInfo().get(i).getName() );
                        }
                    }
                }
                ((HomeActivity) context).loadFragment(R.string.tag_chat, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public void setList(List<InboxModel> inboxModelList) {
        this.inboxList = new ArrayList<>();
        this.inboxList = inboxModelList;
        notifyDataSetChanged();
        for (int i = 0; i < inboxList.size(); i++) {
            Log.e("inbox members", "InboxAdapter: " + inboxList.get(i).members );
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_date, txt_last_message;
        CircleImageView img_picture;
        ImageView iv_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_last_message = itemView.findViewById(R.id.txt_last_message);
            img_picture = itemView.findViewById(R.id.img_picture);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }
    }
}