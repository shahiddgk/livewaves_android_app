package com.app.livewave.fragments.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.livewave.R;
import com.app.livewave.models.SettingsModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SettingViewModel extends ViewModel {
    private MutableLiveData<List<SettingsModel>> settingList;

    public SettingViewModel() {
        settingList = new MutableLiveData<>();
        settingList.setValue(getSettings());
    }

    public LiveData<List<SettingsModel>> getSettingList() {
        return settingList;
    }
    public static List<SettingsModel> getSettings(){


        List<SettingsModel> settingsModelList = new ArrayList<>();
        settingsModelList.add(new SettingsModel(1,"Edit Profile","You can edit your profile", R.drawable.ic_pencil));
        settingsModelList.add(new SettingsModel(2,"Invite","Invite your love ones to Livewaves", R.drawable.ic_invite));
        settingsModelList.add(new SettingsModel(3,"Contact us","Feel free to contact us", R.drawable.ic_email));
        settingsModelList.add(new SettingsModel(4,"Help","We are willing to help you", R.drawable.ic_help));
        settingsModelList.add(new SettingsModel(5,"Terms and Conditions","Feel free to study our terms", R.drawable.ic_terms_and_conditions));
        settingsModelList.add(new SettingsModel(6,"Getting Started","Learn how to get started", R.drawable.ic_flag));
        settingsModelList.add(new SettingsModel(7,"Logout","Thanks for using our app", R.drawable.ic_logout));
        return settingsModelList;
    }
}
