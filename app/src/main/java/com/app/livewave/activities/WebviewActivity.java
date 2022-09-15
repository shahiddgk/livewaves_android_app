package com.app.livewave.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.livewave.R;
import com.app.livewave.interfaces.ApiResponseHandler;
import com.app.livewave.interfaces.DialogBtnClickInterface;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.retrofit.ApiClient;
import com.app.livewave.utils.ApiManager;
import com.app.livewave.utils.BaseUtils;
import com.app.livewave.utils.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;

import io.paperdb.Paper;
import retrofit2.Response;

import static com.app.livewave.utils.Constants.URL;
import static com.app.livewave.utils.Constants.currentUser;

public class WebviewActivity extends AppCompatActivity {

    String intentType, url;
    WebView web_view;
    private String type, amount;
    KProgressHUD dialog;

    private UserModel userModel;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private PermissionRequest myRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Paper.init(this);
        userModel = Paper.book().read(currentUser);

        intentType = getIntent().getStringExtra("intent_type");
        if (intentType != null) {
            switch (intentType) {
                case "1":
                    url = "https://sites.google.com/view/privacypolicyentertaiment/contact";
                    break;
                case "2":
                    url = "https://sites.google.com/view/privacypolicyentertaiment/help";
                    break;
                case "3":
                    url = "https://sites.google.com/view/privacypolicyentertaiment/terms";
                    break;
                case "4":
                    String userId = getIntent().getStringExtra("userId");
                    if (userId != null)
                        url = "https://livewaves.app/api/v1/chat/" + userId + "?token=" + Paper.book().read(Constants.token);
                    else
                        url = "https://livewaves.app/api/v1/chat?token=" + Paper.book().read(Constants.token);
                    break;
                case "5":
                    if (getIntent().getStringExtra(URL) != null) {
                        url = getIntent().getStringExtra(URL);
                        break;
                    }
                case "6": //for fix payments like events posts streams etc
                    url = Constants.BASE_APIS_URL + Constants.API_VERSION  + "payment/stripe?" + getIntent().getStringExtra("id") + "&type=" + getIntent().getStringExtra("type") + "&token=" + Paper.book().read(Constants.token);
                    Log.e("urlLink", "" + url);
                    break;

                case "7":// for custom payments like tips
                    url = Constants.BASE_APIS_URL + Constants.API_VERSION  + "payment/stripe?" + getIntent().getStringExtra("id") + "&amount=" + getIntent().getStringExtra("amount") + "&type=" + getIntent().getStringExtra("type") + "&token=" + Paper.book().read(Constants.token);
                    Log.e("urlLink", "" + url);
                    break;

                case "8":// for custom payments like tips
                    url = Constants.BASE_APIS_URL + Constants.API_VERSION  + "payment/stripe?" + "&type=" + getIntent().getStringExtra("type")  + "&amount=" + getIntent().getStringExtra("amount") + "&id=" + getIntent().getStringExtra("id") + "&token=" + Paper.book().read(Constants.token);
                    Log.e("urlLink", "" + url);
                    break;
                case "9":// for custom payments like tips
                    url = Constants.BASE_APIS_URL + Constants.API_VERSION  + "payment/stripe?" + "&type=" + getIntent().getStringExtra("type")  + "&subscription_id=" + getIntent().getStringExtra("subscription_id") + "&amount=" + getIntent().getStringExtra("amount") + "&subscriber_id=" + Paper.book().read("CurrentUserId")  + "&token=" + Paper.book().read(Constants.token);
                    Log.e("urlLink", "" + url);
                    break;

            }
        }
        switch (intentType) {
            case "1":
                url = "https://sites.google.com/view/privacypolicyentertaiment/contact";
                break;
            case "2":
                url = "https://sites.google.com/view/privacypolicyentertaiment/help";
                break;
            case "3":
                url = "https://sites.google.com/view/privacypolicyentertaiment/terms";
                break;
            case "4":
                String userId = getIntent().getStringExtra("userId");
                if (userId != null)
                    url = "https://livewaves.app/api/v1/chat/" + userId + "?token=" + Paper.book().read(Constants.token);
                else
                    url = "https://livewaves.app/api/v1/chat?token=" + Paper.book().read(Constants.token);
                break;
            case "5":
                if (getIntent().getStringExtra(URL) != null) {
                    url = getIntent().getStringExtra(URL);
                    break;
                }
            case "6": //for fix payments like events posts streams etc
                url = Constants.BASE_APIS_URL + Constants.API_VERSION  + "payment/stripe?" + getIntent().getStringExtra("id") + "&type=" + getIntent().getStringExtra("type") + "&token=" + Paper.book().read(Constants.token);
                Log.e("urlLink", "" + url);
                break;

            case "7":// for custom payments like tips
                url = Constants.BASE_APIS_URL + Constants.API_VERSION   + "payment/stripe?" + getIntent().getStringExtra("id") + "&amount=" + getIntent().getStringExtra("amount") + "&type=" + getIntent().getStringExtra("type") + "&token=" + Paper.book().read(Constants.token);
                Log.e("urlLink", "" + url);
                break;

            case "9":// for custom payments like tips
                url = Constants.BASE_APIS_URL + Constants.API_VERSION  + "payment/stripe?" + "&type=" + getIntent().getStringExtra("type")  + "&subscription_id=" + getIntent().getStringExtra("subscription_id") + "&amount=" + getIntent().getStringExtra("amount") + "&subscriber_id=" + Paper.book().read("CurrentUserId")  + "&token=" + Paper.book().read(Constants.token);
                Log.e("urlLink", "" + url);
                break;

        }
        initView();
    }

    @Override
    protected void onDestroy() {
        dialog.dismiss();
        if (web_view != null)
            web_view.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            startActivity(new Intent(WebviewActivity.this, HomeActivity.class));
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        web_view = findViewById(R.id.web_view);
        dialog = BaseUtils.progressDialog(WebviewActivity.this);
        WebSettings webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        web_view.loadUrl(url);
        web_view.getSettings().setPluginState(WebSettings.PluginState.ON);
        web_view.getSettings().setAppCacheEnabled(true);
        web_view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        web_view.getSettings().setDatabaseEnabled(true);
        web_view.addJavascriptInterface(new WebAppInterface(this), "Android");
        web_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final JsResult res = result;
                AlertDialog.Builder builder = new AlertDialog.Builder(WebviewActivity.this);
                builder.setTitle("Message");
                builder.setMessage(message);
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        res.confirm();
                    }
                });
                builder.create();
                builder.show();
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                myRequest = request;
                Log.d("ew1!", request.getResources()[0] + "");                // Generally you want to check which permissions you are granting
                for (String permission : request.getResources()) {
                    if ("android.webkit.resource.AUDIO_CAPTURE".equals(permission)) {
//                        requestMicrophone();
                        askForPermission(request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                }
            }

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // make sure there is no existing message
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
//                    Toast.makeText(getApplicationContext(), "Cannot open file chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

        });
        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
                if (url.equals("alert://alert")) {
//                    Toast.makeText(WebviewActivity.this, "alert", Toast.LENGTH_LONG).show();
                } else if (url.equals("choose://image")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");

                    startActivityForResult(intent, 12);
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
//                customLoadingIndicator.hideIndicator();
            }
        });
    }

    public void askForPermission(String origin, String permission, int requestCode) {
        Log.d("WebView", "inside askForPermission for" + origin + "with" + permission);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(WebviewActivity.this,
                    permission)) {
            } else {
                ActivityCompat.requestPermissions(WebviewActivity.this,
                        new String[]{permission},
                        requestCode);
            }
        } else {
            myRequest.grant(myRequest.getResources());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                Log.d("WebView", "PERMISSION FOR AUDIO");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myRequest.grant(myRequest.getResources());

                } else {
                    BaseUtils.showLottieDialog(WebviewActivity.this, "Enable Permission to use Microphone", R.raw.invalid, new DialogBtnClickInterface() {
                        @Override
                        public void onClick(boolean positive) {

                        }
                    });
//                    BaseUtils.showToast(WebviewActivity.this, "Enable Permission to use Microphone");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            uploadMessage = null;
        }
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showMessage(String toast) {
            if (getIntent().getStringExtra("type").equals("stream")) {

                if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                    getStreamById(getIntent().getStringExtra("id"));
                } else {
                    Toast.makeText(mContext, "Permission Denied -> Go to Setting -> Allow Permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(
                            WebviewActivity.this,
                            new String[]{
                                    android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.RECORD_AUDIO},
                            1);
                }
            } else {
                finish();
            }

        }
    }

    private void getStreamById(String id) {
//        System.out.println("STREAM DETAILS");
//        System.out.println(id);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
                String streamId = id.substring(id.lastIndexOf("=") + 1);
                ApiManager.apiCall(ApiClient.getInstance().getInterface().getStream(streamId), WebviewActivity.this, new ApiResponseHandler<StreamModel>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<StreamModel>> data) {
                        StreamModel streamModel = data.body().getData();
//                        System.out.println("STREAM DETAILS");
//                        System.out.println(streamModel.getId());
//                        System.out.println(streamModel.getPlatformID());
//                        System.out.println(streamModel.getTitle());

                        Intent intent = new Intent(WebviewActivity.this, SubscriberActivity.class);
                        intent.putExtra("ID", streamModel.getId());
                        intent.putExtra("Subscriber","Subscriber");
                        intent.putExtra("TITLE", streamModel.getTitle());
                        intent.putExtra("PLATFORM_ID", streamModel.getPlatformID());
                        intent.putExtra("STREAM_ID_TYPE", "stream_id");
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}