package com.app.livewave.DialogSheets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.app.livewave.BottomDialogSheets.EnterAmountDialogSheet;
import com.app.livewave.R;
import com.app.livewave.interfaces.MessageInterface;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SaveStreamDialog implements View.OnClickListener {

    private static final String TAG = "SaveStreamDialog";


    private SaveStreamListener saveStreamListener;


    TextView cancelBtn, okayBtn;
    Context context;
    SwitchMaterial paid;
    Dialog dialog;
    FragmentActivity activity;
    EditText description;

    public SaveStreamDialog(SaveStreamListener saveStreamListener, FragmentActivity activity) {

        this.saveStreamListener = saveStreamListener;
        this.activity = activity;
        getDialog();

    }

    private void getDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_save_stream);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initViews(dialog);
        initClickListeners();
        dialog.show();


    }


    private void initClickListeners() {
        cancelBtn.setOnClickListener(this);
        okayBtn.setOnClickListener(this);
    }

    private void initViews(Dialog view) {
        cancelBtn = view.findViewById(R.id.btn_no);
        okayBtn = view.findViewById(R.id.btn_yes);
        paid = view.findViewById(R.id.switchPaid);
        description = view.findViewById(R.id.tvDescription);
        paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    new EnterAmountDialogSheet(new MessageInterface() {
                        @Override
                        public void IAmountEnter(String amount) {
                            if (amount == null) {
                                paid.setChecked(false);

                                saveStreamListener.paidAmount(null);
                            } else {
                                saveStreamListener.paidAmount(amount);
                                paid.setText("Paid $ " + amount);
                            }


                        }
                    }).show(activity.getSupportFragmentManager(), "Amount");

                }else {
                    paid.setText("Paid $ ");
                    saveStreamListener.paidAmount(null);

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_yes) {
            if (description.getText().toString().equals("") || description.getText().toString().isEmpty()) {
                description.setError("This field is required!");
            } else {
                String des = description.getText().toString();
                Log.e(TAG, "onClick: " + des );
                saveStreamListener.onYesButtonClickListener(true);
                saveStreamListener.onNoButtonClickListener(false);
                saveStreamListener.getDescription(des);
                dialog.dismiss();
            }


        } else if (view.getId() == R.id.btn_no) {
            Log.e(TAG, "onClick: ");
            saveStreamListener.onYesButtonClickListener(false);
            saveStreamListener.onNoButtonClickListener(true);
            dialog.dismiss();
        }

    }

}
