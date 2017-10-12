package com.manuzid.systeminfowidget.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.SysInfoMainProvider;
import com.manuzid.systeminfowidget.category.AbstractCategory;
import com.manuzid.systeminfowidget.util.AppRater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.manuzid.systeminfowidget.Constants.BACKGROUND_RESOURCE_METHOD_NAME;
import static com.manuzid.systeminfowidget.category.BatteryCategory.BATTERY;
import static com.manuzid.systeminfowidget.category.CameraCategory.CAMERA;
import static com.manuzid.systeminfowidget.category.DisplayCategory.DISPLAY;
import static com.manuzid.systeminfowidget.category.GeneralCategory.GENERAL;
import static com.manuzid.systeminfowidget.category.MemoryCategory.MEMORY;
import static com.manuzid.systeminfowidget.category.MoreCategory.MORE;
import static com.manuzid.systeminfowidget.category.NetworkCategory.NETWORK;

/**
 * Created by Emanuel Zienecker on 05.06.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
public class ConfigPreferencesActivity extends Activity {
    private static final String TAG = ConfigPreferencesActivity.class.getSimpleName();

    public static final String CATEGORY_SELECTION = "manuzid-category-selection";

    public static final String TEMP_FORMAT = "temp_format";
    public static final String TEMP_CELSIUS = "celsius";
    public static final String TEMP_FAHRENHEIT = "fahrenheit";
    public static final String COLOR_SCHEME = "color_scheme";
    public static final String COLOR_BLUE = "color_blue";
    public static final String COLOR_RED = "color_red";
    public static final String COLOR_LILA = "color_lila";
    public static final String COLOR_ORANGE = "color_orange";
    public static final String COLOR_GREEN = "color_green";
    public static final String COLOR_BLACK = "color_black";

    private static final String SET_BACKGROUND_RES = "setBackgroundResource";

    private ListView preferencesListView;
    private List<SystemInfoPreference> prefData;
    private final Context mContext = this;
    private AlertDialog alertDialogTemp;
    private AlertDialog alertDialogCategory;
    private AlertDialog alertDialogColor;
    private SharedPreferences prefs;
    private String colorScheme;
    private String tempFormat;
    private String mVersionNumber;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sysinfo_preferences);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        preferencesListView = (ListView) findViewById(R.id.preferences_listview);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        tempFormat = prefs.getString(TEMP_FORMAT, TEMP_CELSIUS);
        colorScheme = prefs.getString(COLOR_SCHEME, COLOR_BLUE);

        mVersionNumber =
                getAppVersionNumber(getApplicationContext().getPackageName(),
                        getApplicationContext().getPackageManager());

        prefData = new ArrayList<>();
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_general_app_config)), "",
                true));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_colorstyle)),
                String.format(getResources().getString(R.string.config_colorstyle)), false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_temp_title)),
                String.format(getResources().getString(R.string.config_temp_summary)), false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_category_title)),
                String.format(getResources().getString(R.string.config_category_summary)), false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_entrys_about_title)), "",
                true));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_entrys_about)),
                String.format(getResources().getString(R.string.config_entrys_about_app_summary), mVersionNumber),
                false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_general_rate)),
                String.format(getResources().getString(R.string.config_general_rate_summary)), false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_general_friend)),
                String.format(getResources().getString(R.string.config_general_friend_summary)), false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_general_send_feedback)),
                String.format(getResources().getString(R.string.config_general_send_feedback_summary)), false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_general_moreapps)),
                String.format(getResources().getString(R.string.config_general_moreapps_summary)), false));
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_entries_rights_title)), "",
                true));
        prefData.add(new SystemInfoPreference(
                String.format(getResources().getString(R.string.config_entries_rights_legal_title)), "", false));

        PreferencesAdapter prefAdap = new PreferencesAdapter(getApplicationContext(), prefData);
        preferencesListView.setAdapter(prefAdap);
        preferencesListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                SystemInfoPreference preferenceObject = (SystemInfoPreference) preferencesListView.getAdapter().getItem(arg2);

                if (String.format(getResources().getString(R.string.config_colorstyle)).equals(preferenceObject.title)) {
                    showCustomDialog(0);
                } else if (String.format(getResources().getString(R.string.config_temp_title)).equals(
                        preferenceObject.title)) {
                    showCustomDialog(1);
                } else if (String.format(getResources().getString(R.string.config_category_title)).equals(
                        preferenceObject.title)) {
                    showCustomDialog(2);
                } else if (String.format(getResources().getString(R.string.config_general_rate)).equals(
                        preferenceObject.title)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                            + getApplicationContext().getPackageName())));
                } else if (String.format(getResources().getString(R.string.config_general_friend)).equals(
                        preferenceObject.title)) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("mailto:");
                    buffer.append("");
                    buffer.append("?subject=");
                    buffer.append(getResources().getString(R.string.support_subject));
                    buffer.append("&body=");
                    buffer.append(getResources().getString(R.string.support_text));
                    buffer.append("\n");
                    buffer.append("https://market://details?id=" + getApplicationContext().getPackageName());
                    String uriString = buffer.toString().replace(" ", "%20");

                    startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)),
                            getResources().getString(R.string.config_general_friend)));
                } else {
                    if (String.format(getResources().getString(R.string.config_general_send_feedback)).equals(
                            preferenceObject.title)) {
                        String feedbackString = "mailto:"
                                + "manuzidtv@gmail.com"
                                + "?subject="
                                + "Feedback"
                                + "&body="
                                + getResources().getString(R.string.feedback_app_version) + " " + mVersionNumber
                                + "\n"
                                + getResources().getString(R.string.feedback_os_version) + " " + android.os.Build.VERSION.RELEASE
                                + "\n"
                                + getResources().getString(R.string.feedback_device_name) + " " + android.os.Build.MODEL
                                + "\n"
                                + getResources().getString(R.string.feedback_problem_disc);

                        final String uriString = feedbackString.replace(" ", "%20");

                        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)),
                                getResources().getString(R.string.feedback_mail_subject)));
                    } else if (String.format(getResources().getString(R.string.config_general_moreapps)).equals(
                            preferenceObject.title)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=ManuZiD")));
                    } else if (String.format(getResources().getString(R.string.config_entries_rights_legal_title)).equals(
                            preferenceObject.title)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, null, getApplicationContext(),
                                RightsLegalActivity.class));
                    }
                }

            }

            private void showCustomDialog(int id) {
                switch (id) {
                    case 0:
                        final CharSequence[] colorItems =
                                {
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_blue)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_red)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_orange)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_lila)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_green)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_black))};
                        int checkedColorItem = 0;
                        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        colorScheme = prefs.getString(COLOR_SCHEME, COLOR_BLUE);

                        switch (colorScheme) {
                            case COLOR_BLUE:
                                checkedColorItem = 0;
                                break;
                            case COLOR_RED:
                                checkedColorItem = 1;
                                break;
                            case COLOR_ORANGE:
                                checkedColorItem = 2;
                                break;
                            case COLOR_LILA:
                                checkedColorItem = 3;
                                break;
                            case COLOR_GREEN:
                                checkedColorItem = 4;
                                break;
                            case COLOR_BLACK:
                                checkedColorItem = 5;
                                break;
                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(String.format(getResources().getString(R.string.config_colorstyle)));
                        builder.setSingleChoiceItems(colorItems, checkedColorItem, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_BLUE).commit();
                                        } else {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_BLUE).commit();
                                        }
                                        break;

                                    case 1:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_RED).commit();
                                        } else {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_RED).commit();
                                        }
                                        break;
                                    case 2:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_ORANGE).commit();
                                        } else {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_ORANGE).commit();
                                        }
                                        break;
                                    case 3:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_LILA).commit();
                                        } else {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_LILA).commit();
                                        }
                                        break;
                                    case 4:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_GREEN).commit();
                                        } else {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_GREEN).commit();
                                        }
                                        break;
                                    case 5:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_BLACK).commit();
                                        } else {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_BLACK).commit();
                                        }
                                        break;
                                }

                                alertDialogColor.dismiss();

                            }
                        });
                        alertDialogColor = builder.create();
                        alertDialogColor.show();
                        break;
                    case 1:
                        final CharSequence[] tempItems =
                                {String.format(mContext.getResources().getString(R.string.config_temp_celcius)),
                                        String.format(mContext.getResources().getString(R.string.config_temp_fahrenheit))};
                        int checkedTempItem = 0;
                        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        tempFormat = prefs.getString(TEMP_FORMAT, TEMP_CELSIUS);
                        if (TEMP_CELSIUS.equals(tempFormat)) {
                            checkedTempItem = 0;
                        } else if (TEMP_FAHRENHEIT.equals(tempFormat)) {
                            checkedTempItem = 1;
                        }
                        final AlertDialog.Builder tempBuilder = new AlertDialog.Builder(mContext);
                        tempBuilder.setTitle(String.format(getResources().getString(R.string.config_colorstyle)));
                        tempBuilder.setSingleChoiceItems(tempItems, checkedTempItem, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            prefs.edit().putString(TEMP_FORMAT, TEMP_CELSIUS).commit();
                                        } else {
                                            prefs.edit().putString(TEMP_FORMAT, TEMP_CELSIUS).commit();
                                        }
                                        break;
                                    case 1:
                                        if (prefs == null) {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            prefs.edit().putString(TEMP_FORMAT, TEMP_FAHRENHEIT).commit();
                                        } else {
                                            prefs.edit().putString(TEMP_FORMAT, TEMP_FAHRENHEIT).commit();
                                        }
                                        break;
                                }

                                alertDialogTemp.dismiss();

                            }
                        });
                        alertDialogTemp = tempBuilder.create();
                        alertDialogTemp.show();
                        break;
                    case 2:
                        final CharSequence[] categoryItems =
                                {mContext.getString(R.string.config_category_choice_general),
                                        mContext.getResources().getString(R.string.config_category_choice_more),
                                        mContext.getResources().getString(R.string.config_category_choice_display),
                                        mContext.getResources().getString(R.string.config_category_choice_camera),
                                        mContext.getResources().getString(R.string.config_category_choice_memory),
                                        mContext.getResources().getString(R.string.config_category_choice_battery),
                                        mContext.getResources().getString(R.string.config_category_choice_network)};

                        final boolean[] selectedCategories = getCheckedCategoriesFromSharedPreferences(mContext);

                        final AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(mContext);
                        categoryBuilder.setTitle(mContext.getString(R.string.config_category_summary));
                        categoryBuilder.setMultiChoiceItems(categoryItems, selectedCategories, new DialogInterface.OnMultiChoiceClickListener() {

                            int count = 6;

                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                count += isChecked ? 1 : -1;
                                selectedCategories[which] = isChecked;

                                if (count > 6) {
                                    Toast.makeText(getApplicationContext(), mContext.getString(R.string.config_category_choice_too_much_items_selected), Toast.LENGTH_SHORT).show();
                                    selectedCategories[which] = false;
                                    count--;
                                    ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                                }
                            }
                        });

                        categoryBuilder.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final SparseBooleanArray checkedItemPositions = ((AlertDialog) dialog).getListView().getCheckedItemPositions();

                                int count = 0;
                                for (int i = 0; i < checkedItemPositions.size(); i++) {
                                    final boolean checkedState = checkedItemPositions.get(i);
                                    if (checkedState)
                                        count++;
                                }

                                if (count == 6) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                    Set<String> categoriesSelected = new LinkedHashSet<>();

                                    if (checkedItemPositions.get(0))
                                        categoriesSelected.add(GENERAL);

                                    if (checkedItemPositions.get(1))
                                        categoriesSelected.add(MORE);

                                    if (checkedItemPositions.get(2))
                                        categoriesSelected.add(DISPLAY);

                                    if (checkedItemPositions.get(3))
                                        categoriesSelected.add(CAMERA);

                                    if (checkedItemPositions.get(4))
                                        categoriesSelected.add(MEMORY);

                                    if (checkedItemPositions.get(5))
                                        categoriesSelected.add(BATTERY);

                                    if (checkedItemPositions.get(6))
                                        categoriesSelected.add(NETWORK);

                                    prefs.edit().putStringSet(CATEGORY_SELECTION, categoriesSelected).apply();
                                } else {
                                    Log.e(TAG, "Der Benutzer hat ungleich 6 Einträge ausgewählt. Es sind " + count + " Einträge ausgewählt!");
                                    Toast.makeText(getApplicationContext(), mContext.getString(R.string.config_category_choice_too_little_items_selected), Toast.LENGTH_SHORT).show();
                                }

                                alertDialogCategory.dismiss();
                            }
                        });

                        categoryBuilder.setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogCategory.dismiss();
                            }
                        });


                        alertDialogCategory = categoryBuilder.create();
                        alertDialogCategory.show();
                        break;
                }
            }

        });

        AppRater.app_launched(mContext);

    }

    private String getAppVersionNumber(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            return "?";
        }
    }

    @Override
    protected void onPause() {
        updateAppWidget();
        Toast.makeText(mContext, mContext.getResources().getString(R.string.config_save_changes), Toast.LENGTH_LONG).show();
        super.onPause();
    }

    private void updateAppWidget() {
        final Context context = ConfigPreferencesActivity.this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        // Hier die UI setzen

        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);

        remoteView.setViewVisibility(R.id.lblFirstInfo, View.GONE);
        remoteView.setViewVisibility(R.id.txtFirstInfo, View.GONE);
        remoteView.setViewVisibility(R.id.lblSecondInfo, View.GONE);
        remoteView.setViewVisibility(R.id.txtSecondInfo, View.GONE);
        remoteView.setViewVisibility(R.id.lblThird, View.GONE);
        remoteView.setViewVisibility(R.id.txtThird, View.GONE);
        remoteView.setViewVisibility(R.id.lblFourth, View.GONE);
        remoteView.setViewVisibility(R.id.txtFourth, View.GONE);
        remoteView.setViewVisibility(R.id.lblFifth, View.GONE);
        remoteView.setViewVisibility(R.id.txtFifth, View.GONE);
        remoteView.setViewVisibility(R.id.txtSupportedPictureSizes, View.GONE);
        remoteView.setViewVisibility(R.id.lblSixth, View.GONE);
        remoteView.setViewVisibility(R.id.txtSixth, View.GONE);
        remoteView.setViewVisibility(R.id.lblSeventh, View.GONE);
        remoteView.setViewVisibility(R.id.txtSeventh, View.GONE);
        remoteView.setViewVisibility(R.id.device_memory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.usb_memory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.device_memory_progress_bar, View.GONE);
        remoteView.setViewVisibility(R.id.usb_memory_progress_bar, View.GONE);
        remoteView.setViewVisibility(R.id.imgRestore, View.VISIBLE);
        remoteView.setViewVisibility(R.id.txtConfigClick, View.VISIBLE);

        final String colorScheme;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        colorScheme = prefs.getString(ConfigPreferencesActivity.COLOR_SCHEME, ConfigPreferencesActivity.COLOR_BLUE);

        switch (colorScheme) {
            case ConfigPreferencesActivity.COLOR_BLUE:
                remoteView.setInt(R.id.relative_general, SET_BACKGROUND_RES, R.drawable.relative_background_blue);
                break;
            case ConfigPreferencesActivity.COLOR_RED:
                remoteView.setInt(R.id.relative_general, SET_BACKGROUND_RES, R.drawable.rela_background_red);
                break;
            case ConfigPreferencesActivity.COLOR_ORANGE:
                remoteView.setInt(R.id.relative_general, SET_BACKGROUND_RES, R.drawable.rela_background_orange);
                break;
            case ConfigPreferencesActivity.COLOR_LILA:
                remoteView.setInt(R.id.relative_general, SET_BACKGROUND_RES, R.drawable.rela_background_purple);
                break;
            case ConfigPreferencesActivity.COLOR_GREEN:
                remoteView.setInt(R.id.relative_general, SET_BACKGROUND_RES, R.drawable.rela_background_green);
                break;
            case ConfigPreferencesActivity.COLOR_BLACK:
                remoteView.setInt(R.id.relative_general, SET_BACKGROUND_RES, R.drawable.rela_background_black);
                break;
        }

        registerOnClickPendingIntentForCategories(context, remoteView, mAppWidgetId);

        SysInfoMainProvider.updateAppWidget(appWidgetManager, mAppWidgetId, remoteView);
    }

    private void registerOnClickPendingIntentForCategories(Context context, RemoteViews remoteView, int appWidgetId) {
        if (SysInfoMainProvider.availableCategories == null)
            return;

        int buttonCounter = 0;

        final Set<String> selectedCategoriesFromSharedPreferences = getSelectedCategoriesFromSharedPreferences(context);

        for (String category : selectedCategoriesFromSharedPreferences) {
            final AbstractCategory lCategory = SysInfoMainProvider.availableCategories.get(category);

            // 1 Den Button das entsprechende PendingIntent zuweisen
            remoteView.setOnClickPendingIntent(lCategory.getButtonId(),
                    preparePendingIntent(context, lCategory.getRequestAction(), appWidgetId, lCategory.getRequestCode()));

            // 2 Kategorie-Drawable für den Button setzen
            remoteView.setInt(lCategory.getButtonId(), BACKGROUND_RESOURCE_METHOD_NAME,
                    lCategory.getDefaultButtonDrawable());
        }
    }

    private PendingIntent preparePendingIntent(Context context, String reqAction, int appWidgetId, int reqCode) {
        Intent preparedIntent = new Intent();
        preparedIntent.setAction(reqAction);
        preparedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, reqCode, preparedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Set<String> getSelectedCategoriesFromSharedPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> categorySelection = prefs.getStringSet(CATEGORY_SELECTION, null);

        if (categorySelection == null) {
            return new LinkedHashSet<>(Arrays.asList(GENERAL, MORE, DISPLAY, CAMERA, MEMORY, BATTERY));
        }

        return categorySelection;
    }

    private boolean[] getCheckedCategoriesFromSharedPreferences(Context context) {
        final Set<String> selectedCategoriesFromSharedPreferences = getSelectedCategoriesFromSharedPreferences(context);

        return new boolean[]{
                selectedCategoriesFromSharedPreferences.contains(GENERAL),
                selectedCategoriesFromSharedPreferences.contains(MORE),
                selectedCategoriesFromSharedPreferences.contains(DISPLAY),
                selectedCategoriesFromSharedPreferences.contains(CAMERA),
                selectedCategoriesFromSharedPreferences.contains(MEMORY),
                selectedCategoriesFromSharedPreferences.contains(BATTERY),
        selectedCategoriesFromSharedPreferences.contains(NETWORK)};
    }

}