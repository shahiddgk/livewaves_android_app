package com.app.livewave.fragments.Events;

import static android.app.Activity.RESULT_OK;
import static com.app.livewave.utils.BaseUtils.showAlertDialog;
import static com.app.livewave.utils.Constants.EDIT_EVENT;
import static com.app.livewave.utils.Constants.EVENT_ID;
import static com.app.livewave.utils.Constants.EVENT_OBJ;
import static com.app.livewave.utils.Constants.EVENT_SCREEN_REQUEST_CODE;
import static com.app.livewave.utils.Constants.HIDE_HEADER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.PublisherActivity;
import com.app.livewave.activities.SubscriberActivity;
import com.app.livewave.activities.WebviewActivity;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.paperdb.Paper;
import retrofit2.Response;

public class EventDetailFragment extends Fragment implements View.OnClickListener, PlayerStateListener {
    private ImageView eventImage, img_edit_event, img_share_event, img_delete_event;
    private TextView tv_title, txt_start_date, txt_end_date, txt_start_time, txt_end_time, tv_address, tv_event_price, tv_limit, tv_check_in, tv_ticket_number;
    private CardView ll_ticket_info;
    private MaterialButton btn_buy, btn_direction, btn_check_in, btn_go_live;
    private EventModel eventModel;
    private String eventId, sharingId;
    private UserModel userModel;
    private EventModel event;
//    Toolbar toolbar;
//    CollapsingToolbarLayout collapsingToolbarLayout;
//    AppBarLayout app_bar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        initComponents(view);
        initClickListeners(view);
        getBundleData();

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Event Detail");

        if (Constants.APPENV == ENV.review) {
            tv_event_price.setVisibility(View.GONE);
            btn_buy.setVisibility(View.GONE);
        }
        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_event_detail);
