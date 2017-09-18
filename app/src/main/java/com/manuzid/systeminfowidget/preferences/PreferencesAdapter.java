package com.manuzid.systeminfowidget.preferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.manuzid.systeminfowidget.R;

import java.util.List;

/**
 * Created by Emanuel Zienecker on 02.05.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
class PreferencesAdapter extends BaseAdapter {
    private Context context;
    private List<Preferences> data;

    PreferencesAdapter(final Context context, final List<Preferences> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if (result == null) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final Preferences pref = data.get(position);

            if (pref.header) {
                result = inflater.inflate(R.layout.sysinfo_preferences_adapter_header, null);
                final TextView txtHeader = (TextView) result.findViewById(R.id.adap_header);
                txtHeader.setText(pref.title);
                txtHeader.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
                result.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                result.setTag(pref.title);

            } else {
                result = inflater.inflate(R.layout.sysinfo_preferences_adapter_item, null);
                final TextView txtTitle = (TextView) result.findViewById(R.id.adap_item_titel);
                final TextView txtSummary = (TextView) result.findViewById(R.id.adap_item_summary);
                txtTitle.setText(pref.title);
                txtSummary.setText(pref.summary);
                result.setTag(pref.title);
            }
        }

        return result;
    }

}