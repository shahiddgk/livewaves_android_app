package com.app.livewave.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.livewave.R;
import com.app.livewave.adapters.FullScreenImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class FullScreenActivity extends AppCompatActivity {

    ViewPager pager;
    FullScreenImageAdapter imageAdapter;
    List<String> slideModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        slideModelList = getIntent().getStringArrayListExtra("Images");
        initViews();
        initClickListeners();
    }

    private void initClickListeners() {
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        pager = findViewById(R.id.pager);
        imageAdapter = new FullScreenImageAdapter(this);
        pager.setAdapter(imageAdapter);
        if (slideModelList == null) {
            slideModelList = new ArrayList<>();
        }
        imageAdapter.setImages(slideModelList);
    }
}