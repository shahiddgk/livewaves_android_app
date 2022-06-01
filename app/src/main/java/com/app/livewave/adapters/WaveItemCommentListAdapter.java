package com.app.livewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.livewave.R;
import com.app.livewave.models.ResponseModels.CommentModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.livewave.utils.BaseUtils.getUrlforPicture;

public class WaveItemCommentListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<CommentModel> commentModelList = new ArrayList<>();

    public WaveItemCommentListAdapter(Context context,List<CommentModel> commentModelList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.commentModelList = commentModelList;
    }

   public void setData(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.commentModelList = commentModelList;
   }

    @Override
    public int getCount() {
        return commentModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.wave_item_comment_item,null);
        }
        TextView commenterName = view.findViewById(R.id.tv_commenter_name);
        TextView commenterComment = view.findViewById(R.id.tv_commenter_comment);
        CircleImageView circleImageView = view.findViewById(R.id.civ_user_img_comment_bar);

        System.out.println(commentModelList.get(i).getUser().getName());
        System.out.println(commentModelList.get(i).getUser().getPhoto());
        System.out.println(commentModelList.get(i).getComment());
        Glide.with(context).load(getUrlforPicture(commentModelList.get(i).getUser().getPhoto())).placeholder(R.drawable.profile_place_holder).into(circleImageView);
        commenterName.setText(commentModelList.get(i).getUser().getName());
        commenterComment.setText(commentModelList.get(i).getComment());

        return view;
    }
}
