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
import com.app.livewave.fragments.Events.EventsAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;


public class SearchEventFragment extends Fragment {

    RecyclerView rv_event;
    UserModel userModel;
    String query;
    private EventsAdapter adapter;
    private List<EventModel> eventModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_event, container, false);
        Log.d("!@#", "view loaded event");

        initViews(v);
        return v;
    }

    private void initViews(View view) {
        Paper.init(getContext());
        userModel = Paper.book().read(Constants.currentUser);

        rv_event = view.findViewById(R.id.rv_event);
        rv_event.setHasFixedSize(false);
        rv_event.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new EventsAdapter(getContext(), eventModelList);
        rv_event.setAdapter(adapter);
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
//        if (query != null)
//            filter(query);
    }

    public void filter(String text) {
//        if (text.equals("")) {
//            eventModelList = new ArrayList<>();
//            adapter.setList(eventModelList);
//        } else {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().searchEvent(text, "event"), getContext(), new ApiResponseHandlerWithFailure<List<EventModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<EventModel>>> data) {
                if (data.body().getData() != null)
                    eventModelList = data.body().getData();
                adapter.setList(eventModelList);
            }

            @Override
            public void onFailure(String failureCause) {
                Log.e("!@#", failureCause);

            }
        });
//        }
    }
}