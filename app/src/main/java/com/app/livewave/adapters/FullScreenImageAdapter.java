package com.app.livewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.app.livewave.R;
import com.app.livewave.utils.TouchImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FullScreenImageAdapter extends PagerAdapter {

    Context context;
    List<String> images = new ArrayList<>();
    LayoutInflater layoutInflater;


    public FullScreenImageAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    public void setImages(List<String> images) {
        this.images = images;
        notifyDataSetChanged();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.layout_fullscreen_image, container, false);

        TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.imgDisplay);
        Glide.with(context).load(images.get(position)).into(imageView);
//        imageView.setImageResource(images[position]);
        container.addView(itemView);

        //listening to image click
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new ImageViewer.Builder(context, images)
//                        .setStartPosition(position)
//                        .show();
////                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
//            }
//        });

        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
