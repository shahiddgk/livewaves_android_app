package com.app.livewave.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.livewave.interfaces.UploadingProgressInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

import static com.app.livewave.utils.BaseUtils.checkExtentionValidation;
import static com.app.livewave.utils.BaseUtils.getFileExtention;
import static com.app.livewave.utils.BaseUtils.videoThumbnail;

public class FirebaseUtils {


    private StorageReference mStorageRef;


    private Context context;
    private UploadTask uploadTask;

    public FirebaseUtils(Context mcontext) {
        this.context = mcontext;
        FirebaseApp.initializeApp(mcontext);
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void uploadFileToFirebase(String ext, String path, Uri file, UploadingProgressInterface uploadingProgressInterface) {

//        String ext = getFileExtention(file.getName());
//        int validMediaFormate = checkExtentionValidation(ext);
//        if (validMediaFormate == -1)
//            return;

        StorageReference storageReference = getFireBaseStorageReferenece(ext, path);
//        Uri uriFile = Uri.fromFile(file);

        UploadTask uploadTask = storageReference.putFile(file);
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
//                                if (validMediaFormate == 2) {
//                                } else {
                                uploadingProgressInterface.onSuccessfulUpload(uri);
//                                }
                            }
                        }
                    });
                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                uploadingProgressInterface.progressChange(getProgressFromSnapshot(snapshot));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                uploadingProgressInterface.onFailureUpload(e.getMessage());
            }
        });
    }
    public void uploadVideoFileToFirebase(String ext, String path, File file, UploadingProgressInterface uploadingProgressInterface) {

//        String videoExt = getFileExtention(file.getName());
//        int validMediaFormate = checkExtentionValidation(videoExt);
//        if (validMediaFormate == -1)
//            return;

        StorageReference storageReference = getFireBaseStorageReferenece(ext, path);
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
//                                if (validMediaFormate == 2) {
//                                } else {
                                uploadingProgressInterface.onSuccessfulUpload(uri);
//                                }
                            }
                        }
                    });
                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                uploadingProgressInterface.progressChange(getProgressFromSnapshot(snapshot));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                uploadingProgressInterface.onFailureUpload(e.getMessage());
            }
        });
    }

    public void uploadVideoThumbnail(String path, File file, UploadingProgressInterface uploadingProgressInterface) {

         byte[] data = videoThumbnail(file.getPath());
        mStorageRef = getFireBaseStorageReferenece("JPEG", path);
        UploadTask uploadTask1 = mStorageRef.putBytes(data);
        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri videoThumbnailUri) {
                            uploadingProgressInterface.onSuccessfulUpload(videoThumbnailUri);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadingProgressInterface.onFailureUpload(e.getMessage());
                Log.e("failed to get thumb", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    private int getProgressFromSnapshot(UploadTask.TaskSnapshot snapshot) {
        return ((int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount()));
    }

    public StorageReference getFireBaseStorageReferenece(String ext, String path) {
        return FirebaseStorage.getInstance().getReference().child(path + "." + ext);

    }

    public static StorageReference getFireBaseStorageRefereneceForSingleImage(String ext, String path) {
        return FirebaseStorage.getInstance().getReference().child(path + "." + ext);

    }

    public void deleteFileFromStorage(String fileUrl) {
        if (!fileUrl.contains("firebasestorage")) {
            return;
        }

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Success!@3", "Post Delete Successfully");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error delete post", e.getMessage());
            }
        });
    }
}
