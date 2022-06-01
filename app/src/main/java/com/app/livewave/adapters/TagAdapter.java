package com.app.livewave.adapters;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.livewave.R;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.bumptech.glide.Glide;
import com.hendraanggrian.appcompat.widget.SocialArrayAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TagAdapter extends SocialArrayAdapter<UserModel> {

    private final int defaultAvatar;
    public TagAdapter(@NonNull Context context) {
        this(context, R.drawable.socialview_ic_mention_placeholder);
    }

    public TagAdapter(@NonNull Context context, @DrawableRes int defaultAvatar) {
        super(context, R.layout.tag_item, R.id.txt_username);
        this.defaultAvatar = defaultAvatar;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final TagAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_item, parent, false);
            holder = new TagAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TagAdapter.ViewHolder) convertView.getTag();
        }
        final UserModel item = getItem(position);
        if (item != null) {
            holder.usernameView.setText(item.getUsername());

            final CharSequence displayname = item.getName();
            if (!TextUtils.isEmpty(displayname)) {
                holder.displaynameView.setText(displayname);
                holder.displaynameView.setVisibility(View.VISIBLE);
            } else {
                holder.displaynameView.setVisibility(View.GONE);
            }
            Glide.with(getContext()).load(BaseUtils.getUrlforPicture(item.getPhoto())).placeholder(R.drawable.profile_place_holder).into(holder.avatarView);
        }
        return convertView;
    }

    private static class ViewHolder {
        private final CircleImageView avatarView;
        private final TextView usernameView;
        private final TextView displaynameView;

        ViewHolder(View itemView) {
            avatarView = itemView.findViewById(R.id.img_profile);
            usernameView = itemView.findViewById(R.id.txt_username);
            displaynameView = itemView.findViewById(R.id.txt_name);
        }
    }


//    Context mContext;
//    int layoutResourceId;
//    List<UserModel> data = null;
//
//    public TagAdapter(Context mContext, int layoutResourceId, List<UserModel> data) {
//        super(mContext, layoutResourceId, data);
//        this.layoutResourceId = layoutResourceId;
//        this.mContext = mContext;
//        this.data = data;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        try{
//
//            if(convertView==null){
//                LayoutInflater inflater = ((HomeActivity) mContext).getLayoutInflater();
//                convertView = inflater.inflate(layoutResourceId, parent, false);
//            }
//
//            UserModel userModel = data.get(position);
//            CircleImageView imageView = convertView.findViewById(R.id.img_profile);
//            TextView txt_name = (TextView) convertView.findViewById(R.id.txt_name);
//            TextView txt_username = (TextView) convertView.findViewById(R.id.txt_username);
//
//            txt_name.setText(userModel.getName());
//            txt_username.setText(userModel.getUsername());
//            Glide.with(mContext).load(userModel.getPhoto()).into(imageView);
//
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return convertView;
//    }
}