//        initComponents();
//        initClickListeners();
//        getIntentData();
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        collapsingToolbarLayout.setTitle("Event Detail");
//
//        if (Constants.APPENV == ENV.review){
//            tv_event_price.setVisibility(View.GONE);
//            btn_buy.setVisibility(View.GONE);
//        }
//    }

    private void initClickListeners(View view) {
        btn_direction.setOnClickListener(this);
        btn_check_in.setOnClickListener(this);
        btn_buy.setOnClickListener(this);
        btn_go_live.setOnClickListener(this);
        img_edit_event.setOnClickListener(this);
        img_delete_event.setOnClickListener(this);
        img_share_event.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().getInterface().getEvent(eventId).cancel();
    }

    private void getEventData() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getEvent(eventId), getActivity(), new ApiResponseHandler<EventModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<EventModel>> data) {
                event = data.body().getData();
                setData(event);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setData(EventModel event) {
        this.event = event;

        tv_title.setText(event.getTitle());
        try {
            Glide.with(getActivity()).load(BaseUtils.getUrlforPicture(event.getAttachment())).placeholder(R.drawable.cover_place_holder).into(eventImage);
        }
        catch (Exception e) {
            System.out.println(e);

        };

        txt_start_date.setText(BaseUtils.convertFromUTCTime(event.getStartsAt()));
        txt_end_date.setText(BaseUtils.convertFromUTCTime(event.getEndsAt()));
        txt_start_time.setText(BaseUtils.getTimeFromDate(event.getStartsAt()));
        txt_end_time.setText(BaseUtils.getTimeFromDate(event.getEndsAt()));
        if (event.getSharingID() != null) {
            sharingId = event.getSharingID();
        }
        if (userModel.getId().equals(event.getUserId())) {
            img_edit_event.setVisibility(View.VISIBLE);
            img_delete_event.setVisibility(View.VISIBLE);
        } else {
            img_edit_event.setVisibility(View.GONE);
            img_delete_event.setVisibility(View.GONE);
        }
//        Calendar calendar = Calendar.getInstance();
        //Returns current time in millis
//        long timeMilli = calendar.getTimeInMillis() / 1000;
        if (event.getType().equals("2")) {
            btn_direction.setVisibility(View.GONE);
            btn_go_live.setVisibility(View.VISIBLE);
            if (!event.getUserId().equals(userModel.getId())) {
                btn_go_live.setText(getString(R.string.join));
            }
        } else {
            btn_direction.setVisibility(View.VISIBLE);
            btn_go_live.setVisibility(View.GONE);

        }
        if (event.getPaid().equalsIgnoreCase("1")) {
            if (event.getIsPaid() == 1 || userModel.getId().equals(event.getUserId())) {
                btn_buy.setVisibility(View.GONE);
            } else {
                btn_buy.setVisibility(View.VISIBLE);
            }
            tv_event_price.setText(String.valueOf(event.getAmount()));
        } else {
            tv_event_price.setText(getString(R.string.free));
            btn_buy.setVisibility(View.GONE);
        }
        if (event.getAddress() != null || event.getLatitude() != null && event.getLongitude() != null)
            tv_address.setText(event.getAddress());
        if (event.getLimited().equals("1")) {
            tv_limit.setText(getString(R.string.forward_slash_between, event.getTicketsSold().toString(), event.getTickets()));
        } else {
            tv_limit.setText(R.string.unlimited_seats);
        }

        if (event.getIsPaid() == 1) {
            ll_ticket_info.setVisibility(View.VISIBLE);
        } else {
            ll_ticket_info.setVisibility(View.GONE);
        }

        if (!event.getPaid().equalsIgnoreCase("1")) {
            if (event.getCheckedIn().toString().equalsIgnoreCase("1")) {
//            tv_check_in.setVisibility(View.VISIBLE);
                btn_check_in.setVisibility(View.GONE);
                tv_ticket_number.setText(event.getTicketID().toString());
                ll_ticket_info.setVisibility(View.VISIBLE);
                if (event.getType().equals("2")) {
                    btn_go_live.setVisibility(View.VISIBLE);
                }
            } else {
                if (event.getUserId().equals(userModel.getId())) {
                    btn_check_in.setVisibility(View.GONE);
                } else {
//                tv_check_in.setVisibility(View.GONE);
                    btn_check_in.setVisibility(View.VISIBLE);
                    if (event.getType().equals("2")) {
                        btn_go_live.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void getBundleData() {
//        Intent intent = getIntent();
        Bundle bundle = getArguments();
        if (bundle.containsKey(EVENT_OBJ)) {
            eventModel = new Gson().fromJson(bundle.getString(EVENT_OBJ), EventModel.class);
            setData(eventModel);
        }
        if (bundle.containsKey(EVENT_ID)) {
            eventId = bundle.getString(EVENT_ID);
        }
        if (eventModel == null) {
            getEventData();
        }

    }

    private void initComponents(View view) {
        eventImage = view.findViewById(R.id.img_event);
        img_edit_event = view.findViewById(R.id.img_edit_event);
        img_share_event = view.findViewById(R.id.img_share_event);
        img_delete_event = view.findViewById(R.id.img_delete_event);
        tv_title = view.findViewById(R.id.tv_event_title);
        txt_start_date = view.findViewById(R.id.txt_start_date);
        txt_end_date = view.findViewById(R.id.txt_end_date);
        txt_start_time = view.findViewById(R.id.txt_start_time);
        txt_end_time = view.findViewById(R.id.txt_end_time);
        tv_address = view.findViewById(R.id.tv_address);
        tv_event_price = view.findViewById(R.id.tv_event_price);
        tv_limit = view.findViewById(R.id.tv_limit);
//        tv_check_in = findViewById(R.id.tv_check_in);
        tv_ticket_number = view.findViewById(R.id.tv_ticket_number);
        ll_ticket_info = view.findViewById(R.id.ll_ticket_info);
        btn_direction = view.findViewById(R.id.btn_direction);
        btn_check_in = view.findViewById(R.id.btn_check_in);
        btn_go_live = view.findViewById(R.id.btn_go_live);
        btn_buy = view.findViewById(R.id.btn_buy);

        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
//        app_bar = view.findViewById(R.id.app_bar);
//        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        collapsingToolbarLayout =
//                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            getActivity().onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        btn_direction.setOnClickListener(this);
        btn_check_in.setOnClickListener(this);
        btn_buy.setOnClickListener(this);
        btn_go_live.setOnClickListener(this);
        img_edit_event.setOnClickListener(this);
        img_delete_event.setOnClickListener(this);
        img_share_event.setOnClickListener(this);

        if (id == R.id.btn_direction) {
            openMap();
        } else if (id == R.id.btn_check_in) {
            if (event.getPaid().equalsIgnoreCase("1")
                    && event.getIsPaid().toString().equals("0")) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("id", "event_id=" + event.getId());
                intent.putExtra("type", "event");
                intent.putExtra("intent_type", "6");
                startActivity(intent);

            } else {
                if (event.getType().equals("2")) {
                    addCheckIn("stream");
                } else {
                    addCheckIn(getString(R.string.normal));
                }
            }
        } else if (id == R.id.btn_buy) {
            Intent intent = new Intent(getActivity(), WebviewActivity.class);
            intent.putExtra("id", "event_id=" + event.getId());
            intent.putExtra("type", "event");
            intent.putExtra("intent_type", "6");
            startActivity(intent);

        } else if (id == R.id.btn_go_live) {
            if (event.getType().equals("2") && event.getUserId().equals(userModel.getId())) {
                String startDate = txt_start_date.getText().toString();
                String endDate = txt_end_date.getText().toString();
                String startTime = txt_start_time.getText().toString();
                String endTime = txt_end_time.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                SimpleDateFormat outputformat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                try {
                    Date Start_Date = sdf.parse(startDate + " " + startTime), End_Date = sdf.parse(endDate + " " + endTime), Current_Date = new Date();

                    String StartDate = outputformat.format(Start_Date), EndDate = outputformat.format(End_Date), CurrenetDate = outputformat.format(Current_Date);

                    Date sd = sdf.parse(StartDate), ed = sdf.parse(EndDate),cd = sdf.parse(CurrenetDate);

                    System.out.println("FORMAT");
                    System.out.println(sd);
                    System.out.println(ed);
                    System.out.println(cd);

                    if (ed.after(cd) || sd.equals(cd)) {
                        if (ActivityCompat.checkSelfPermission(getContext(),
                                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(getContext(),
                                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{android.Manifest.permission.CAMERA,
                                            android.Manifest.permission.RECORD_AUDIO},
                                    1);
                        }else {
                            createStream();
                        }
//                        Toast.makeText(this, "Stream is creating", Toast.LENGTH_SHORT).show();
                    } else if (!sd.before(cd)) {
                        BaseUtils.showLottieDialog(getActivity(), "You can not start a stream before start date!", R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {

                            }
                        });
//                        Toast.makeText(this, "You can not start a stream before start date!", Toast.LENGTH_SHORT).show();
                    } else if (ed.before(cd)) {
                        BaseUtils.showLottieDialog(getActivity(), "You can not start a stream after end date!", R.raw.invalid, new DialogBtnClickInterface() {
                            @Override
                            public void onClick(boolean positive) {
                            }
                        });
//                        Toast.makeText(this, "You can not start a stream after end date!", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

//
            } else {
                System.out.println("PLATFORM ID");
                System.out.println(event.getPlatformID());

                String platformid = event.getPlatformID();
                System.out.println(platformid);
                if (platformid.equals(null)|| platformid.equals("")){
                    System.out.println(event.getPlatformID());
                    BaseUtils.showLottieDialog(getActivity(), "Null Platform ID!", R.raw.invalid, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {
                        }
                    });
//                    Toast.makeText(this, "Null Platform ID!", Toast.LENGTH_SHORT).show();
                }else {
                    if (ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getContext(),
                                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.RECORD_AUDIO},
                                1);
                    }else {
                        joinStream();
                    }

                }
//                if (event.getPlatformID() != null) {
//                    if (ActivityCompat.checkSelfPermission(getContext(),
//                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                            ActivityCompat.checkSelfPermission(getContext(),
//                                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(
//                                new String[]{android.Manifest.permission.CAMERA,
//                                        android.Manifest.permission.RECORD_AUDIO},
//                                1);
//                    }else {
//                        joinStream();
//                    }
//                } else {
//                    System.out.println(event.getPlatformID());
//                    BaseUtils.showLottieDialog(getActivity(), "Null Platform ID!", R.raw.invalid, new DialogBtnClickInterface() {
//                        @Override
//                        public void onClick(boolean positive) {
//                        }
//                    });
////                    Toast.makeText(this, "Null Platform ID!", Toast.LENGTH_SHORT).show();
//                }
            }

        } else if (id == R.id.img_edit_event) {
//            Intent intent = new Intent(getActivity(), CreateEventFragment.class);
//            Pair pair = new Pair(eventImage, getString(R.string.event_image));
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pair);
//
//            intent.putExtra(EVENT_OBJ, event);
//            intent.putExtra(EDIT_EVENT, true);
//            startActivityForResult(intent, EVENT_SCREEN_REQUEST_CODE, options.toBundle());

            System.out.println("eventTYPE");
            System.out.println(new Gson().toJson(event));

            Bundle bundle = new Bundle();
            bundle.putString(EVENT_OBJ, new Gson().toJson(event));
            bundle.putBoolean(EDIT_EVENT, true);
            bundle.putBoolean(HIDE_HEADER, false);
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_event_create, bundle);

        } else if (id == R.id.img_delete_event) {

            showAlertDialog(getString(R.string.delete_event), getString(R.string.are_you_sure_cant_to_delete_event), getActivity(), new DialogBtnClickInterface() {
                @Override
                public void onClick(boolean positive) {
                    if (positive) {
                        deleteEvent();
                    }
                }
            });

        } else if (id == R.id.img_share_event) {

            shareEvent();
        }
    }

    private void joinStream() {
        System.out.println(event.getId());
        System.out.println(event.getTitle());
        System.out.println("EVENT Platform idDD");
        System.out.println(event.getPlatformID());

        Intent intent = new Intent(getContext(), SubscriberActivity.class);
        intent.putExtra("Subscriber","Subscriber");
        intent.putExtra("ID", event.getId().toString());
        intent.putExtra("TITLE", event.getTitle());
        intent.putExtra("PLATFORM_ID", event.getPlatformID());
        intent.putExtra("STREAM_ID_TYPE", "event_id");
        startActivity(intent);

//        Bundle bundle = new Bundle();
//        bundle.putInt("ID", event.getId());
//        bundle.putString("TITLE", event.getTitle());
//        bundle.putString("PLATFORM_ID", event.getPlatformID());
//        bundle.putString("STREAM_ID_TYPE", "event_id");
//        ((HomeActivity) getActivity()).loadFragment(R.string.tag_subscriber, bundle);

    }

    private void createStream() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().streamEvent(event.getId(), String.valueOf(System.currentTimeMillis() + userModel.getId())), getActivity(), new ApiResponseHandler<StreamModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<StreamModel>> data) {
                Intent intent = new Intent(getContext(), PublisherActivity.class);
                intent.putExtra("ID", data.body().getData().getId());
                intent.putExtra("TITLE", data.body().getData().getTitle());
                intent.putExtra("PLATFORM_ID", data.body().getData().getPlatformID());
                intent.putExtra("Event", true);
                startActivity(intent);

//                Bundle bundle = new Bundle();
//                bundle.putInt("ID", data.body().getData().getId());
//                bundle.putString("TITLE", data.body().getData().getTitle());
//                bundle.putString("PLATFORM_ID", data.body().getData().getPlatformID());
//                bundle.putBoolean("Event", true);
//                ((HomeActivity) getActivity()).loadFragment(R.string.tag_publisher, bundle);
            }
        });
    }

    private void deleteEvent() {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().deleteEvent(eventId), getActivity(), new ApiResponseHandler<EventModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<EventModel>> data) {
                BaseUtils.showLottieDialog(getActivity(), getString(R.string.event_deleted_successfully), R.raw.delete, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                        getActivity().onBackPressed();
//                        finish();
                    }
                });
