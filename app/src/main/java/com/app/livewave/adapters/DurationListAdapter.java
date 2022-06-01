package com.app.livewave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.livewave.R;

public class DurationListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] numberOfItems;
    private String[] durationList;

    public DurationListAdapter(Context context, String[] numberOfItems, String[] durationList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.numberOfItems = numberOfItems;
        this.durationList = durationList;
    }

    @Override
    public int getCount() {
        return numberOfItems.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.duration_list_item,null);
        }

        TextView textView = view.findViewById(R.id.duration_item_text);

        textView.setText(durationList[i]);

        return view;
    }
}
