package com.app.livewave.interfaces;

import android.widget.ArrayAdapter;

import com.app.livewave.models.ResponseModels.UserModel;

public interface TagInterface {
    void onCallback(ArrayAdapter<UserModel> mentionArrayAdapter);
}
