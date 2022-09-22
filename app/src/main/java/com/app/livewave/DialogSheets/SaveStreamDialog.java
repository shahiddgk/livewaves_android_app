package com.app.livewave.DialogSheets;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.app.livewave.R;

public class SaveStreamDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "SaveStreamDialog";


    private SaveStreamListener saveStreamListener;


    TextView cancelBtn, okayBtn;
    Context context;

    public SaveStreamDialog(SaveStreamListener saveStreamListener) {

        this.saveStreamListener = saveStreamListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_save_stream, container, false);
        initViews(view);
        initClickListeners();
        context = getActivity();
        return view;
    }

    private void initClickListeners() {
        cancelBtn.setOnClickListener(this);
        okayBtn.setOnClickListener(this);
    }

    private void initViews(View view) {
        cancelBtn = view.findViewById(R.id.btn_no);
        okayBtn = view.findViewById(R.id.btn_yes);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_yes) {
            saveStreamListener.onYesButtonClickListener(true);
            saveStreamListener.onNoButtonClickListener(false);
            this.dismiss();

        } else if (view.getId() == R.id.btn_no){
            Log.e(TAG, "onClick: "  );
            saveStreamListener.onYesButtonClickListener(false);
            saveStreamListener.onNoButtonClickListener(true);
            this.dismiss();
        }

    }

}
