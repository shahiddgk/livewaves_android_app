package com.app.livewave.adapters.wavesplayer;

import static com.app.livewave.utils.BaseUtils.showAlertDialog;
import static com.app.livewave.utils.Constants.duration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.DialogSheets.wavesplayer.AddSubscriptionDialogSheet;
import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.SubscriptionPlan;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.settings.MySubscriptionListFragment;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class SubscriptionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SubscriptionPlan> subscriptionPlans;
    KProgressHUD dialog;
    MySubscriptionListFragment fragment;


    public SubscriptionListAdapter(MySubscriptionListFragment fragment, ArrayList<SubscriptionPlan> subscriptionPlans, boolean isSummarized) {
        this.fragment = fragment;
        this.subscriptionPlans = subscriptionPlans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_subscription, parent, false);
        RecyclerView.ViewHolder holder = new Holder(itemView);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Holder classHolder = (Holder) holder;

        dialog = BaseUtils.progressDialog(fragment.getActivity());
        classHolder.title.setText(subscriptionPlans.get(position).getTitle());
        classHolder.date.setText(subscriptionPlans.get(position).getCreatedAt());
        classHolder.subscription_duration.setText(duration[subscriptionPlans.get(position).getDuration() - 1] + "");
        classHolder.price.setText("$" + subscriptionPlans.get(position).getAmount());

        classHolder.flagPost.setVisibility(subscriptionPlans.get(position).getTrackAccess().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        classHolder.flagTracks.setVisibility(subscriptionPlans.get(position).getEventAccess().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        classHolder.flagEvents.setVisibility(subscriptionPlans.get(position).getPostAccess().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        classHolder.flag_ticket_access.setVisibility(subscriptionPlans.get(position).getTicketAccess().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
        classHolder.flag_stream_access.setVisibility(subscriptionPlans.get(position).getStreamAccess().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);

        classHolder.edit.setOnClickListener(v -> {
            AddSubscriptionDialogSheet addSubscriptionDialogSheet = new AddSubscriptionDialogSheet(fragment, subscriptionPlans.get(position), true);
            FragmentManager fm = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            addSubscriptionDialogSheet.show(fm, "selectDurationDialogSheet");
        });

        classHolder.delete.setOnClickListener(v -> {
            showAlertDialog(v.getContext().getString(R.string.delete_subscription), v.getContext().getString(R.string.are_you_sure_cant_to_delete_subscription), v.getContext(), new DialogBtnClickInterface() {
                @Override
                public void onClick(boolean positive) {
                    if (positive) {
                        deleteSubscription(subscriptionPlans.get(position).getId());
                    }
                }
            });
        });

        classHolder.aSwitch.setChecked(subscriptionPlans.get(position).getStatus().equalsIgnoreCase("1") ? true : false);
        classHolder.aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subscriptionPlans.get(position).setStatus(isChecked ? "1" : "0");
            statusChangeApi(subscriptionPlans.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionPlans.size();
    }

    private void statusChangeApi(SubscriptionPlan subscriptionPlan) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().subscriptionStatusChange(subscriptionPlan), fragment.getActivity(), new ApiResponseHandler<List<SubscriptionPlan>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionPlan>>> data) {
                Toast.makeText(fragment.getActivity(), "Subscription Status Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void deleteSubscription(int id) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().deleteSubscription(id), fragment.getActivity(), new ApiResponseHandler<List<SubscriptionPlan>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionPlan>>> data) {
                Toast.makeText(fragment.getActivity(), data.body().getMessage(), Toast.LENGTH_SHORT).show();
                fragment.getSubscriptionPlans();
            }
        });
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView title, date, subscription_duration, price;
        ImageView edit, delete;
        Switch aSwitch;
        TextView flagPost, flagEvents, flagTracks, flag_ticket_access,flag_stream_access;

        public Holder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.subscription_title);
            date = itemView.findViewById(R.id.date);
            subscription_duration = itemView.findViewById(R.id.subscription_duration);
            price = itemView.findViewById(R.id.price);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            aSwitch = itemView.findViewById(R.id.switch1);

            flagPost = itemView.findViewById(R.id.flag_post);
            flagEvents = itemView.findViewById(R.id.flag_events);
            flagTracks = itemView.findViewById(R.id.flag_tracks);
            flag_ticket_access = itemView.findViewById(R.id.flag_ticket_access);
            flag_stream_access = itemView.findViewById(R.id.flag_stream_access);
        }
    }
}