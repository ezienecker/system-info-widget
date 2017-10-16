package com.manuzid.systeminfowidget.preferences.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.manuzid.systeminfowidget.R;

import java.util.Set;

import static com.manuzid.systeminfowidget.category.BatteryCategory.BATTERY;
import static com.manuzid.systeminfowidget.category.CameraCategory.CAMERA;
import static com.manuzid.systeminfowidget.category.DisplayCategory.DISPLAY;
import static com.manuzid.systeminfowidget.category.GeneralCategory.GENERAL;
import static com.manuzid.systeminfowidget.category.MemoryCategory.MEMORY;
import static com.manuzid.systeminfowidget.category.MoreCategory.MORE;
import static com.manuzid.systeminfowidget.category.NetworkCategory.NETWORK;

/**
 * Created by Emanuel Zienecker on 10.10.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class CategoryDialog implements DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener {
    AlertDialog.Builder categoryBuilder;
    boolean[] selectedCategories;
    int count = 6;
    Context mContext;

    private CategoryDialog(Context context, Set<String> selectedCategoriesFromSharedPreferences) {
        this.mContext = context;
        this.categoryBuilder = new AlertDialog.Builder(mContext);
        this.categoryBuilder.setTitle(mContext.getString(R.string.config_category_summary));
        selectedCategories = getCheckedCategoriesFromSharedPreferences(selectedCategoriesFromSharedPreferences);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.e("TAG", "Folgender Button: " + which);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        count += isChecked ? 1 : -1;
        selectedCategories[which] = isChecked;

        if (count > 6) {
            Toast.makeText(mContext.getApplicationContext(), mContext.getString(R.string.config_category_choice_too_much_items_selected), Toast.LENGTH_SHORT).show();
            selectedCategories[which] = false;
            count--;
            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
        }
    }

    private boolean[] getCheckedCategoriesFromSharedPreferences(Set<String> selectedCategories) {
        return new boolean[]{
                selectedCategories.contains(GENERAL),
                selectedCategories.contains(MORE),
                selectedCategories.contains(DISPLAY),
                selectedCategories.contains(CAMERA),
                selectedCategories.contains(MEMORY),
                selectedCategories.contains(BATTERY),
                selectedCategories.contains(NETWORK)};
    }

}
