package com.app.livewave.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.app.livewave.R;

import org.webrtc.EglBase;

import java.util.ArrayList;

public class ImageSliderAdapterForLoginSignUp extends PagerAdapter {

    int[] images;
    LayoutInflater layoutInflater;
    Context context;

    public ImageSliderAdapterForLoginSignUp(int[] images, Context context) {
        this.images = images;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View my_image_slide_layout = layoutInflater.inflate(R.layout.image_slider_view,container,false);
        ImageView imageView = my_image_slide_layout.findViewById(R.id.image_view_slider);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(context.getDrawable(images[position]));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(images[position]));
        }

        container.addView(my_image_slide_layout);

        return my_image_slide_layout;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
