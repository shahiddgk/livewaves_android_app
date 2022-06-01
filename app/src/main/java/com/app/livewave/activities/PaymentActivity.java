package com.app.livewave.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.livewave.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
//import com.stripe.android.ApiResultCallback;
//import com.stripe.android.PaymentIntentResult;
//import com.stripe.android.Stripe;
//import com.stripe.android.model.ConfirmPaymentIntentParams;
//import com.stripe.android.model.PaymentIntent;
//import com.stripe.android.model.PaymentMethodCreateParams;
//import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    // 10.0.2.2 is the Android emulator's alias to localhost
    // 192.168.1.6 If you are testing in real device with usb connected to same network then use your IP address
    private static final String BACKEND_URL = "http://192.168.0.101:5555/"; //4242 is port mentioned in server i.e index.js
    EditText amountText;
   // CardInputWidget cardInputWidget;
    Button payButton;

    // we need paymentIntentClientSecret to start transaction
    private String paymentIntentClientSecret;
    //declare stripe
  //  private Stripe stripe;

    Double amountDouble=null;

    private OkHttpClient httpClient;

    static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_ui);
        amountText = findViewById(R.id.amount_id);
  //      cardInputWidget = findViewById(R.id.cardInputWidget);
        payButton = findViewById(R.id.payButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in progress");
        progressDialog.setCancelable(false);
        httpClient = new OkHttpClient();

        //Initialize
//        stripe = new Stripe(
//                getApplicationContext(),
//                Objects.requireNonNull("pk_test_51KCTsZCcsPMfpQpwrIQeycDeZbTffiz8gUjMzlZauMFH5zU9tKU1Z5VYuYwG1tBXhyhSbSym0WGsSnh2biJQehQ9001ScPSPcj")
//        );


        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Amount
                amountDouble = Double.valueOf(amountText.getText().toString());
                //call checkout to get paymentIntentClientSecret key
                progressDialog.show();
                startCheckout();
            }
        });
    }

    private void startCheckout() {
        {
            // Create a PaymentIntent by calling the server's endpoint.
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//        String json = "{"
//                + "\"currency\":\"usd\","
//                + "\"items\":["
//                + "{\"id\":\"photo_subscription\"}"
//                + "]"
//                + "}";
            double amount=amountDouble*100;
            Map<String,Object> payMap=new HashMap<>();
            Map<String,Object> itemMap=new HashMap<>();
            List<Map<String,Object>> itemList =new ArrayList<>();
            payMap.put("currency","INR");
            itemMap.put("id","photo_subscription");
            itemMap.put("amount",amount);
            itemList.add(itemMap);
            payMap.put("items",itemList);
            String json = new Gson().toJson(payMap);
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(BACKEND_URL + "create-payment-intent")
                    .post(body)
                    .build();
            httpClient.newCall(request)
                    .enqueue(new PayCallback(this));

        }
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<PaymentActivity> activityRef;
        PayCallback(@NonNull PaymentActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(Request request, IOException e) {
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(Response response) throws IOException {
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");

        //once you get the payment client secret start transaction
        //get card detail
//        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
//        if (params != null) {
//            //now use paymentIntentClientSecret to start transaction
//            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
//                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
//            //start payment
//            stripe.confirmPayment(PaymentActivity.this, confirmParams);
//        }
        Log.i("TAG", "onPaymentSuccess: "+paymentIntentClientSecret);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
    //    stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(PaymentActivity.this));

    }

//    private final class PaymentResultCallback
//            implements ApiResultCallback<PaymentIntentResult> {
//        @NonNull private final WeakReference<PaymentActivity> activityRef;
//        PaymentResultCallback(@NonNull PaymentActivity activity) {
//            activityRef = new WeakReference<>(activity);
//        }
//        //If Payment is successful
//        @Override
//        public void onSuccess(@NonNull PaymentIntentResult result) {
//            progressDialog.dismiss();
//            final PaymentActivity activity = activityRef.get();
//            if (activity == null) {
//                return;
//            }
//            PaymentIntent paymentIntent = result.getIntent();
//            PaymentIntent.Status status = paymentIntent.getStatus();
//            if (status == PaymentIntent.Status.Succeeded) {
//                // Payment completed successfully
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                Toast toast =Toast.makeText(activity, "Ordered Successful", Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
//                // Payment failed – allow retrying using a different payment method
//                activity.displayAlert(
//                        "Payment failed",
//                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
//                );
//            }
//        }
//        //If Payment is not successful
//        @Override
//        public void onError(@NonNull Exception e) {
//            progressDialog.dismiss();
//            final PaymentActivity activity = activityRef.get();
//            if (activity == null) {
//                return;
//            }
//            // Payment request failed – allow retrying using the same payment method
//            activity.displayAlert("Error", e.toString());
//        }
//    }
    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }
}