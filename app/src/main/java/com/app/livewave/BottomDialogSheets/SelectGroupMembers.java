package com.app.livewave.BottomDialogSheets;

import static com.app.livewave.utils.Constants.HEADER_TITLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.SelectGroupMemberAdapter;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.models.InboxModel;
import com.app.livewave.models.MembersInfo;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Response;

public class SelectGroupMembers extends BottomSheetDialogFragment {

    SearchView searchView;
    RecyclerView rv_followings;
    SelectGroupMemberAdapter adapter;
    UserModel userModel;
    ProgressBar progress_bar;
    private int currentPageNumber = 1;
    private int totalPages = 0;
    private int itemsPerPage = 0;
    KProgressHUD dialog;
    List<FollowModel> followModelList = new ArrayList<>();
    List<FollowModel> filterList = new ArrayList<>();
    String query;
    Handler mHandler = new Handler();
    TextView txt_done;
    TextInputEditText et_group_name;
    FirebaseFirestore db;
    List<FollowModel> selectedMembersList = new ArrayList<>();
    boolean isSearchViewEmpty = true;

    public SelectGroupMembers() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_members_dialog, container, false);
        initViews(view);
        initClickListener();
        return view;
    }

    private void initClickListener() {
        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMembersList = new ArrayList<>();
                selectedMembersList = adapter.getSelected();
                if (TextUtils.isEmpty(et_group_name.getText().toString())) {
                    et_group_name.setError("Group Name is required!");
                    et_group_name.requestFocus();
                } else if (selectedMembersList.size() < 1) {
                    Toast.makeText(getContext(), "Select Members", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    ArrayList<MembersInfo> membersInfoArrayList = new ArrayList<>();
                    ArrayList<Integer> membersList = new ArrayList<>();
                    for (int i = 0; i < selectedMembersList.size(); i++) {
                        MembersInfo membersInfo = new MembersInfo(selectedMembersList.get(i).getName(),
                                selectedMembersList.get(i).getId(), selectedMembersList.get(i).getPhoto(),
                                selectedMembersList.get(i).getUsername(), true, "available");
                        membersInfoArrayList.add(membersInfo);
                        membersList.add(selectedMembersList.get(i).getId());
                    }
                    MembersInfo myInfo = new MembersInfo(userModel.getName(), userModel.getId(), userModel.getPhoto(),
                            userModel.getUsername(), true, "available");
                    membersInfoArrayList.add(myInfo);
                    membersList.add(userModel.getId());
                    DocumentReference reference = db.collection(Constants.firebaseDatabaseRoot).document();
                    InboxModel inboxModel = new InboxModel(membersInfoArrayList, membersList, userModel.getId(),
                            et_group_name.getText().toString(), new Date().getTime(), reference.getId(), "");
                    reference.set(inboxModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            dismiss();
                            Gson gson = new Gson();
                            String myJson = gson.toJson(inboxModel);
//                            Intent intent = new Intent(getContext(), ChatFragment.class);
//                            intent.putExtra("inboxModel", myJson);
//                            getContext().startActivity(intent);

                            Bundle bundle = new Bundle();
                            bundle.putString("inboxModel", myJson);
                            if (!inboxModel.getTitle().equals("")) {
                                bundle.putString(HEADER_TITLE, inboxModel.getTitle());
                            } else {
                                for (int i = 0; i < inboxModel.getMembersInfo().size(); i++) {
                                    if (inboxModel.getMembersInfo().get(i).getId() != userModel.getId()) {
                                        bundle.putString(HEADER_TITLE, inboxModel.getMembersInfo().get(i).getName());
                                    }
                                }
                            }
                            ((HomeActivity) getActivity()).loadFragment(R.string.tag_chat, bundle);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initViews(View view) {
        Paper.init(getContext());
        userModel = Paper.book().read(Constants.currentUser);
        db = FirebaseFirestore.getInstance();
        searchView = view.findViewById(R.id.searchView);
        et_group_name = view.findViewById(R.id.et_group_name);
        txt_done = view.findViewById(R.id.txt_done);
        progress_bar = view.findViewById(R.id.progress_bar);
        dialog = BaseUtils.progressDialog(getContext());

        rv_followings = view.findViewById(R.id.rv_followings);
        rv_followings.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_followings.setLayoutManager(layoutManager);
        adapter = new SelectGroupMemberAdapter(getContext());
        rv_followings.setAdapter(adapter);
        loadFollowings();

        rv_followings.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                    if (currentPageNumber != totalPages) {
                        if (isSearchViewEmpty) {
                            progress_bar.setVisibility(View.VISIBLE);
                            currentPageNumber++;
                            loadFollowings();
                        }
                    }
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                isSearchViewEmpty = query.equals("");
                searchFromList(query);
                if (followModelList.size() >= itemsPerPage) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchFromFollowing(query);
                        }
                    }, 500);
                }

                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }

    private void searchFromList(String newText) {
        filterList = new ArrayList<>();
        for (int i = 0; i < followModelList.size(); i++) {
            if (followModelList.get(i).getName().toLowerCase().contains(newText.toLowerCase())) {
                filterList.add(followModelList.get(i));
            }
        }
        adapter.setList(filterList);
    }

    private void searchFromFollowing(String text) {
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowings(text), getContext(), new ApiResponseHandler<GenericDataModel<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<FollowModel>>> data) {
                filterList.clear();
                filterList = data.body().getData().getData();
                adapter.setList(filterList);
            }
        });
    }

    private void loadFollowings() {
//        dialog.show();
        ApiManager.apiCall(ApiClient.getInstance().getInterface().getFollowings(userModel.getId().toString(), currentPageNumber), getContext(), new ApiResponseHandler<GenericDataModel<FollowModel>>() {
            @Override
            public void onSuccess(Response<ApiResponse<GenericDataModel<FollowModel>>> data) {
                if (data != null) {
                    currentPageNumber = data.body().getData().getCurrentPage();
                    totalPages = data.body().getData().getLastPage();
                    itemsPerPage = data.body().getData().getPerPage();
                    if (currentPageNumber == 1) {
                        followModelList = new ArrayList<>();
                        followModelList = data.body().getData().getData();
                    } else {
                        followModelList.addAll(data.body().getData().getData());
                    }
                    adapter.setList(followModelList);
//                    dialog.dismiss();
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
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
}