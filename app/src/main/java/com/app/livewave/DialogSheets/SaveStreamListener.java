package com.app.livewave.DialogSheets;

public interface SaveStreamListener {
    void onYesButtonClickListener(boolean yes);
    void onNoButtonClickListener(boolean no);
    void paidAmount(String amount);
    void getDescription(String des);

}
