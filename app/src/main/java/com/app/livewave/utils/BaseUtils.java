package com.app.livewave.utils;

import static com.app.livewave.utils.Constants.HIDE_HEADER;
import static com.app.livewave.utils.Constants.SPECIFIC_USER_ID;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.app.livewave.R;
import com.app.livewave.activities.HomeActivity;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.IdsAndTagsListModel;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.LocationModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.UserModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class BaseUtils {

//    public static void showToast(Context context, String message) {
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//    }

    @SuppressLint("SimpleDateFormat")
    public static long getDate(String date) {
        long time = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date d = format.parse(date);
            assert d != null;
            time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getUrlforPicture(String url) {
        if (url != null) {
            return url.contains("https:") || url.contains("http:") ? url : Constants.BASE_URL + url;
        } else {
            return "";
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void openUserProfile(String id, Context context) {

        Paper.init(context);
        UserModel userModel = Paper.book().read(Constants.currentUser);
        if (!id.equals(userModel.getId().toString()) && !id.equals(userModel.getUsername())) {
//            Toast.makeText(context, "Go to other person profile", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, UserProfileFragment.class);
//            intent.putExtra(SPECIFIC_USER_ID, id);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);

            Bundle bundle = new Bundle();
            bundle.putString(SPECIFIC_USER_ID, id);
            bundle.putBoolean(HIDE_HEADER, false);
            ((HomeActivity) context).loadFragment(R.string.tag_user_profile, bundle);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFromUTC(String date) {
        String dateStr = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date d = format.parse(date);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            dateStr = formatter.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertMilliSecToUTCWithCustomFormat(long milisec, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(milisec);
        return formatter.format(calendar.getTime());
    }

    public static void changeViewVisibilityWithAnimation(View view, int hideVisible) {
        //0 for hide 1 for visible

        TranslateAnimation animate;
        if (hideVisible == 0) {
            animate = new TranslateAnimation(0, view.getWidth() + 2000, 0, 0);
        } else {
            animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
        }
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void changeViewVisibilityWithAnimationWithVisibilityGone(View view, int hideVisible) {


        //0 for hide 1 for visible
        TranslateAnimation animate;
        if (hideVisible == 0) {
            animate = new TranslateAnimation(0, view.getWidth() + 2000, 0, 0);
        } else {
            animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
        }
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (hideVisible == 1) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (hideVisible == 0) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static String getDateFromMilliSec(String milliSeconds) {

//        yyyy-MM-dd HH:mm:ss
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(Long.parseLong(milliSeconds) * 1000);

        return formatter.format(calendar.getTime());
    }

    public static String getTimeFromMiliSecond(long miliSeconds) {
        String dateFormat = "hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(miliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }
//    public static String getTime(String milliSeconds) {
//
////        yyyy-MM-dd HH:mm:ss
//        // Create a DateFormatter object for displaying date in specified format.
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//        // Create a calendar object that will convert the date and time value in milliseconds to date.
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        calendar.setTimeInMillis(Long.parseLong(milliSeconds) * 1000);
//        return formatter.format(calendar.getTime());
//    }

    public static KProgressHUD progressDialog(Context context) {
        KProgressHUD dialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        return dialog;
    }

    public static KProgressHUD showProgressDialog(Context context) {
        KProgressHUD loadingDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setDimAmount(0.5f)
                .setCancellable(false)
                .setMaxProgress(100);
        return loadingDialog;
    }

    public static String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            Log.e("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
            return obj.getAddressLine(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static void showAlertDialog(String title, String message, Context context, DialogBtnClickInterface mDialogInterface) {

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialogInterface.onClick(true);
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialogInterface.onClick(false);
                dialog.dismiss();
            }
        }).setIcon(R.mipmap.ic_launcher).show();
    }

    public static void showLottieDialog(Context context, String msg, int animation, DialogBtnClickInterface dialogBtnClickInterface) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.lottie_dialog, null);
        LottieAnimationView animationView = dialogView.findViewById(R.id.animation_view);
        animationView.setAnimation(animation);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText(msg);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                dialogBtnClickInterface.onClick(true);
            }
        }, 3000);

    }

    public static String nullCheck(String string) {
        return string == null ? "" : string;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static int getFileSize(File file) {
        return (int) file.length() / (1024 * 1024);
    }

    public static int checkExtentionValidation(String attachment) {
        int a = -1;
        for (String s : Constants.imagesExtensions) {
            if (attachment.contains(s)) {
                return 0;
            }
        }
        for (String s : Constants.videosExtensions) {
            if (attachment.contains(s)) {
                return 1;
            }
        }
        return a;
    }

    public static String getFileExtention(String fileName) {

        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static byte[] videoThumbnail(String path) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    public static LocationModel getLocationFromLatLng(String lat, String lng, Context context) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();


        return new LocationModel(address, city, state, country, postalCode, knownName);


    }


    public static String getPath(Context context, Uri uri) {
        if (uri.toString().contains("media/external")) {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (uri.toString().startsWith("content://com.google.android.apps.photos.content")) {
            String type = "";
            try (
                    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int colIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                    type = cursor.getString(colIndex);
                    Log.d("Check Type :: ", type + "");

                }
            } catch (
                    IllegalArgumentException e) {
                Log.d("Check Type :: ", type + "");
                e.printStackTrace();
            }
        } else {
            try {
//                try (Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
//                    if (cursor != null && cursor.moveToFirst()) {
//                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                    }
//                }

                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();
                cursor = context.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                cursor.close();
                return path;
            } catch (Exception e) {
                e.printStackTrace();
                return uri.getPath();
            }
        }
        return "";
    }

    public static int getIndexFromList(List<PostModel> postModels, int id) {
        for (int i = 0; i < postModels.size(); i++) {
            if (postModels.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    public static IdsAndTagsListModel updateETwithTags(FollowModel follower, EditText et_comment, ArrayList<FollowModel> taggedUsersIds) {

        String ids;
        String tags;

        String name = follower.getFollowingUsername();
        if (name.contains(" "))
            name = "@" + name.substring(0, name.indexOf(" ")) + " ";
        else
            name = "@" + name + " ";
        taggedUsersIds.add(follower);

        ids = (String.valueOf(follower.getId()));
        tags = (name.trim());

        String wholeString = et_comment.getText().toString();
        wholeString = wholeString.substring(0, wholeString.lastIndexOf("@"));
        wholeString = wholeString + name;
        String text = "";
        String[] wordsArray = wholeString.split(" ");
        Spannable spannable = new SpannableString(wholeString);
        Log.d("TAG", "onFollowerClick: " + spannable);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.RED);

        Log.d("TAG", "onFollowerClick: " + wordsArray.length);
        for (int j = 0; j < wordsArray.length; j++) {
            String s = wordsArray[j];
            if (s.contains("@") && s.length() > 1) {
                text = text + "<font color=#EDBB1F>" + s + "</font>" + " ";
            } else {
                text = text + s + " ";
            }
            et_comment.setText(Html.fromHtml(text));
        }
        et_comment.setSelection(et_comment.getText().toString().length());

        return new IdsAndTagsListModel(ids, tags);
    }

    public static IdsAndTagsListModel checkETTagsInEditText(EditText et_post, List<FollowModel> taggedUsers, List<String> ids, List<String> tags) {

        String mIds;
        String mTags;
        String mAddPost;
        mAddPost = et_post.getText().toString();
        for (FollowModel follower : taggedUsers) {
            String firstName = follower.getFollowingUsername();
            if (!mAddPost.contains(firstName)
                    && ids.contains(String.valueOf(follower.getFollowingId()))) {
                taggedUsers.remove(follower);
                ids.remove(String.valueOf(follower.getFollowingId()));
                tags.remove("@" + firstName);
            } else if (mAddPost.contains(firstName)
                    && !ids.contains(String.valueOf(follower.getFollowingId()))) {
                taggedUsers.remove(follower);
                tags.remove("@" + firstName);
                ids.remove(follower.getFollowingId());
            }
        }
        mTags = android.text.TextUtils.join(",", tags);
        mIds = android.text.TextUtils.join(",", ids);

        return new IdsAndTagsListModel(mIds, mTags);
    }

    public static void highlightTags(Context context, EditText et_comment, String comment) {
        SpannableString ss = new SpannableString(comment);
        String remainingString = comment;
        ArrayList<Integer> temp = new ArrayList<>();

        int first, last, previous = 0;
        int counter = 0;
        for (int i = 0; i < comment.length(); i++) {
            if (comment.charAt(i) == ' ') {
                remainingString = comment.substring(i + 1);
            } else {
                if (comment.charAt(i) == '@' && !temp.contains(i)) {
                    temp.add(i);
                    first = i;
                    if (remainingString.contains(" ")) {
                        last = first + remainingString.indexOf(" ");
                    } else {
                        last = comment.length() - 1;
                    }
                    ForegroundColorSpan fcsRed = new ForegroundColorSpan(context.getResources().getColor(R.color.buttercup));
//                    Log.d("sss", "highlightTags: first "+first+" last "+last);
                    ss.setSpan(fcsRed, first, last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    previous = last + 1;
                    remainingString = comment.substring(previous);
                    counter++;
                }
            }
        }
        et_comment.setText(ss);
        et_comment.setSelection(et_comment.getText().toString().length());
    }

//    public static String parseDate(String time) {
//        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        try {
//            Date datea = utcFormat.parse(time);
//            DateFormat pstFormat = new SimpleDateFormat("MM/dd/yyyy");
//            pstFormat.setTimeZone(TimeZone.getDefault());
//            return pstFormat.format(datea);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return getDateFromMilliSec(time);
//        }
//    }


    public static String getVideoDuration(Context context, Uri uri) {

        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(uri.toString()));
        int duration = mp.getDuration();
        mp.release();
        /*convert millis to appropriate time*/
        return String.format("%d.%d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public static void setVerifiedAccount(String verified, TextView textView) {
        if (verified != null) {
            try {
                if (verified.equals("1")) {
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                    textView.setCompoundDrawablePadding(5);
                } else {
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                }
            } catch (Exception e) {
            }

        }


    }

    public static String convertToUTCTime(String s) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date1.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfT.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = sdfT.format(new Date(millis));
        return utcTime;
    }

    public static String convertFromUTCTime(String s) {
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date datea = utcFormat.parse(s);
            DateFormat pstFormat = new SimpleDateFormat("MM/dd/yyyy");
            pstFormat.setTimeZone(TimeZone.getDefault());
            return pstFormat.format(datea);

        } catch (ParseException e) {
            e.printStackTrace();
            return getDateFromMilliSec(s);
        }
//        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date date = null;
//        try {
//            date = utcFormat.parse(s);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        DateFormat pstFormat = new SimpleDateFormat("MM/dd/yyyy");
//        pstFormat.setTimeZone(TimeZone.getDefault());
//        return pstFormat.format(date);
    }

    public static String getTimeFromDate(String s) {
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = utcFormat.parse(s);
            DateFormat pstFormat = new SimpleDateFormat("hh:mm a");
            pstFormat.setTimeZone(TimeZone.getDefault());
            return pstFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return getDateFromMilliSec(s);
        }
    }

    public static void setWidthAndHeight(RelativeLayout view, int width, int height) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.setMargins(0, 0, (width / 100) * 5, (height / 100) * 10);
        view.setLayoutParams(layoutParams);
        view.requestLayout();
    }

    //    public static void setHalfScreen(RelativeLayout view,  int width, int height) {
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(layoutParams);
//        view.requestLayout();
//    }
//    public static void setBottomHalfScreen(RelativeLayout view,  int width, int height) {
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        view.setLayoutParams(layoutParams);
//        view.requestLayout();
//    }
    public static void setFullScreen(Context context, RelativeLayout view, int width, int height) {
        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.getResources().getDisplayMetrics());//used to convert you width integer value same as dp
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());
        view.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        view.requestLayout();
    }

    public static void blink(MaterialCardView cardLive) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (cardLive.getVisibility() == View.VISIBLE) {
                            cardLive.setVisibility(View.INVISIBLE);
                        } else {
                            cardLive.setVisibility(View.VISIBLE);
                        }
                        blink(cardLive);
                    }
                });
            }
        }).start();
    }

    public static Uri getCompressedUri(Uri uri, Context context) {
        InputStream imageStream = null;
        try {
            imageStream = context.getContentResolver().openInputStream(
                    uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
            stream = null;
        } catch (IOException e) {

            e.printStackTrace();
        }
        return getImageUri(context, bmp);
    }
}
