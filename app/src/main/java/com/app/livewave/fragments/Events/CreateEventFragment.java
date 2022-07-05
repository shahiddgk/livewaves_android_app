package com.app.livewave.fragments.Events;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.livewave.BottomDialogSheets.SelectCategoryDialog;
import com.app.livewave.BottomDialogSheets.SelectMediaForPostDialogFragment;
import com.app.livewave.R;
import com.app.livewave.activities.ImagePickerActivity;
import com.app.livewave.interfaces.ApiResponseHandlerWithFailure;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.OnCategorySelect;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.interfaces.UploadingProgressInterface;
import com.app.livewave.models.ParameterModels.CreateEventRequestModel;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.SubcategoryModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.utils.ENV;
import com.app.livewave.utils.FirebaseUtils;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.BaseUtils.changeViewVisibilityWithAnimationWithVisibilityGone;
import static com.app.livewave.utils.BaseUtils.getMimeType;
import static com.app.livewave.utils.Constants.AUTOCOMPLETE_REQUEST_CODE;
import static com.app.livewave.utils.Constants.EDIT_EVENT;
import static com.app.livewave.utils.Constants.EVENT_ID;
import static com.app.livewave.utils.Constants.EVENT_OBJ;
import static com.app.livewave.utils.Constants.REQUEST_EVENT_IMAGE;
import static com.app.livewave.utils.Constants.currentUser;

public class CreateEventFragment extends Fragment implements View.OnClickListener, PlayerStateListener {
    private TextInputEditText et_title, et_address, et_start_date, et_end_date, et_start_time, et_end_time, et_category_select, et_number_of_tickets, et_amount;
    private ImageView img_event, img_edit_event_cover;
    private TabLayout tl_free_paid, tl_limited_unlimited, tl_event_type;
    private MaterialButton btn_create_event;
    private TextInputLayout til_address, tli_title, tli_amount, tli_number_of_tickets, tli_start_date, tli_end_date, tli_start_time, tli_end_time, tli_category;
    private long startTime;
    private int startHours, startMin, startSec;
    private String categoryId, category, subCategoryId, subCategory;
    private boolean isImageSelected, oldPicture, editEvent;
    private String editImageUrl;
    private String eventId;
    private UserModel userModel;
    private String attachment = "";
    KProgressHUD dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        initComponents(view);
        initClickListeners(view);
        getBundleData();
        if (Constants.APPENV == ENV.review) {
            tl_free_paid.setVisibility(View.GONE);
        }
        return view;
    }

