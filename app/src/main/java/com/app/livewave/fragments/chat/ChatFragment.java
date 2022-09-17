package com.app.livewave.fragments.chat;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.livewave.BottomDialogSheets.SelectMediaForPostDialogFragment;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.activities.ImagePickerActivity;
import com.app.livewave.adapters.MessageAdapter;
import com.app.livewave.interfaces.PostOptionInterface;
import com.app.livewave.models.InboxModel;
import com.app.livewave.models.MembersInfo;
import com.app.livewave.models.MessageModel;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.app.livewave.wavesplayer.playback.PlayerStateListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.paperdb.Paper;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.app.livewave.utils.BaseUtils.checkExtentionValidation;
import static com.app.livewave.utils.BaseUtils.getFileExtention;
import static java.util.UUID.randomUUID;

public class ChatFragment extends Fragment implements View.OnClickListener, PlayerStateListener {

    InboxModel inboxModel;
    FollowModel followModel;
    UserModel friendModel;
    int type = 0;
    RecyclerView rv_messages;
    RelativeLayout rl_recording_view, rl_message_view;
    TextView txt_cancel, txt_send_recording;
    Chronometer chronometer;
    ImageView img_show_options, img_select_picture, img_record, img_location, img_send;
    TextInputEditText et_message;
    MessageAdapter adapter;
    FirebaseFirestore db;
    List<MessageModel> messagesList = new ArrayList<>();
    UserModel userModel = new UserModel();
    private int REQUEST_IMAGE = 1;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://livewaveschat");
    StorageReference storageReference;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    public static final int RequestPermissionCode = 101;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    KProgressHUD dialog;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private DocumentSnapshot lastVisible;
    private int limit = 6;
    FirebaseFirestore rootRef;
    CollectionReference messageRef;
    LinearLayoutManager layoutManager;
    CollectionReference inboxRef;
    int messageEndCheck = 0;
    int check = 0;
    public static boolean messageSent = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        setHasOptionsMenu(true);
        getBundleData();
        initViews(view);
        initClickListeners();
//        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (type == 0) {
            getMessagesFromFirebase();
        } else {
            checkIfChatExists();
        }

//        if (type == 0) {
//            if (!myJson.getTitle().equals("")) {
//                ((HomeActivity) getActivity()).setHeaderTitle(inboxModel.getTitle());
//            } else {
//                for (int i = 0; i < inboxModel.getMembersInfo().size(); i++) {
//                    if (inboxModel.getMembersInfo().get(i).getId() != userModel.getId()) {
//                        ((HomeActivity) getActivity()).setHeaderTitle(inboxModel.getMembersInfo().get(i).getName());
//                    }
//                }
//            }
//            getMessagesFromFirebase();
//        } else if (type == 1) {
//
//            ((HomeActivity) getActivity()).setHeaderTitle(followModel.getName());
//
//            checkIfChatExists();
//        } else if (type == 2) {
//
//            ((HomeActivity) getActivity()).setHeaderTitle(friendModel.getName());
//
//            checkIfChatExists();
//        }
        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//        getBundleData();