//                BaseUtils.showToast(EventDetailActivity.this, getString(R.string.event_deleted_successfully));

            }
        });
    }

    private void addCheckIn(String type) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().checkInEvent(eventId), getActivity(), new ApiResponseHandler<EventModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<EventModel>> data) {
                event.setCheckedIn(1);
                event.setTicketID(data.body().getData().getTicketID());
//                setData(event);
                getEventData();
            }
        });
    }

    private void shareEvent() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Event");
        String app_url = Constants.BASE_URL + "pl/event/" + sharingId;
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
        startActivity(Intent.createChooser(shareIntent, "Share Event"));
    }

    private void openMap() {
        if (event.getLatitude() != null && event.getLongitude() != null) {
            if (!event.getLatitude().isEmpty() && !event.getLongitude().isEmpty()) {

                double latitude = Double.parseDouble(event.getLatitude());
                double longitude = Double.parseDouble(event.getLongitude());
                String label = "Party Location";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        } else if (event.getAddress() != null) {
            if (isGoogleMapsInstalled()) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + event.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } else {
                BaseUtils.showLottieDialog(getActivity(), "Please download google maps!", R.raw.invalid, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                    }
                });
            }

        }
    }

    public boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onResume() {
        getEventData();
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EVENT_SCREEN_REQUEST_CODE) {
            EventModel updatedData = (EventModel) data.getSerializableExtra(EVENT_OBJ);
//            setData(updatedData);
            eventId = updatedData.getId().toString();
            getEventData();
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (isTaskRoot()) {
//            startActivity(new Intent(EventDetailActivity.this, HomeActivity.class));
//            finish();
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void updatePlayerState() {
    }

//    @Override
//    public void onDestroy() {
//        if (((HomeActivity)getActivity()).isTaskRoot(this)) {
//            Bundle bundle = new Bundle();
//            bundle.putBoolean(SHOW_HEADER,false);
//            ((HomeActivity)getActivity()).loadFragment(R.string.tag_dashboard,bundle);
//        } else {
//            getActivity().onBackPressed();
//        }
//        super.onDestroy();
//    }
}