//    //Checkout
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_event);
//
//        initComponents();
//        initClickListeners();
//        getIntentData();
//        if (Constants.APPENV == ENV.review) {
//            tl_free_paid.setVisibility(View.GONE);
//        }
//    }

    private void getBundleData() {
//        Intent intent = getIntent();
        Bundle bundle = getArguments();

        if (bundle.containsKey(EDIT_EVENT)) {
            if (bundle.getBoolean(EDIT_EVENT, false)) {
                editEvent = true;
//                setData((EventModel) intent.getSerializableExtra(EVENT_OBJ));
                setData(new Gson().fromJson(bundle.getString(EVENT_OBJ), EventModel.class));
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void setData(EventModel eventModel) {

        et_title.setText(eventModel.getTitle());
        et_start_date.setText(BaseUtils.convertFromUTCTime(eventModel.getStartsAt()));
        et_end_date.setText(BaseUtils.convertFromUTCTime(eventModel.getEndsAt()));
        et_start_time.setText(BaseUtils.getTimeFromDate(eventModel.getStartsAt()));
        et_end_time.setText(BaseUtils.getTimeFromDate(eventModel.getEndsAt()));

        if (editEvent) {
            btn_create_event.setText(R.string.edit_event);
            eventId = eventModel.getId().toString();
        }
        et_address.setText(eventModel.getAddress());
        et_category_select.setText(eventModel.getCategoryTitle());

        if (eventModel.getType().equalsIgnoreCase("2")) {
            TabLayout.Tab tab = tl_event_type.getTabAt(1);
            assert tab != null;
            tab.select();
        }

        if (eventModel.getPaid().equals("1")) {
            TabLayout.Tab tab = tl_free_paid.getTabAt(1);
            assert tab != null;
            tab.select();
            et_amount.setText(String.valueOf(eventModel.getAmount()));
        }
        if (eventModel.getLimited().equals("1")) {
            TabLayout.Tab tab = tl_limited_unlimited.getTabAt(1);
            assert tab != null;
            tab.select();
            et_number_of_tickets.setText(eventModel.getTickets());
        }
        if (eventModel.getAttachment() != null) {
            Glide.with(this).load(BaseUtils.getUrlforPicture(eventModel.getAttachment())).placeholder(R.drawable.cover_place_holder).into(img_event);
            isImageSelected = true;
            oldPicture = true;
            editImageUrl = BaseUtils.getUrlforPicture(eventModel.getAttachment());
            attachment = BaseUtils.getUrlforPicture(eventModel.getAttachment());
        }


    }

    private void initClickListeners(View view) {
        btn_create_event.setOnClickListener(this);
        et_start_date.setOnClickListener(this);
        et_end_date.setOnClickListener(this);
        et_category_select.setOnClickListener(this);
        et_address.setOnClickListener(this);
        et_start_time.setOnClickListener(this);
        img_edit_event_cover.setOnClickListener(this);
        et_end_time.setOnClickListener(this);

        tl_event_type.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tl_free_paid.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    changeViewVisibilityWithAnimationWithVisibilityGone(tli_amount, 0);
                } else {
                    changeViewVisibilityWithAnimationWithVisibilityGone(tli_amount, 1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tl_limited_unlimited.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    changeViewVisibilityWithAnimationWithVisibilityGone(tli_number_of_tickets, 0);
                } else {
                    changeViewVisibilityWithAnimationWithVisibilityGone(tli_number_of_tickets, 1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        view.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }


//    private void initDateFragment(int type) {
//
//        //type 0 for start 1 for end
////        if (type == 1 && Objects.requireNonNull(et_start_date.getText()).toString().isEmpty()) {
////            tli_end_date.requestFocus();
////            tli_end_date.setError("Enter Start First");
////            return;
////        }
//
//        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
//
//        // now define the properties of the
//        // materialDateBuilder that is title text as SELECT A DATE
//        materialDateBuilder.setTitleText(getString(R.string.start_date));
//
//        // now create the instance of the material date
//        // picker
//        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
//
//        // handle select date button which opens the
//        // material design date picker
//        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
//
//
//        // now handle the positive button click from the
//        // material design date picker
//        materialDatePicker.addOnPositiveButtonClickListener(
//                new MaterialPickerOnPositiveButtonClickListener<Long>() {
//                    @Override
//                    public void onPositiveButtonClick(Long selection) {
//
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(selection);
//
//                        int mYear = calendar.get(Calendar.YEAR);
//                        int mMonth = calendar.get(Calendar.MONTH) + 1;
//                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//
//                        if (type == 0) {
//                            startTime = selection;
//                            et_start_date.setText(mYear + "-" + mMonth + "-" + mDay);
//
//
//                        } else {
////                            if (startTime > selection) {
////                                tli_end_date.requestFocus();
////                                tli_end_date.setError("End Time can't be smaller then start time");
////                                materialDatePicker.dismiss();
////                                return;
////                            }
//                            et_end_date.setText(mYear + "-" + mMonth + "-" + mDay);
//
//                        }
//
//                    }
//
//
//                });
//    }
//
//    private void showTimePicker(Long startDate, int type) {
//
//
//        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
//                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
//                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
//                .setTitleText(getString(R.string.event_start_time)).build();
//
//        materialTimePicker.show(getSupportFragmentManager(), getString(R.string.event_start_time));
//
//        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View v) {
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(startDate);
//
//                int mYear = calendar.get(Calendar.YEAR);
//                int mMonth = calendar.get(Calendar.MONTH) + 1;
//                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//                int mHours = materialTimePicker.getHour();
//                int mMin = materialTimePicker.getMinute();
//
////                if (type == 0) {
////                    startHours = mHours;
////                    startMin = mMin;
////                } else {
////                    if (startHours > mHours || startHours == mHours && startMin > mMin) {
////                        tli_end_date.requestFocus();
////                        tli_end_date.setError("End Time can't be smaller then start time");
////                        et_end_date.setText("");
////                        materialTimePicker.dismiss();
////                        return;
////                    }
////                }
////                et_start_date.setError(null);
////                tli_end_date.setError(null);
//
//                if (type == 0) {
//                    et_start_date.setText(mYear + "-" + mMonth + "-" + mDay);
//                    et_start_time.setText(" " + mHours + ":" + mMin);
//
//                } else {
//                    et_end_date.setText(mYear + "-" + mMonth + "-" + mDay);
//                    et_end_time.setText(" " + mHours + ":" + mMin);
//
//                }
//
//            }
//        });
//    }


    private void initComponents(View view) {
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), apiKey);
        }

        Paper.init(getActivity());
        userModel = Paper.book().read(currentUser);
        dialog = BaseUtils.progressDialog(getActivity());
        et_title = view.findViewById(R.id.et_title);
        et_start_date = view.findViewById(R.id.et_start_date);
        et_start_time = view.findViewById(R.id.et_start_time);
        et_end_date = view.findViewById(R.id.et_end_date);
        et_end_time = view.findViewById(R.id.et_end_time);
        et_category_select = view.findViewById(R.id.et_category_select);
        et_number_of_tickets = view.findViewById(R.id.et_number_of_tickets);
        et_amount = view.findViewById(R.id.et_amount);
        et_address = view.findViewById(R.id.et_address);

        btn_create_event = view.findViewById(R.id.btn_create_event);

        til_address = view.findViewById(R.id.til_address);
        tli_title = view.findViewById(R.id.tli_title);
        tli_start_date = view.findViewById(R.id.tli_start_date);
        tli_start_time = view.findViewById(R.id.tli_start_time);
        tli_end_date = view.findViewById(R.id.tli_end_date);
        tli_end_time = view.findViewById(R.id.tli_end_time);
        tli_category = view.findViewById(R.id.tli_category);

        tli_amount = view.findViewById(R.id.tli_amount);
        tli_amount.setVisibility(View.GONE);
        tli_number_of_tickets = view.findViewById(R.id.tli_number_of_tickets);
        tli_number_of_tickets.setVisibility(View.GONE);


        img_event = view.findViewById(R.id.img_event);
        img_edit_event_cover = view.findViewById(R.id.img_edit_event_cover);

        tl_event_type = view.findViewById(R.id.tl_event_type);
        tl_free_paid = view.findViewById(R.id.tl_free_paid);
        tl_limited_unlimited = view.findViewById(R.id.tl_limited_unlimited);

    }

    private void getAddress() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.et_start_date) {
            showDatePicker(et_start_date);
        } else if (v.getId() == R.id.et_end_date) {
            showDatePicker(et_end_date);
        } else if (v.getId() == R.id.et_start_time) {
            showTimePicker(et_start_time);
        } else if (v.getId() == R.id.et_end_time) {
            showTimePicker(et_end_time);
        } else if (v.getId() == R.id.et_category_select) {
            showCategoryDialog();
//            Intent intent = new Intent(this, SelectCategoryActivity.class);
//            startActivityForResult(intent, CATEGORY_SELECTION_REQUEST_CODE);
        } else if (v.getId() == R.id.btn_create_event) {
            try {
                fieldsValidation();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.et_address) {
            getAddress();
        } else if (v.getId() == R.id.img_edit_event_cover) {
            upLoadImage();
        }
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    private void showCategoryDialog() {
        SelectCategoryDialog selectCategoryDialog = new SelectCategoryDialog(new OnCategorySelect() {
            @Override
            public void onSelect(SubcategoryModel subcategoryModel) {
                category = subcategoryModel.getTitle();
                categoryId = subcategoryModel.getCategoryId().toString();
                subCategoryId = subcategoryModel.getId().toString();
                tli_category.setError(null);
                et_category_select.setText(category);
            }
        });
        FragmentManager fm = getChildFragmentManager();
        selectCategoryDialog.show(fm, "myDialog");
    }


    private void showTimePicker(TextInputEditText txt) {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), R.style.Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String time = hourOfDay + ":" + minute;

                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                Date date = null;
                try {
                    date = fmt.parse(time);
                } catch (ParseException e) {

                    e.printStackTrace();
                }

                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");

                String formattedTime = fmtOut.format(date);
                txt.setText(formattedTime);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
//        mTimePicker = new TimePickerDialog(CreateEventActivity.this, R.style.Dialog, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
////                SimpleDateFormat format12 = new SimpleDateFormat("hh:mm aa");
//                SimpleDateFormat format24 = new SimpleDateFormat("HH:mm");
//                Date dateTime = new Date();
//                dateTime.setHours(selectedHour);
//                dateTime.setMinutes(selectedMinute);
//
//                String time = selectedHour + ":" + selectedMinute;
//                try {
//                    Date date = format24.parse(time);
//                    String formattedTime = format24.format(date);
//                    txt.setText(formattedTime);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, hour, minute, true);//Yes 24 hour time
//        mTimePicker.setTitle("Select Time");
//        mTimePicker.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePicker(TextInputEditText text) {
//        Calendar cldr = Calendar.getInstance();
//        int day = cldr.get(Calendar.DAY_OF_MONTH);
//        int month = cldr.get(Calendar.MONTH);
//        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.Dialog);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String month_s = "";
                String day_s = "";
                if (month > 8) {
                    month_s = String.valueOf(month + 1);
                } else {
                    month_s = "0" + (month + 1);
                }
                if (dayOfMonth > 9) {
                    day_s = String.valueOf(dayOfMonth);
                } else {
                    day_s = "0" + dayOfMonth;
                }
                text.setText(month_s + "/" + day_s + "/" + year);
            }
        });
        datePickerDialog.show();
    }

    private void upLoadImage() {
        SelectMediaForPostDialogFragment selectMediaForPostDialogFragment = new SelectMediaForPostDialogFragment(2);
        selectMediaForPostDialogFragment.addClickListener(new PostOptionInterface() {
            @Override
            public void pressed(String PressedButton) {
                Intent intent = new Intent(getActivity(), ImagePickerActivity.class);

                if (getString(R.string.take_photo_from_camera).equals(PressedButton)) {
                    launchCameraIntent();
                }
                if (getString(R.string.select_picture_from_gallery).equals(PressedButton)) {
                    launchGalleryIntent();
                }
            }
        });
        FragmentManager fm = this.getChildFragmentManager();
        selectMediaForPostDialogFragment.show(fm, "Select Media");

    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_GALLERY_IMAGE);
        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
        startActivityForResult(intent, REQUEST_EVENT_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_EVENT_IMAGE);
    }


