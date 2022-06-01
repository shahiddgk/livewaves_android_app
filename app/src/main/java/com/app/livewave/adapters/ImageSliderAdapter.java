package com.app.livewave.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.app.livewave.R;
import com.app.livewave.activities.FullScreenActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageSliderAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> images = new ArrayList<>();
    LayoutInflater layoutInflater;
    boolean isImage;

    public ImageSliderAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    public void setImages(ArrayList<String> images, boolean isImage) {
        this.images = images;
        this.isImage = isImage;
        notifyDataSetChanged();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.image_slider_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Glide.with(context).load(images.get(position)).into(imageView);
//        imageView.setImageResource(images[position]);
        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImage){
                    Intent intent = new Intent(context, FullScreenActivity.class);
                    intent.putStringArrayListExtra("Images", images);
                    context.startActivity(intent);
                }

//                new ImageViewer.Builder(context, images)
//                        .setStartPosition(position)
//                        .show();
//                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
