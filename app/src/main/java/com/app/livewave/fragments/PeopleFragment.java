package com.app.livewave.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.adapters.FollowAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.Constants;

import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class PeopleFragment extends Fragment {

    RecyclerView rv_people;
    UserModel userModel;
    FollowAdapter adapter;
    List<FollowModel> searchList;
    String query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);
        Log.d("!@#", "view loaded psople");

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        Paper.init(getContext());
        userModel = Paper.book().read(Constants.currentUser);

        rv_people = view.findViewById(R.id.rv_people);
        rv_people.setHasFixedSize(false);
        rv_people.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new FollowAdapter(getContext(), userModel.getId());
        rv_people.setAdapter(adapter);
        filter("");
//        ((SearchActivity) getActivity()).searchData(new SearchQueryInterface() {
//            @Override
//            public void onQuery(String data) {
//                query = data;
//                filter(query);
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (query!=null)
//            filter(query);
    }

    public void filter(String text) {
//        if (text.equals("")){
//            searchList = new ArrayList<>();
//            adapter.setList(searchList);
//        }else {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().searchUser(text, "user"), getContext(), new ApiResponseHandler<List<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<FollowModel>>> data) {
                if (data.body().getData() != null)
                    searchList = data.body().getData();
                adapter.setList(searchList);
            }
        });
//        }
    }
}