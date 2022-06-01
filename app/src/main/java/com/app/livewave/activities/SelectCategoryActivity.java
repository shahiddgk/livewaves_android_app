package com.app.livewave.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.app.livewave.R;
import com.app.livewave.adapters.CategoryListAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class SelectCategoryActivity extends AppCompatActivity {
    private RecyclerView rv_categories;
    private final List<CategoryModel> categoryData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();
        loadAllCategories();
    }


    private void loadAllCategories() {

        ApiManager.apiCall(ApiClient.getInstance().getInterface().getCategories(), this, new ApiResponseHandler<List<CategoryModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<CategoryModel>>> data) {
                setCategories(data.body().getData());
            }
        });
    }


    private void setCategories(List<CategoryModel> responseCategories) {
        if (responseCategories.size() > 0) {
            categoryData.addAll(responseCategories);
            rv_categories.setLayoutManager(new LinearLayoutManager(this));
            rv_categories.setAdapter(new CategoryListAdapter(responseCategories, this));
        }
    }

    private void initComponents() {
        rv_categories = findViewById(R.id.rv_categories);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}