//        initViews();
//        initClickListeners();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        if (type == 0) {
//            if (!inboxModel.getTitle().equals("")) {
//                getSupportActionBar().setTitle(inboxModel.getTitle());
//            } else {
//                for (int i = 0; i < inboxModel.getMembersInfo().size(); i++) {
//                    if (inboxModel.getMembersInfo().get(i).getId() != userModel.getId()) {
//                        getSupportActionBar().setTitle(inboxModel.getMembersInfo().get(i).getName());
//                    }
//                }
//            }
//            getMessagesFromFirebase();
//        } else if (type == 1) {
//            getSupportActionBar().setTitle(followModel.getName());
//            checkIfChatExists();
//        } else if (type == 2) {
//            getSupportActionBar().setTitle(friendModel.getName());
//            checkIfChatExists();
//        }
//    }

    private void checkIfChatExists() {
        inboxRef = rootRef.collection(Constants.firebaseDatabaseRoot);
        Query query = inboxRef.whereArrayContains("members", userModel.getId()).orderBy("sentAt", Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    InboxModel inboxModelList = documentSnapshot.toObject(InboxModel.class);
                    if (type == 1) {
                        if (inboxModelList.getMembers().contains(followModel.getId())) {
                            inboxModel = inboxModelList;
                            getMessagesFromFirebase();
                        }
                    } else {
                        if (inboxModelList.getMembers().contains(friendModel.getId())) {
                            inboxModel = inboxModelList;
                            getMessagesFromFirebase();
                        }
                    }
                }
            }
        });
    }

    private void initClickListeners() {
        img_show_options.setOnClickListener(this);
        et_message.setOnClickListener(this);
        img_send.setOnClickListener(this);
        img_select_picture.setOnClickListener(this);
        img_record.setOnClickListener(this);
        txt_cancel.setOnClickListener(this);
        txt_send_recording.setOnClickListener(this);
    }

    private void getMessagesFromFirebase() {
        adapter = new MessageAdapter(getActivity(), inboxModel.getMembersInfo(), inboxModel.id);
        rv_messages.setAdapter(adapter);
        messageRef = rootRef.collection(Constants.firebaseDatabaseRoot).document(inboxModel.id).collection("Messages");
        Query query = messageRef.orderBy("sentAt").limitToLast(10);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            MessageModel productModel = dc.getDocument().toObject(MessageModel.class);
                            messagesList.add(productModel);
                            if (productModel.getDeleteMessage() != null) {
                                for (int i = 0; i < productModel.getDeleteMessage().size(); i++) {
                                    if (!userModel.getId().equals(productModel.getDeleteMessage().get(i))) {
                                        check++;
                                    }
                                }
                                if (check == 2) {
                                    messagesList.clear();
                                    messageEndCheck++;
                                }
                                check = 0;
                            }
                            break;
                        case MODIFIED:
                            MessageModel productModel1 = dc.getDocument().toObject(MessageModel.class);
                            for (int i = 0; i < messagesList.size(); i++) {
                                if ((messagesList.get(i).id).equals(productModel1.id)) {
                                    messagesList.set(i, productModel1);
                                }
                            }
                            break;
                        case REMOVED:
                            break;
                    }
                }
                adapter.setList(messagesList);
                rv_messages.scrollToPosition(messagesList.size() - 1);
                if (value.getDocuments().size() > 0)
                    lastVisible = value.getDocuments().get(0);
                rv_messages.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && messageEndCheck > 0) {
                            if (!isLastItemReached) {
                                Query nextQuery = messageRef.orderBy("sentAt").endBefore(lastVisible).limitToLast(limit);
                                nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<MessageModel> messagesList1 = new ArrayList<>();
                                        for (QueryDocumentSnapshot d : queryDocumentSnapshots) {
                                            MessageModel productModel = d.toObject(MessageModel.class);
                                            if (productModel.getDeleteMessage() != null) {
                                                for (int k = 0; k < productModel.getDeleteMessage().size(); k++) {
                                                    if (!userModel.getId().equals(productModel.getDeleteMessage().get(k))) {
                                                        check++;
                                                    }
                                                }
                                                if (check == 2) {
                                                    messagesList1.clear();
                                                    messageEndCheck++;
                                                }
                                                check = 0;
                                            }
                                        }
                                        for(int i=0 ; i<messagesList1.size() ; i++){
                                            messagesList.add(i , messagesList1.get(i));
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (queryDocumentSnapshots.size() > 0)
                                            lastVisible = queryDocumentSnapshots.getDocuments().get(0);
                                        if (queryDocumentSnapshots.size() < limit) {
                                            isLastItemReached = true;
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
//        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    MessageModel productModel = documentSnapshot.toObject(MessageModel.class);
//                    messagesList.add(productModel);
//                }
//                adapter.setList(messagesList);
//                rv_messages.scrollToPosition(messagesList.size() - 1);
//                lastVisible = queryDocumentSnapshots.getDocuments().get(0);
//                rv_messages.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                    @Override
//                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
//                            if (!isLastItemReached) {
//                                Query nextQuery = messageRef.orderBy("sentAt").endBefore(lastVisible).limitToLast(limit);
//                                nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                        int i =0;
//                                        for (QueryDocumentSnapshot d : queryDocumentSnapshots) {
//                                            MessageModel productModel = d.toObject(MessageModel.class);
//                                            messagesList.add(i, productModel);
//                                            i++;
//                                        }
//                                        adapter.notifyDataSetChanged();
//                                        if (queryDocumentSnapshots.size() > 0)
//                                            lastVisible = queryDocumentSnapshots.getDocuments().get(0);
//                                        if (queryDocumentSnapshots.size() < limit) {
//                                            isLastItemReached = true;
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });
//            }
//        });
    }

    private void initViews(View view) {
        db = FirebaseFirestore.getInstance();
        Paper.init(getActivity());
        userModel = Paper.book().read(Constants.currentUser);
        dialog = BaseUtils.progressDialog(getActivity());
        rl_recording_view = view.findViewById(R.id.rl_recording_view);
        rl_message_view = view.findViewById(R.id.rl_message_view);
        txt_cancel = view.findViewById(R.id.txt_cancel);
        txt_send_recording = view.findViewById(R.id.txt_send_recording);
        chronometer = view.findViewById(R.id.chronometer);
        img_show_options = view.findViewById(R.id.img_show_options);
        img_select_picture = view.findViewById(R.id.img_select_picture);
        img_record = view.findViewById(R.id.img_record);
   //     img_location = view.findViewById(R.id.img_location);
        img_send = view.findViewById(R.id.img_send);
        et_message = view.findViewById(R.id.et_message);

        et_message.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

        rv_messages = view.findViewById(R.id.rv_messages);
        rv_messages.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        rv_messages.setLayoutManager(layoutManager);
        rootRef = FirebaseFirestore.getInstance();
    }

    private void getBundleData() {
        Gson gson = new Gson();
        Bundle bundle = getArguments();
        if (bundle.containsKey("inboxModel")) {
            inboxModel = gson.fromJson(bundle.getString("inboxModel"), InboxModel.class);
            type = 0;
        } else if (bundle.containsKey("followModel")) {
            followModel = gson.fromJson(bundle.getString("followModel"), FollowModel.class);
            type = 1;
        } else if (bundle.containsKey("friendModel")) {
            friendModel = gson.fromJson(bundle.getString("friendModel"), UserModel.class);
            type = 2;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.group_info) {
//            Intent intent = new Intent(getActivity(), GroupInfoFragment.class);
//            intent.putExtra("ChatID", inboxModel.getId());
//            startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString("ChatID", inboxModel.getId());
            ((HomeActivity) getActivity()).loadFragment(R.string.tag_group_info, bundle);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        if (inboxModel != null) {
            if (!inboxModel.title.equals("")) {
                getActivity().getMenuInflater().inflate(R.menu.group_info, menu);
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        return super.onCreateOptionsMenu(menu);
//        if (inboxModel != null) {
//            if (!inboxModel.title.equals("")) {
//                getActivity().getMenuInflater().inflate(R.menu.group_info, menu);
//                return true;
//            }
//        }
//        return true;
//    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_show_options:
                showOptions();
                break;
            case R.id.et_message:
                hideOptions();
                break;
            case R.id.img_send:
                validateMessage();
                break;
            case R.id.img_select_picture:
                selectImage();
                break;
            case R.id.img_record:
                startRecording();
                break;
            case R.id.txt_cancel:
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                rl_recording_view.setVisibility(View.GONE);
                if (mediaRecorder != null)
                    mediaRecorder.stop();
                break;
            case R.id.txt_send_recording:
                if (mediaRecorder != null)
                    mediaRecorder.stop();
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                rl_recording_view.setVisibility(View.GONE);
                dialog.show();
                uploadAudio();
                break;
        }
    }

    private void uploadAudio() {
        if (AudioSavePathInDevice != null) {
            Uri uriAudio = Uri.fromFile(new File(AudioSavePathInDevice).getAbsoluteFile());
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storage.getReference().child("uploads/audios/" + userModel.getId() + "/" + UUID.randomUUID().toString());
            ref.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            sendMessageToFirebase(uri.toString(), "", userModel.getId(), new Date().getTime(), 2);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startRecording() {
        rl_recording_view.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        random = new Random();
        if (checkPermission()) {
            ContextWrapper cw = new ContextWrapper(getActivity());
            File directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            AudioSavePathInDevice =
                    directory.getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + ".MP3";
            MediaRecorderReady();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new
                    String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
        }
    }

    private String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void selectImage() {
        SelectMediaForPostDialogFragment selectMediaForPostDialogFragment = new SelectMediaForPostDialogFragment(2);
        selectMediaForPostDialogFragment.addClickListener(new PostOptionInterface() {
            @Override
            public void pressed(String PressedButton) {
                if (getString(R.string.take_photo_from_camera).equals(PressedButton)) {
                    launchCameraIntent();
                }
                if (getString(R.string.select_picture_from_gallery).equals(PressedButton)) {
                    launchGalleryIntent();

                }
            }
        });
        FragmentManager fm = getChildFragmentManager();
        selectMediaForPostDialogFragment.show(fm, "Select_Media");
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, Constants.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri uri = data != null ? data.getParcelableExtra("path") : null;
                dialog.show();
                uploadImageToServer(uri);
            }
        }
    }

    private void uploadImageToServer(Uri uri) {
        File file = new File(BaseUtils.getPath(getActivity(), uri));
        String unique_name = randomUUID().toString();
        FirebaseApp.initializeApp(getActivity());

        String ext = getFileExtention(file.getName());
        int validMediaFormate = checkExtentionValidation(ext);
        if (validMediaFormate == 0) {
            String profilePath = "uploads/images/" + userModel.getId() + "/" + unique_name;
            uploadPhoto(file, profilePath);
        }
    }

    private void uploadPhoto(File file, String profilePath) {
        String ext = getFileExtention(file.getName());
        storageReference = storage.getReference().child(profilePath + "." + ext);
        Uri uriFile = Uri.fromFile(file);
        UploadTask uploadTask = storageReference.putFile(uriFile);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                Log.d("uploadedUrl", uri.toString());
                                sendMessageToFirebase(uri.toString(), "", userModel.getId(), new Date().getTime(), 1);
                            }
                        }
                    });
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateMessage() {
        if (TextUtils.isEmpty(et_message.getText().toString())) {
            et_message.setError("Field can't be empty!");
            et_message.requestFocus();
        } else {
            sendMessageToFirebase("", et_message.getText().toString(), userModel.getId(), new Date().getTime(), 0);
            et_message.setText("");
        }
    }

    private void sendMessageToFirebase(String attachment, String message, Integer senderId, long sentAt, int attachmentType) {
        Log.e("i am here", "sendMessageToFirebase: "  );
        if (inboxModel != null) {
            DocumentReference reference = db.collection(Constants.firebaseDatabaseRoot).document(inboxModel.id).collection("Messages").document();
            MessageModel messageModel = new MessageModel(attachment, message, senderId, sentAt, attachmentType, reference.getId());
            List<Integer> deleteMessage = new ArrayList<>(inboxModel.members);
            messageModel.setDeleteMessage(deleteMessage);
            reference.set(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    et_message.setText("");
                    if (attachmentType == 0) {
                        updateLastMessage(message, senderId, userModel.getName(), sentAt, inboxModel.getId(), reference.getId());
                        messageSent = true;
                    }
                    else if (attachmentType == 1)
                        updateLastMessage("Image", senderId, userModel.getName(), sentAt, inboxModel.getId(), reference.getId());
                    else
                        updateLastMessage("Sent a voice message", senderId, userModel.getName(), sentAt, inboxModel.getId(), reference.getId());
                }
            }).addOnFailureListener(e -> {

            });
        } else {
            DocumentReference inboxReference = db.collection(Constants.firebaseDatabaseRoot).document();

            InboxModel inboxModel = new InboxModel();
            inboxModel.setCreatedAt(sentAt);
            inboxModel.setCreatedBy(userModel.getId());
            inboxModel.setId(inboxReference.getId());
            List<Integer> members = new ArrayList<>();
            members.add(userModel.getId());
            if (type == 1)
                members.add(followModel.getId());
            else if (type == 2)
                members.add(friendModel.getId());
            inboxModel.setMembers(members);
            List<MembersInfo> membersInfo = new ArrayList<>();
            membersInfo.add(new MembersInfo(userModel.getName(), userModel.getId(), userModel.getPhoto(), userModel.getUsername(), true , "available"));
            if (type == 1)
                membersInfo.add(new MembersInfo(followModel.getName(), followModel.getId(), followModel.getPhoto(), followModel.getUsername(), true , "available"));
            else
                membersInfo.add(new MembersInfo(friendModel.getName(), friendModel.getId(), friendModel.getPhoto(), friendModel.getUsername(), true , "available"));
            inboxModel.setMembersInfo(membersInfo);
            inboxModel.setTitle("");
            this.inboxModel = inboxModel;
            inboxReference.set(inboxModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DocumentReference reference = db.collection(Constants.firebaseDatabaseRoot).document(inboxReference.getId()).collection("Messages").document();
                    MessageModel messageModel = new MessageModel(attachment, message, senderId, sentAt, attachmentType, reference.getId());
                    List<Integer> deleteMessage = new ArrayList<>(members);
                    messageModel.setDeleteMessage(deleteMessage);
                    reference.set(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            et_message.setText("");
                            getMessagesFromFirebase();
                            if (attachmentType == 0)
                                updateLastMessage(message, senderId, userModel.getName(), sentAt, inboxReference.getId(), reference.getId());
                            else if (attachmentType == 1)
                                updateLastMessage("Image", senderId, userModel.getName(), sentAt, inboxReference.getId(), reference.getId());
                            else
                                updateLastMessage("Sent a voice message", senderId, userModel.getName(), sentAt, inboxReference.getId(), reference.getId());
                        }
                    }).addOnFailureListener(e -> {

                    });
                }
            });
        }
    }

    private void updateLastMessage(String lastMessage, Integer senderId, String senderName, long sentAt, String id, String lastMessageId) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("lastMessage", lastMessage);
        hashMap.put("senderId", senderId);
        hashMap.put("senderName", senderName);
        hashMap.put("sentAt", sentAt);
        hashMap.put("lastMessageId", lastMessageId);
        List<MembersInfo> membersInfo = new ArrayList<>();
        membersInfo = inboxModel.membersInfo;
        Log.e("member info", "updateLastMessage: " + membersInfo.size() );
        for(int i=0 ; i<membersInfo.size() ; i++){
            membersInfo.get(i).setType("available");
        }
        hashMap.put("membersInfo", membersInfo);

        db.collection(Constants.firebaseDatabaseRoot).document(id).update(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.e("update last ", "onSuccess: " );
                dialog.dismiss();
            }
        });
    }

    private void hideOptions() {
        img_show_options.setVisibility(View.VISIBLE);
        img_select_picture.setVisibility(View.GONE);
        img_record.setVisibility(View.GONE);
      //  img_location.setVisibility(View.GONE);
    }

    private void showOptions() {
        img_show_options.setVisibility(View.GONE);
        img_select_picture.setVisibility(View.VISIBLE);
        img_record.setVisibility(View.VISIBLE);
      //  img_location.setVisibility(View.VISIBLE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean StoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;

                if (StoragePermission && RecordPermission) {
                    Toast.makeText(getActivity(), "Permission Granted",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void updatePlayerState() {

    }
}