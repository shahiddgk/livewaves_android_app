package com.app.livewave.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.app.livewave.R;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.MyApplication;
import com.bumptech.glide.Glide;

import org.webrtc.EglBase;

import javax.annotation.Nullable;

public class GridViewAdapter extends BaseAdapter {

   private Context context;
    private LayoutInflater inflater;
    private String[] numberOfItems;
    private String[] imageUrl;

    public GridViewAdapter(Context c,String[] numberOfItems,String[] imageUrl) {
        context = c;
        this.numberOfItems = numberOfItems;
        this.imageUrl = imageUrl;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return numberOfItems.length;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_item,null);
        }

        ImageView imageView = convertView.findViewById(R.id.image_giff_view);

        Glide.with(MyApplication.getAppContext()).load(BaseUtils.getUrlforPicture(imageUrl[position])).into(imageView);
        return convertView;
    }
}
