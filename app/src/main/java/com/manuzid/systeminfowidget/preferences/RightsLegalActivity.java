package com.manuzid.systeminfowidget.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.manuzid.systeminfowidget.R;

import java.util.Calendar;

/**
 * Created by Emanuel Zienecker on 02.05.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
public class RightsLegalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sysinfo_config_legal);

        int year = Calendar.getInstance().get(Calendar.YEAR);

        TextView legalNotice = findViewById(R.id.config_legal_privat);
        legalNotice.setText(String.format(getString(R.string.config_entries_rights_legal_privat), year));
    }
}