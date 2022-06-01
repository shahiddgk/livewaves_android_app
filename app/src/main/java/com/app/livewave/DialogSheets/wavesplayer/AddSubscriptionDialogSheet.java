package com.app.livewave.DialogSheets.wavesplayer;

import static com.app.livewave.utils.Constants.duration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.SubscriptionPlan;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.settings.MySubscriptionListFragment;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.FirebaseUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

import retrofit2.Response;

public class AddSubscriptionDialogSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    private TextInputEditText et_subscription_title, et_subscription_amount;
    private TextInputLayout til_subscription_title, til_subscription_duration, til_subscription_amount;
    private AutoCompleteTextView subscription_duration;
    private MaterialButton btn_create, btn_cancel;
    KProgressHUD dialog;
    private FirebaseUtils utils;
    SubscriptionPlan subscriptionPlan;
    boolean isEdit = false;
    MySubscriptionListFragment fragment;
    CheckBox flagPost, flagEvents, flagTracks, flag_in_person_tickets, flag_paid_live_streams;

    public AddSubscriptionDialogSheet(MySubscriptionListFragment fragment) {
        this.fragment = fragment;
    }

    public AddSubscriptionDialogSheet(MySubscriptionListFragment fragment, SubscriptionPlan subscriptionPlan, boolean isEdit) {
        this.fragment = fragment;
        this.subscriptionPlan = subscriptionPlan;
        this.isEdit = isEdit;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_subscription_dialog, container, false);
        utils = new FirebaseUtils(getActivity());
        initViews(view);
        initClickListeners();
        return view;
    }

    private void initClickListeners() {
        btn_create.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    private void initViews(View view) {
        et_subscription_title = view.findViewById(R.id.et_subscription_title);
        subscription_duration = view.findViewById(R.id.subscription_duration);
        subscription_duration.setOnClickListener(view12 -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (getContext(), android.R.layout.select_dialog_item, duration);
            subscription_duration.setAdapter(adapter);
        });

        et_subscription_amount = view.findViewById(R.id.et_subscription_amount);

        flagPost = view.findViewById(R.id.flag_posts);
        flagEvents = view.findViewById(R.id.flag_events);
        flagTracks = view.findViewById(R.id.flag_tracks);
        flag_in_person_tickets = view.findViewById(R.id.flag_in_person_tickets);
        flag_paid_live_streams = view.findViewById(R.id.flag_paid_live_streams);

        btn_create = view.findViewById(R.id.btn_create);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        dialog = BaseUtils.progressDialog(getContext());

        if (subscriptionPlan != null) {
            et_subscription_title.setText(subscriptionPlan.getTitle());
            subscription_duration.setText(duration[subscriptionPlan.getDuration() - 1] + "");
            et_subscription_amount.setText(subscriptionPlan.getAmount() + "");
            flagPost.setChecked(subscriptionPlan.getPostAccess().equalsIgnoreCase("1"));
            flagEvents.setChecked(subscriptionPlan.getEventAccess().equalsIgnoreCase("1"));
            flagTracks.setChecked(subscriptionPlan.getTrackAccess().equalsIgnoreCase("1"));
            flag_in_person_tickets.setChecked(subscriptionPlan.getTicketAccess().equalsIgnoreCase("1"));
            flag_paid_live_streams.setChecked(subscriptionPlan.getStreamAccess().equalsIgnoreCase("1"));
            btn_create.setText(R.string.update);
        }

        til_subscription_title = view.findViewById(R.id.til_subscription_title);
        til_subscription_duration = view.findViewById(R.id.til_subscription_duration);
        til_subscription_duration.setOnClickListener(view1 -> {
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>
//                    (getContext(), android.R.layout.select_dialog_item, language);
//            subscription_duration.setAdapter(adapter);
        });
        til_subscription_amount = view.findViewById(R.id.til_subscription_amount);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private int getWindowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_create) {
            validationCheck();
        } else if (id == R.id.btn_cancel) {
            this.dismiss();
        }
    }

    private int selectedIndexCal(String duration[], String durationIndays) {
        int selectedOption = 0;
        for (int i = 0; i < duration.length; i++) {
            if (durationIndays.equals(duration[i])) {
                selectedOption = i + 1;
            }
        }
        return selectedOption;
    }

    private void validationCheck() {
        int selectedOption = 0;
        String title, durationIndays, amount;
        title = et_subscription_title.getText().toString().trim();

        durationIndays = subscription_duration.getText().toString().trim();

        selectedOption = selectedIndexCal(duration, durationIndays);

        amount = et_subscription_amount.getText().toString().trim();

        til_subscription_title.setError(null);
        til_subscription_duration.setError(null);
        til_subscription_amount.setError(null);

        if (title.isEmpty()) {
            til_subscription_title.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (durationIndays.isEmpty()) {
            til_subscription_duration.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (amount.isEmpty()) {
            til_subscription_amount.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        dialog.show();

        title = et_subscription_title.getText().toString();
        durationIndays = subscription_duration.getText().toString();
        amount = et_subscription_amount.getText().toString();

        if (isEdit) {
            subscriptionPlan.setTitle(title);
            subscriptionPlan.setDuration(selectedIndexCal(duration, durationIndays));
            subscriptionPlan.setAmount(Integer.parseInt(amount));
            subscriptionPlan.setPostAccess(flagPost.isChecked() ? "1" : "0");
            subscriptionPlan.setEventAccess(flagEvents.isChecked() ? "1" : "0");
            subscriptionPlan.setTrackAccess(flagTracks.isChecked() ? "1" : "0");
            subscriptionPlan.setTicketAccess(flag_in_person_tickets.isChecked() ? "1" : "0");
            subscriptionPlan.setStreamAccess(flag_paid_live_streams.isChecked() ? "1" : "0");
            editSubscription(subscriptionPlan);
        } else {
            subscriptionPlan = new SubscriptionPlan(title, selectedOption, Integer.parseInt(amount),
                    flagPost.isChecked() ? "1" : "0", flagEvents.isChecked() ? "1" : "0", flagTracks.isChecked() ? "1" : "0", flag_in_person_tickets.isChecked() ? "1" : "0",
                    flag_paid_live_streams.isChecked() ? "1" : "0");
            createNewPlan(subscriptionPlan);
        }
    }

    private void createNewPlan(SubscriptionPlan subscriptionPlan) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        ApiManager.apiCall(ApiClient.getInstance().getInterface().createNewSubscriptionPlan(subscriptionPlan), getContext(), new ApiResponseHandler<List<SubscriptionPlan>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionPlan>>> data) {
                Toast.makeText(getContext(), "Subscription Plan Added Successfully", Toast.LENGTH_SHORT).show();
                dismiss();
                fragment.getSubscriptionPlans();
            }
        });
    }

    private void editSubscription(SubscriptionPlan subscriptionPlan) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        ApiManager.apiCall(ApiClient.getInstance().getInterface().editSubscription(subscriptionPlan), getContext(), new ApiResponseHandler<List<SubscriptionPlan>>() {
            @Override
            public void onSuccess(Response<ApiResponse<List<SubscriptionPlan>>> data) {
                Toast.makeText(getContext(), "Subscription Plan Updated Successfully", Toast.LENGTH_SHORT).show();
                dismiss();
                fragment.getSubscriptionPlans();
            }
        });
    }
}