//    private void initTimeFragment(int type) {
////
////        if (type == 1) {
////            if (et_start_date.getText().toString().isEmpty()) {
////                tli_start_date.setError("Select start time first");
////                return;
////            }
////            if (Objects.requireNonNull(et_start_time.getText()).toString().isEmpty()) {
////                tli_end_time.setError("Select start time first");
////                return;
////            }
////        }
//        showTimePicker(startTime, type);
//
//    }

    private void fieldsValidation() throws ParseException {

        String title = Objects.requireNonNull(et_title.getText()).toString();
        String startDate = Objects.requireNonNull(et_start_date.getText()).toString();
        String endDate = Objects.requireNonNull(et_end_date.getText()).toString();
        String amount = Objects.requireNonNull(et_amount.getText()).toString();
        String tickets = Objects.requireNonNull(et_number_of_tickets.getText()).toString();
        String category = Objects.requireNonNull(et_category_select.getText()).toString();

        String lat = "", lng = "";
        String statedAt = et_start_date.getText().toString();
        String endAt = et_end_date.getText().toString();
        String type = tl_event_type.getSelectedTabPosition() == 0 ? "1" : "2";
        String paid = tl_free_paid.getSelectedTabPosition() == 0 ? "0" : "1";
        String limitedUnlimited = tl_limited_unlimited.getSelectedTabPosition() == 0 ? "0" : "1";
        String eventStartTime = et_start_time.getText().toString();
        String eventEndTime = et_end_time.getText().toString();


        if (title.isEmpty()) {
            tli_title.requestFocus();
            tli_title.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (startDate.isEmpty()) {
            tli_start_date.requestFocus();
            tli_start_date.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (endDate.isEmpty()) {
            tli_end_date.requestFocus();
            tli_end_date.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (eventStartTime.isEmpty()) {
            tli_start_time.requestFocus();
            tli_start_time.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (eventEndTime.isEmpty()) {
            tli_end_time.requestFocus();
            tli_end_time.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (tl_free_paid.getSelectedTabPosition() == 1 && amount.isEmpty() || amount.equals("0")) {
            tli_amount.requestFocus();
            tli_amount.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (tl_limited_unlimited.getSelectedTabPosition() == 1 && tickets.isEmpty()) {
            tli_number_of_tickets.requestFocus();
            tli_number_of_tickets.setError(getString(R.string.field_cant_be_empty));
            return;

        }
        if (category.isEmpty()) {
            tli_category.requestFocus();
            tli_category.setError(getString(R.string.field_cant_be_empty));
            return;
        }
        if (type.equals("1")) {
            if (TextUtils.isEmpty(et_address.getText().toString())) {
                et_address.setError("Address is required for Live Events!");
                et_address.requestFocus();
                return;
            }
        }
        SimpleDateFormat requiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat currentFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String sDate = statedAt + " " + eventStartTime;
        String eDate = endAt + " " + eventEndTime;
        Date sd = currentFormat.parse(sDate);
        Date ed = currentFormat.parse(eDate);
        sDate = requiredFormat.format(sd);
        eDate = requiredFormat.format(ed);
        CreateEventRequestModel createEventRequestModel = new CreateEventRequestModel(title, categoryId, type, lat, lng, limitedUnlimited, BaseUtils.convertToUTCTime(sDate), BaseUtils.convertToUTCTime(eDate), tickets, amount, subCategoryId, attachment, paid, et_address.getText().toString().trim());
        dialog.show();
        if (editEvent) {
            createEventRequestModel.setEvent_id(eventId);
            editEvent(createEventRequestModel);
        } else {
            addEvent(createEventRequestModel);
        }
    }

    private void editEvent(CreateEventRequestModel createEventRequestModel) {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().editEvent(createEventRequestModel), getActivity(), new ApiResponseHandlerWithFailure<EventModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<EventModel>> data) {
                dialog.dismiss();
                BaseUtils.showLottieDialog(getActivity(), "Event Edited Successfully", R.raw.check, new DialogBtnClickInterface() {
                    @Override
                    public void onClick(boolean positive) {
                        Intent intent = new Intent();

//                Pair[] pairs = new Pair[2];
//                pairs[0] = new Pair<View, String>(img_event, getResources().getString(R.string.event_image));
//                pairs[1] = new Pair<View, String>(et_title, getResources().getString(R.string.event_title));
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CreateEventActivity.this, pairs);
//
                        intent.putExtra(EVENT_ID, eventId);
                        intent.putExtra(EVENT_OBJ, data.body().getData());
                        getActivity().setResult(RESULT_OK, intent);
//                startActivity(intent, options.toBundle());
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

    private void addEvent(CreateEventRequestModel createEventRequestModel) {
        ApiManager.apiCallWithFailure(ApiClient.getInstance().getInterface().createEvent(createEventRequestModel), getActivity(), new ApiResponseHandlerWithFailure<EventModel>() {
            @Override
            public void onSuccess(Response<ApiResponse<EventModel>> data) {
                dialog.dismiss();
                EventModel finalEventModel = data.body().getData();
                BaseUtils.showLottieDialog(getActivity(), "Event Added Successfully", R.raw.check, new DialogBtnClickInterface() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            case (CATEGORY_SELECTION_REQUEST_CODE): {
//                if (resultCode == Activity.RESULT_OK) {
//                    category = data.getStringExtra(CATEGORY);
//                    categoryId = data.getStringExtra(CATEGORY_ID);
//                    tli_category.setError(null);
//                    et_category_select.setText(category);
//                }
//                break;
//            }
            case (AUTOCOMPLETE_REQUEST_CODE): {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    et_address.setText(place.getAddress());
                    et_address.setError(null);
                    break;
                }else {
                    Log.e("@#$", data+"");
                }
            }
            case (REQUEST_EVENT_IMAGE): {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getParcelableExtra("path");
                    Glide.with(this).load(uri).placeholder(R.drawable.profile_place_holder).into(img_event);
                    File file = new File(uri.getPath());
                    String unique_name = UUID.randomUUID().toString();
                    String path = Constants.FirestoreBaseDir + userModel.getId() + "/events/" + unique_name;
                    FirebaseApp.initializeApp(getActivity());
                    String ext = getMimeType(getActivity(), uri);
                    FirebaseUtils utils = new FirebaseUtils(getActivity());
                    utils.uploadVideoFileToFirebase(ext, path, file, new UploadingProgressInterface() {
                        @Override
                        public void progressChange(int progress) {

                        }

                        @Override
                        public void onFailureUpload(String message) {

                        }

                        @Override
                        public void onSuccessfulUpload(Uri fileUri) {
                            attachment = fileUri.toString();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void updatePlayerState() {

    }
}