package com.app.livewave.interfaces;

import android.net.Uri;

public interface UploadingProgressInterface {

//    interface IuploadingProgress {
        void progressChange(int progress);
        void onFailureUpload(String message);
        void onSuccessfulUpload(Uri fileUri);

//    }

//    interface IonFailureUpload {
//    }
//
//    interface IOnSuccessUpload {
//        void onSuccessfulUpload(Uri fileUri);
//    }

}
