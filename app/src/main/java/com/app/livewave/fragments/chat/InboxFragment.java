package com.app.livewave.fragments.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.FollowingDialogSheet;
import com.app.livewave.BottomDialogSheets.SelectGroupMembers;
import com.app.livewave.R;
import com.app.livewave.adapters.InboxAdapter;
import com.app.livewave.models.InboxModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;

public class InboxFragment extends Fragment implements PlayerStateListener {

    //    private Toolbar toolbar;
    RecyclerView rv_inbox;
    InboxAdapter adapter;
    FirebaseFirestore rootRef;
    //    CollectionReference inboxRef;
    UserModel userModel;
    List<InboxModel> inboxModelList = new ArrayList<>();
    List<InboxModel> inboxModelListNew;
    FollowingDialogSheet followingDialogSheet;
    SelectGroupMembers selectGroupMembers;
    TextView txt_new_group;
    static InboxFragment instance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootRef = FirebaseFirestore.getInstance();
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        setHasOptionsMenu(true);
        initViews(view);
        getInboxListFromFirebase();
        setUpRecyclerView();
        Log.e("oncreateview", "onCreateView: " );


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickListener();


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume", "onResume: " );

    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_inbox);
//        initViews();
//        initClickListener();
//        getInboxListFromFirebase();
//    }

    private void initClickListener() {
        txt_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGroupMembers = new SelectGroupMembers();
                FragmentManager fm = getChildFragmentManager();
                selectGroupMembers.show(fm, "following");
            }
        });
    }

    public static InboxFragment getInstance() {
        return instance;
    }

    private void getInboxListFromFirebase() {
        Query query = rootRef.collection(Constants.firebaseDatabaseRoot).whereArrayContains("members", userModel.getId()).orderBy("sentAt", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("ErrorFetchingData", error.toString());
                }
                 inboxModelList.clear();
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Log.e("TAG", "onEvent: " + dc.getType());
                        switch (dc.getType()) {
                            case ADDED:
                                InboxModel chatRoot = dc.getDocument().toObject(InboxModel.class);
                                inboxModelList.add(chatRoot);
                                Log.e("added", "onEvent: " + inboxModelList.size());
                                Log.e("added", "onEvent: " + inboxModelList.size());
                                int pos = -1;
                                for (int i = inboxModelList.size() - 1; i > -1; i--) {
                                    for (int j = 0; j < inboxModelList.get(i).membersInfo.size(); j++) {
                                        if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType() != null) {
                                            if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType().equals("delete")) {
                                                pos = i;
                                            }
                                        }
                                    }
                                    if (pos != -1) {
                                        inboxModelList.remove(pos);
                                    }
                                    pos = -1;
                                }
                                // adapter.setList(inboxModelList);

                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                int check = 0;
                                InboxModel modifiedMessage = dc.getDocument().toObject(InboxModel.class);
                                for (int i = 0; i < inboxModelList.size(); i++) {
                                    if (inboxModelList.get(i).getId().equals(modifiedMessage.id)) {
                                        inboxModelList.get(i).senderName = modifiedMessage.senderName;
                                        inboxModelList.get(i).senderId = modifiedMessage.senderId;
                                        inboxModelList.get(i).lastMessage = modifiedMessage.lastMessage;
                                        inboxModelList.get(i).sentAt = modifiedMessage.sentAt;
                                        inboxModelList.get(i).lastMessageId = modifiedMessage.lastMessageId;
                                        inboxModelList.get(i).membersInfo = modifiedMessage.membersInfo;
                                        check = 1;
                                    }
                                }

                                Log.e("modified", "onEvent: " + check + "  " + modifiedMessage.members);

                                if (check == 0) {
                                    inboxModelList.add(modifiedMessage);
                                    Log.e("size", "onEvent: " + inboxModelList.size() );
                                }
                                int pos1 = -1;
                                for (int i = inboxModelList.size() - 1; i > -1; i--) {
                                    for (int j = 0; j < inboxModelList.get(i).membersInfo.size(); j++) {
                                        if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType() != null) {
                                            if (userModel.getId() == inboxModelList.get(i).membersInfo.get(j).getId() && inboxModelList.get(i).membersInfo.get(j).getType().equals("delete")) {
                                                pos1 = i;
                                            }
                                        }
                                    }
                                    if (pos1 != -1) {
                                        inboxModelList.remove(pos1);
                                    }
                                    pos1 = -1;
                                }
//                                InboxModel inboxModel = inboxModelList.get(InboxAdapter.clickedChatPosition);
//                                inboxModelList.set(InboxAdapter.clickedChatPosition,inboxModelList.get(0));
//                                inboxModelList.set(0,inboxModel);
//                                adapter.notifyDataSetChanged();
                                getInboxListFromFirebase();
                                adapter.notifyDataSetChanged();
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                } else {
                    Log.e("else ", "onEvent: " + "nulll");
                }

            }
        });
    }

    private void initViews(View view) {

        Log.e("current user", "initViews: " + userModel.getName());
//        toolbar = view.findViewById(R.id.toolbar);
        rv_inbox = view.findViewById(R.id.rv_inbox);
        txt_new_group = view.findViewById(R.id.txt_new_group);

//        inboxRef = rootRef.collection(Constants.firebaseDatabaseRoot");

//        CollapsingToolbarLayout collapsingToolbarLayout =
//                view.findViewById(R.id.collapsing_toolbar);
//        collapsingToolbarLayout.setTitle(getString(R.string.chats));
//        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.buttercup));
//        this.setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    private void setUpRecyclerView() {
        rv_inbox.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_inbox.setLayoutManager(layoutManager);
        adapter = new InboxAdapter(getActivity(), userModel.getId());
        adapter.setList(inboxModelList);
        rv_inbox.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_add) {
            followingDialogSheet = new FollowingDialogSheet();
            FragmentManager fm = getChildFragmentManager();
            followingDialogSheet.show(fm, "following");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.inbox_menu, menu);
//        return true;
//    }

    @Override
    public void updatePlayerState() {

    }
}