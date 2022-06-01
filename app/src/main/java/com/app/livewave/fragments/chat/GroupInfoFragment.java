package com.app.livewave.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.AddGroupMember;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.adapters.MembersAdapter;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.interfaces.RemoveMemberInterface;
import com.app.livewave.models.InboxModel;
import com.app.livewave.models.MembersInfo;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class GroupInfoFragment extends Fragment implements PlayerStateListener {

    String rootId;
    RecyclerView rv_members;
    ImageView img_picture;
    MembersAdapter adapter;
    UserModel userModel;
    FirebaseFirestore rootRef;
    List<MembersInfo> membersInfoList = new ArrayList<>();
    InboxModel inboxModel;
    AddGroupMember addGroupMember;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);
        setHasOptionsMenu(true);
//        rootId = getIntent().getStringExtra("ChatID");

        rootId = getArguments().getString("ChatID");
        initViews(view);
        initClickListeners(view);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getInfoFromServer(rootId);

        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_group_info);
//        rootId = getIntent().getStringExtra("ChatID");
//        initViews();
//        initClickListeners();
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        getInfoFromServer(rootId);
//    }

    private void initClickListeners(View view) {
        view.findViewById(R.id.txt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroupMember = new AddGroupMember(inboxModel);
                FragmentManager fm = getChildFragmentManager();
                addGroupMember.show(fm, "following");
            }
        });
    }

    private void getInfoFromServer(String rootId) {
        rootRef.collection(Constants.firebaseDatabaseRoot).document(rootId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                inboxModel = documentSnapshot.toObject(InboxModel.class);
                if (inboxModel != null){
//                    Objects.requireNonNull(getSupportActionBar()).setTitle(inboxModel.getTitle());
                    ((HomeActivity) getActivity()).setHeaderTitle(inboxModel.getTitle());

//                    membersInfoList = new ArrayList<>();
//                    for (int i = 0; i < inboxModel.getMembersInfo().size(); i++) {
//                        if (inboxModel.getMembersInfo().get(i).getId() != userModel.getId()) {
//                            membersInfoList.add(inboxModel.getMembersInfo().get(i));
//                        }
//                    }
                    adapter.setList(inboxModel.getMembersInfo());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(View view) {
        rootRef = FirebaseFirestore.getInstance();
        userModel = Paper.book().read(Constants.currentUser);
        img_picture = view.findViewById(R.id.img_picture);
        rv_members = view.findViewById(R.id.rv_members);
        rv_members.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_members.setLayoutManager(layoutManager);
        adapter = new MembersAdapter(getActivity(), userModel.getId(), new RemoveMemberInterface() {
            @Override
            public void selected(int id) {
                if (inboxModel != null) {
                    BaseUtils.showAlertDialog("Remove", "Do you want to remove this user?", getActivity(), new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {
                            if (positive) {
                                membersInfoList = new ArrayList<>();
                                for (int i = 0; i < inboxModel.getMembersInfo().size(); i++) {
                                    if (inboxModel.getMembersInfo().get(i).getId() != id) {
                                        membersInfoList.add(inboxModel.getMembersInfo().get(i));
                                    }
                                }
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("membersInfo", membersInfoList);
                                rootRef.collection(Constants.firebaseDatabaseRoot).document(rootId).update(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        List<Integer> membersList = new ArrayList<>();
                                        for (int i = 0; i < inboxModel.getMembers().size(); i++) {
                                            if (!inboxModel.getMembers().get(i).equals(id)){
                                                membersList.add(inboxModel.getMembers().get(i));
                                            }
                                        }
                                        HashMap<String, Object> hashMap1 = new HashMap<>();
                                        hashMap1.put("members", membersList);
                                        rootRef.collection(Constants.firebaseDatabaseRoot).document(rootId).update(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                adapter.setList(membersInfoList);
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }

                        }
                    });

                }
            }
        });
        rv_members.setAdapter(adapter);
    }

    @Override
    public void updatePlayerState() {

    }
}