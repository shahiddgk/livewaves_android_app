package com.app.livewave.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.livewave.BottomDialogSheets.DaurationCategoryForSubscription;
import com.app.livewave.BottomDialogSheets.GiffImageGridListView;
import com.app.livewave.R;
import com.app.livewave.activities.RegisterActivity;
import com.app.livewave.fragments.Subscription.SubscriptionListAdapter;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ParameterModels.CreateEventRequestModel;
import com.app.livewave.models.RequestModels.CreateSubscriptionRequestModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.MyApplication;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.ParseException;
import java.util.Objects;
import java.util.concurrent.Flow;

import retrofit2.Response;

import static com.app.livewave.utils.Constants.EDIT_EVENT;
import static com.app.livewave.utils.Constants.EDIT_SUBSCRIPTION_PLAN;
import static com.app.livewave.utils.Constants.EVENT_OBJ;
import static com.app.livewave.utils.Constants.SUBSCRIPTION_OBJ;
import static com.app.livewave.utils.Constants.categoryList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateNewSubscription#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewSubscription extends Fragment implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener, PlayerStateListener {

    private TextInputEditText et_title, et_category_select, et_amount;
    private TextInputLayout tli_title, tli_amount, tli_category;
    MaterialCheckBox cb_Allow_post,cb_Allow_event_stream_tickets,cb_Allow_Tracks,cb_Allow_in_person_tickets,cb_Allow_paid_live_streams;
    private MaterialButton btn_create_subscription;
    ImageView img_back;
    boolean edit_subscription = false;
    int subscription_id = 0;
    String duration_id;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    KProgressHUD dialog;

