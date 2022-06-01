package com.app.livewave.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.MembersInfo;
import com.app.livewave.models.MessageModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<MessageModel> messageList = new ArrayList<>();
    Context context;
    int MSG_TYPE_LEFT = 0;
    int MSG_TYPE_RIGHT = 1;
    UserModel userModel;
    List<MembersInfo> membersInfo = new ArrayList();
    MyViewHolder holder1;
    MediaPlayer player1;
    FirebaseFirestore rootRef;
    String rootId;


    public MessageAdapter(Context context, List<MembersInfo> membersInfo, String rootId) {
        this.context = context;
        Paper.init(context);
        this.userModel = Paper.book().read(Constants.currentUser);
        this.membersInfo = membersInfo;
        rootRef = FirebaseFirestore.getInstance();
        this.rootId = rootId;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            return new MessageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_chat_right, parent, false));
        } else {
            return new MessageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_chat_left, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (messageList.get(position).getAttachmentType() == 0) {
            holder.rl_audio_view.setVisibility(View.GONE);
            holder.img_picture.setVisibility(View.GONE);
            holder.txt_msg.setVisibility(View.VISIBLE);
        } else if (messageList.get(position).getAttachmentType() == 1) {
            holder.rl_audio_view.setVisibility(View.GONE);
            holder.img_picture.setVisibility(View.VISIBLE);
            holder.txt_msg.setVisibility(View.GONE);
        } else {
            holder.rl_audio_view.setVisibility(View.VISIBLE);
            holder.img_picture.setVisibility(View.GONE);
            holder.txt_msg.setVisibility(View.GONE);
            audioMessage(position, holder.seek_bar, holder.txt_duration, holder.img_play, holder.img_pause , holder);
        }
        for (int i = 0; i < membersInfo.size(); i++) {
            if (membersInfo.get(i).getId() == messageList.get(position).getSenderId()) {
                if (messageList.get(position).getSenderId() != userModel.getId()) {
                    Glide.with(context).load(membersInfo.get(i).getPhoto()).placeholder(R.drawable.profile_place_holder).into(holder.img_user_profile);
                }
            }
        }
        holder.txt_msg.setText(messageList.get(position).message);
        Glide.with(context).load(messageList.get(position).attachment).into(holder.img_picture);
        holder.txt_message_time.setText(BaseUtils.getTimeFromMiliSecond(messageList.get(position).getSentAt()));

        if (messageList.get(position).senderId == userModel.getId()) {
            holder.layout_chat_right.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    BaseUtils.showAlertDialog("Delete Chat", "Are you sure you want to delete this chat", context, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {
                            if (positive) {
                                rootRef.collection(Constants.firebaseDatabaseRoot).document(rootId).collection("Messages").document(messageList.get(position).id).update("attachmentType", 0);
                                rootRef.collection(Constants.firebaseDatabaseRoot).document(rootId).collection("Messages").document(messageList.get(position).id).update("message", "This message was deleted");
                            }
                        }
                    });
                    return false;
                }
            });
        }
    }

    private void audioMessage(int position, AppCompatSeekBar seek_bar, TextView txt_duration, ImageView img_play, ImageView img_pause, MyViewHolder holder) {
        Handler handler = new Handler();
        Runnable runnable;
        MediaPlayer player = new MediaPlayer();

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(messageList.get(position).getAttachment());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                seek_bar.setProgress(player.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };

        String totalDuration = convertFormat(player.getDuration());
        txt_duration.setText(totalDuration);

        Runnable finalRunnable = runnable;
        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder1 != null){
                    holder1.img_play.setVisibility(View.VISIBLE);
                    holder1.img_pause.setVisibility(View.GONE);
                    player1.pause();
                }

                img_pause.setVisibility(View.VISIBLE);
                img_play.setVisibility(View.GONE);
                player.start();
                seek_bar.setMax(player.getDuration());
                handler.postDelayed(finalRunnable, 0);
                holder1 = holder;
                player1 = player;
            }
        });
        img_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_pause.setVisibility(View.GONE);
                img_play.setVisibility(View.VISIBLE);

                player.pause();
//                handler.removeCallbacks(finalRunnable);
            }
        });
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    player.seekTo(i);
                }
                txt_duration.setText(convertFormat(player.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                img_pause.setVisibility(View.GONE);
                img_play.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setList(List<MessageModel> messageModelList) {
        this.messageList = new ArrayList<>();
        this.messageList = messageModelList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_msg, txt_message_time, txt_duration, txt_name;
        CircleImageView img_user_profile;
        ImageView img_picture, img_play, img_pause;
        AppCompatSeekBar seek_bar;
        RelativeLayout rl_audio_view;
        LinearLayout layout_chat_right;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_msg = itemView.findViewById(R.id.txt_msg);
            txt_message_time = itemView.findViewById(R.id.txt_message_time);
            txt_duration = itemView.findViewById(R.id.txt_duration);
            img_user_profile = itemView.findViewById(R.id.img_user_profile);
            img_picture = itemView.findViewById(R.id.img_picture);
            img_play = itemView.findViewById(R.id.img_play);
            img_pause = itemView.findViewById(R.id.img_pause);
            seek_bar = itemView.findViewById(R.id.seek_bar);
            rl_audio_view = itemView.findViewById(R.id.rl_audio_view);
            layout_chat_right = itemView.findViewById(R.id.layout_chat_right);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).senderId == userModel.getId()) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}