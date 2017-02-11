package com.ushmodin.btcontrol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static android.R.attr.resource;

/**
 * Created by nikolay on 12.02.17.
 */

public class DeviceListAdapter extends ArrayAdapter<DeviceItem> {
    private final LayoutInflater layoutInflater;
    public DeviceListAdapter(Context context, List<DeviceItem> items) {
        super(context, android.R.layout.simple_list_item_1, items);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceItem item = getItem(position);
        View view = layoutInflater.inflate(R.layout.device_list_item, null);
        ((TextView)view.findViewById(R.id.macAddress)).setText(item.getAddress());
        ((TextView)view.findViewById(R.id.titleTextView)).setText(item.getName());
        view.setTag(item);
        return view;
    }
}