//    public CreateNewSubscription() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateNewSubscription.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateNewSubscription newInstance(String param1, String param2) {
        CreateNewSubscription fragment = new CreateNewSubscription();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_new_subscription, container, false);
        initComponents(view);
        initClickListeners(view);
        getBundleData();
        return view;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (bundle.containsKey(EDIT_SUBSCRIPTION_PLAN)) {
            if (bundle.getBoolean(EDIT_SUBSCRIPTION_PLAN, false)) {
                edit_subscription = true;
//                setData((EventModel) intent.getSerializableExtra(EVENT_OBJ));
                tli_category.setVisibility(View.GONE);
                tli_amount.setVisibility(View.GONE);
                setData(new Gson().fromJson(bundle.getString(SUBSCRIPTION_OBJ), SubscriptionModel.class));
            }
        }

    }

    private void setData(SubscriptionModel subscriptionModel) {
        System.out.println("TITLETITLE");
        System.out.println(subscriptionModel.getTitle());
        System.out.println(subscriptionModel.getDuration());
        System.out.println(subscriptionModel.getAmount());
        System.out.println(subscriptionModel.getPost_access());
        System.out.println(subscriptionModel.getEvent_access());
        System.out.println(subscriptionModel.getLivestream_access());
        System.out.println(subscriptionModel.getTicket_access());
        System.out.println(subscriptionModel.getTrack_access());

        btn_create_subscription.setText("Edit Subscription");

        subscription_id = subscriptionModel.getId();

        String duration = Integer.toString(subscriptionModel.getId());
        System.out.println(duration);

        if (subscriptionModel.getDuration() == 1) {
            System.out.println("SELECTED LIST INDEX");
            duration_id = "1";
            et_category_select.setText("1 Day");
            System.out.println(duration_id);
        } else if(subscriptionModel.getDuration() == 2) {
            System.out.println("SELECTED LIST INDEX");
            et_category_select.setText("1 Week (7 Days)");
            duration_id = "2";
            System.out.println(duration_id);
        } else if (subscriptionModel.getDuration() == 3) {
            System.out.println("SELECTED LIST INDEX");
            et_category_select.setText("1 Month (30 Days)");
            duration_id = "3";
            System.out.println(duration_id);
        } else if (subscriptionModel.getDuration() == 4 ) {
            System.out.println("SELECTED LIST INDEX");
            et_category_select.setText("3 Month (90 Days)");
            duration_id = "4";
            System.out.println(duration_id);
        } else if (subscriptionModel.getDuration() == 5) {
            System.out.println("SELECTED LIST INDEX");
            et_category_select.setText("6 Month (180 Days)");
            duration_id = "5";
            System.out.println(duration_id);
        } else if (subscriptionModel.getDuration() == 6) {
            System.out.println("SELECTED LIST INDEX");
            et_category_select.setText("1 year (365 Days)");
            duration_id = "6";
            System.out.println(duration_id);
        } else if (subscriptionModel.getDuration() == 7) {
            System.out.println("SELECTED LIST INDEX");
            duration_id = "7";
            et_category_select.setText("Lifetime");
            System.out.println(duration_id);
        }

        et_title.setText(subscriptionModel.getTitle());
        et_amount.setText(Double.toString(subscriptionModel.getAmount()));
        if (subscriptionModel.getPost_access().equals("1") || subscriptionModel.getPost_access() == "1") {
            cb_Allow_post.isChecked();
            cb_Allow_post.setActivated(true);
            cb_Allow_post.setChecked(true);
        }
        if (subscriptionModel.getLivestream_access().equals("1") || subscriptionModel.getLivestream_access() == "1") {
            cb_Allow_paid_live_streams.isChecked();
            cb_Allow_paid_live_streams.setActivated(true);
            cb_Allow_paid_live_streams.setChecked(true);
        }
        if (subscriptionModel.getTicket_access().equals("1") || subscriptionModel.getTicket_access() == "1") {
            cb_Allow_Tracks.isChecked();
            cb_Allow_Tracks.setActivated(true);
            cb_Allow_Tracks.setChecked(true);
        }
        if (subscriptionModel.getEvent_access().equals("1") || subscriptionModel.getEvent_access() == "1") {
            cb_Allow_event_stream_tickets.isChecked();
            cb_Allow_event_stream_tickets.setActivated(true);
            cb_Allow_event_stream_tickets.setChecked(true);
        }
        if (subscriptionModel.getTrack_access().equals("1") || subscriptionModel.getTrack_access() == "1") {
            cb_Allow_Tracks.isChecked();
            cb_Allow_Tracks.setActivated(true);
            cb_Allow_Tracks.setChecked(true);
        }
    }

    private void initClickListeners(View view) {
        btn_create_subscription.setOnClickListener(this);
      //  img_back.setOnClickListener(this);
        img_back.setOnClickListener(this);
        et_category_select.setOnClickListener(this);

    }

    private void initComponents(View view) {
        et_title = view.findViewById(R.id.et_subscription_title);
        et_category_select = view.findViewById(R.id.et_duration_select);
        et_amount = view.findViewById(R.id.et_subscription_amount_home);
        cb_Allow_post = view.findViewById(R.id.cb_Allow_post);
        cb_Allow_event_stream_tickets = view.findViewById(R.id.cb_Allow_event_stream_tickets);
        cb_Allow_Tracks = view.findViewById(R.id.cb_Allow_Tracks);
        cb_Allow_in_person_tickets = view.findViewById(R.id.cb_Allow_in_person_tickets);
        cb_Allow_paid_live_streams = view.findViewById(R.id.cb_Allow_paid_live_streams);
        //img_back = view.findViewById(R.id.img_back_subscription);
        btn_create_subscription = view.findViewById(R.id.btn_create_subscription);
        tli_title = view.findViewById(R.id.tli_title);
        tli_category = view.findViewById(R.id.tli_duration_select);
        tli_amount = view.findViewById(R.id.tli_subscription_amount);
        img_back = view.findViewById(R.id.img_back);
        dialog = BaseUtils.progressDialog(getActivity());

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.img_back) {
            System.out.println("Back Button Clicked");

            getActivity().onBackPressed();
        }

        if (v.getId() == R.id.btn_create_subscription) {
            System.out.println("Create Clicked");
            try {
                fieldsValidation();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (v.getId() == R.id.et_duration_select) {
            DaurationCategoryForSubscription daurationCategoryForSubscription = new DaurationCategoryForSubscription();
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            daurationCategoryForSubscription.show(fragmentManager, "Duration Text list");
            daurationCategoryForSubscription.addListener(pressedButton -> {
                //add option if any
                if (pressedButton.equals(null) || pressedButton.equals("")){
                    System.out.println("Nothing selected");
                } else {
                    et_category_select.setText(pressedButton);
                    System.out.println(pressedButton);
                    if (pressedButton.equals("1 Day") || pressedButton == "1 Day") {
                        System.out.println("SELECTED LIST INDEX");
                        duration_id = "1";

                        System.out.println(duration_id);
                    } else if(pressedButton.equals("1 Week (7 Days)") || pressedButton == "1 Week (7 Days)") {
                        System.out.println("SELECTED LIST INDEX");
                        duration_id = "2";
                        System.out.println(duration_id);
                    } else if (pressedButton.equals("1 Month (30 Days)") || pressedButton == "1 Month (30 Days)") {
                        System.out.println("SELECTED LIST INDEX");
                        duration_id = "3";
                        System.out.println(duration_id);
                    } else if (pressedButton.equals("3 Month (90 Days)") || pressedButton == "3 Month (90 Days)") {
                        System.out.println("SELECTED LIST INDEX");
                        duration_id = "4";
                        System.out.println(duration_id);
                    } else if (pressedButton.equals("6 Month (180 Days)") || pressedButton == "6 Month (180 Days)") {
                        System.out.println("SELECTED LIST INDEX");
                        duration_id = "5";
                        System.out.println(duration_id);
                    } else if (pressedButton.equals("1 year (365 Days)") || pressedButton == "1 year (365 Days)") {
                        System.out.println("SELECTED LIST INDEX");
                        duration_id = "6";
                        System.out.println(duration_id);
                    } else if (pressedButton.equals("Lifetime") || pressedButton == "Lifetime") {
                        System.out.println("SELECTED LIST INDEX");
                        duration_id = "7";
                        System.out.println(duration_id);
                    }

                }
                daurationCategoryForSubscription.dismiss();
            });
        }

    }

    private void fieldsValidation() throws ParseException {
        String title = Objects.requireNonNull(et_title.getText()).toString();
        String category = Objects.requireNonNull(et_category_select.getText()).toString();
        String amount = Objects.requireNonNull(et_amount.getText()).toString();
        int allowPost,allowEventStreamTickets,allowInPersonTickets,allowPaidLiveStream,allowTracks;

        if (title.isEmpty()) {
            tli_title.requestFocus();
            tli_title.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (category.isEmpty()) {
            tli_category.requestFocus();
            tli_category.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (amount.isEmpty()) {
            tli_amount.requestFocus();
            tli_amount.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (amount.equals("0") ) {
            tli_amount.requestFocus();
            tli_amount.setError(getString(R.string.field_cant_be_empty_or_zero));
            return;
        }
        if (!cb_Allow_post.isChecked()
                && !cb_Allow_event_stream_tickets.isChecked()
                && !cb_Allow_in_person_tickets.isChecked()
                && !cb_Allow_paid_live_streams.isChecked()
                && !cb_Allow_Tracks.isChecked()){
            BaseUtils.showLottieDialog(getActivity(),this.getResources().getString(R.string.accept_any_check_box), R.raw.invalid, new DialogBtnClickInterface() {
                @Override
                public void onClick(boolean positive) {

                }
            });
            return;
        }
        if (cb_Allow_post.isChecked()) {
            allowPost = 1;
        }else {
            allowPost = 0;
        }
        if (cb_Allow_Tracks.isChecked()) {
            allowTracks = 1;
        }else {
            allowTracks = 0;
        }
        if (cb_Allow_paid_live_streams.isChecked()) {
            allowPaidLiveStream = 1;
        }else {
            allowPaidLiveStream = 0;
        }
        if (cb_Allow_in_person_tickets.isChecked()) {
            allowInPersonTickets = 1;
        }else {
            allowInPersonTickets = 0;
        }
        if (cb_Allow_event_stream_tickets.isChecked()) {
            allowEventStreamTickets = 1;
        }else {
            allowEventStreamTickets = 0;
        }

        CreateSubscriptionRequestModel createSubscriptionRequestModel = new CreateSubscriptionRequestModel(title,allowPost,allowEventStreamTickets,allowInPersonTickets,allowPaidLiveStream,allowTracks);

        if (edit_subscription) {
            createSubscriptionRequestModel.setSubscription_id(subscription_id);
            EditSubScription(createSubscriptionRequestModel);
        } else {
            createSubscriptionRequestModel.setAmount(amount);
            createSubscriptionRequestModel.setDuration(duration_id);
            AddSubscription(createSubscriptionRequestModel);
        }

    }

    private void AddSubscription(CreateSubscriptionRequestModel createSubscriptionRequestModel) {

        System.out.println("COMPILER IS HERE HERE");
        System.out.println(createSubscriptionRequestModel.getTitle());
        System.out.println(createSubscriptionRequestModel.getDuration());
        System.out.println(createSubscriptionRequestModel.getAmount());
        System.out.println("post access");
        System.out.println(createSubscriptionRequestModel.getPost_access());
        System.out.println("Event access");
        System.out.println(createSubscriptionRequestModel.getEvent_access());
        System.out.println("Live Stream access");
        System.out.println(createSubscriptionRequestModel.getLivestream_access());
        System.out.println("Ticket access");
        System.out.println(createSubscriptionRequestModel.getTicket_access());
        System.out.println("Track access");
        System.out.println(createSubscriptionRequestModel.getTrack_access());

            dialog.show();
            ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().createSubscription(createSubscriptionRequestModel), getActivity(), new ApiResponseHandlerWithFailure<Object>() {
                @Override
                public void onSuccess(Response<ApiResponse<Object>> data) {
                    dialog.dismiss();
                  //  EventModel finalEventModel = data.body().getData();
                    BaseUtils.showLottieDialog(getActivity(), "Subscription Added Successfully", R.raw.check, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {
//                        finish();
                            getActivity().onBackPressed();
                        }
                    });
                }

                @Override
                public void onFailure(String failureCause) {
                    dialog.dismiss();
                }
            });

        }


    private void EditSubScription(CreateSubscriptionRequestModel createSubscriptionRequestModel) {
        System.out.println("COMPILER IS HERE HERE");
        System.out.println(createSubscriptionRequestModel.getTitle());
        System.out.println(createSubscriptionRequestModel.getDuration());
        System.out.println(createSubscriptionRequestModel.getAmount());
        System.out.println("post access");
        System.out.println(createSubscriptionRequestModel.getPost_access());
        System.out.println("Event access");
        System.out.println(createSubscriptionRequestModel.getEvent_access());
        System.out.println("Live Stream access");
        System.out.println(createSubscriptionRequestModel.getLivestream_access());
        System.out.println("Ticket access");
        System.out.println(createSubscriptionRequestModel.getTicket_access());
        System.out.println("Track access");
        System.out.println(createSubscriptionRequestModel.getTrack_access());

        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().editSubscriptionPlanNew(createSubscriptionRequestModel), getActivity(), new ApiResponseHandlerWithFailure<Object>() {
            @Override
            public void onSuccess(Response<ApiResponse<Object>> data) {
                dialog.dismiss();
                //  EventModel finalEventModel = data.body().getData();
                BaseUtils.showLottieDialog(getActivity(), "Subscription Updated Successfully", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
//                        finish();
                        getActivity().onBackPressed();
                    }
                });
            }

            @Override
            public void onFailure(String failureCause) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void updatePlayerState() {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }
}