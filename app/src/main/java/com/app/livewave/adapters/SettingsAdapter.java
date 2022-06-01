package com.app.livewave.adapters;

import static com.app.livewave.utils.Constants.HIDE_HEADER;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.LanguageSelectionDialog;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.LoginActivity;
import com.app.livewave.activities.LoginActivityWithWavesFeature;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.SettingsModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    List<SettingsModel> settingList;
    Context context;
    UserModel userModel;

    public SettingsAdapter(Context context) {
        this.context = context;
        this.settingList = new ArrayList<>();
        Paper.init(context);
    }

    @NonNull
    @Override
    public SettingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingsAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.settings_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.MyViewHolder holder, int position) {
        userModel = Paper.book().read(Constants.currentUser);
        holder.title.setText(settingList.get(position).getTitle());
        holder.des.setText(settingList.get(position).getDes());
        Glide.with(context).load(settingList.get(position).getIcon()).into(holder.icon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Bundle bundle;
                switch (settingList.get(position).getId()) {
                    case 1:
//                        context.startActivity(new Intent(context, EditProfileFragment.class));

                         bundle = new Bundle();
                        bundle.putBoolean(HIDE_HEADER, false);
                        ((HomeActivity) context).loadFragment(R.string.tag_edit_profile, bundle);
                        break;

//                    case 2:
//                        LanguageSelectionDialog languageSelectionDialog = new LanguageSelectionDialog();
//                        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
//                        languageSelectionDialog.show(fm, "languageSelectionDialog");
//                        break;
//                    case 3:
//                        bundle = new Bundle();
//                        bundle.putInt("userId", userModel.getId());
//                        ((HomeActivity) context).loadFragment(R.string.tag_subscription, bundle);
//                        break;

                    case 4:
//                        context.startActivity(new Intent(context, WalletFragment.class));

                        ((HomeActivity) context).loadFragment(R.string.tag_wallet, null);
                        break;
                    case 5:
//                        context.startActivity(new Intent(context, AnalyticsFragment.class));

                        ((HomeActivity) context).loadFragment(R.string.tag_analytics, null);
                        break;
                    case 6:
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Livewaves");
                            String shareMessage = "Download the LiveWaves App and follow my page " + userModel.getUsername() + " See my Events, Streams, Photos, Videos and much more. \nPlay Store: https://play.google.com/store/apps/details?id=com.app.livewave\nApp Store: https://apps.apple.com/pk/app/livewaves/id1519452753";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch (Exception e) {

                        }
                        break;
                    case 7:
                        intent = new Intent(context, WebviewActivity.class);
                        intent.putExtra("intent_type", "1");
                        context.startActivity(intent);

//                        bundle = new Bundle();
//                        bundle.putString("intent_type", "1");
//                        ((HomeActivity) context).loadFragment(R.string.tag_webview, bundle);
                        break;
                    case 8:
                        intent = new Intent(context, WebviewActivity.class);
                        intent.putExtra("intent_type", "2");
                        context.startActivity(intent);

//                        bundle = new Bundle();
//                        bundle.putString("intent_type", "2");
//                        ((HomeActivity) context).loadFragment(R.string.tag_webview, bundle);
                        break;
                    case 9:
                        intent = new Intent(context, WebviewActivity.class);
                        intent.putExtra("intent_type", "3");
                        context.startActivity(intent);

//                        bundle = new Bundle();
//                        bundle.putString("intent_type", "3");
//                        ((HomeActivity) context).loadFragment(R.string.tag_webview, bundle);
                        break;
                    case 10:
//                        context.startActivity(new Intent(context, GettingStartedFragment.class));

                        ((HomeActivity)context).loadFragment(R.string.tag_gettting_started, null);
                        break;
                    default:
                        logoutUser();
                        break;
                }
            }
        });
    }

    private void logoutUser() {
        BaseUtils.showAlertDialog("Logout", "Do you want to logout?", context, new DialogBtnClickInterface() {
            @Override
            public void onClick(boolean positive) {
                if (positive) {
                    ((Activity) context).finish();
                    Paper.book().write(Constants.isLogin, false);
                    Paper.book().write("CurrentUserId", 0 );
                    context.startActivity(new Intent(context, LoginActivityWithWavesFeature.class));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }

    public void setList(List<SettingsModel> settingsModelList) {
        this.settingList = new ArrayList<>();
        this.settingList = settingsModelList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, des;
        ImageView icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            des = itemView.findViewById(R.id.des);
        }
    }
}