package com.app.livewave.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.app.livewave.R;

import io.paperdb.Paper;

public class GiffImageGridView extends AppCompatActivity {
    GridView gridView;

    String[] numberOfItems = {"One","Two","Three","Four","Five"};
    String[] ImageUrl = {"https://media.giphy.com/media/3oriO13KTkzPwTykp2/giphy.gif",
            "https://media.giphy.com/media/MwOuiiTfWfWgM/giphy.gif",
            "https://media.giphy.com/media/l0HlVV6usFV4VzOPC/giphy.gif",
            "https://media.giphy.com/media/12P29BwtrvsbbW/giphy.gif",
            "https://media.giphy.com/media/fp3h1wpfp7kFa/giphy.gif"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giff_image_grid_view);

        gridView = findViewById(R.id.giff_image_grid_view);

        GridViewAdapter gridViewAdapter = new GridViewAdapter(GiffImageGridView.this,numberOfItems,ImageUrl);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    System.out.println("YOU CLICKED");
                System.out.println(ImageUrl[i]);
                Paper.book().write("ImageUrl",ImageUrl[i]);
                GiffImageGridView.super.onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}