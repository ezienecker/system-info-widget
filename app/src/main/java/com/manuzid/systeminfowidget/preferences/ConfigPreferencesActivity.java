package com.manuzid.systeminfowidget.preferences;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.SysInfoMainProvider;
import com.manuzid.systeminfowidget.util.AppRater;

/**
 * Created by Emanuel Zienecker on 05.06.13. Copyright (c) 2013 Emanuel
 * Zienecker. All rights reserved.
 */
public class ConfigPreferencesActivity extends Activity
{
    public static final String CATEGORY_SELECTION = "manuzid-category-selection";

    public static final String TEMP_FORMAT = "temp_format";
    public static final String TEMP_CELSIUS = "celsius";
    public static final String TEMP_FAHRENHEIT = "fahrenheit";
    public static final String COLOR_SCHEME = "color_sheme";
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
    private AlertDialog alertDialogColor;
    private SharedPreferences prefs;
    private String colorScheme;
    private String tempFormat;
    private String mVersionNumber;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sysinfo_preferences);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        preferencesListView = (ListView) findViewById(R.id.preferences_listview);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
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
        prefData.add(new SystemInfoPreference(String.format(getResources().getString(R.string.config_entrys_rights_title)), "",
                true));
        prefData.add(new SystemInfoPreference(
                String.format(getResources().getString(R.string.config_entrys_rights_legal_title)), "", false));

        PreferencesAdapter prefAdap = new PreferencesAdapter(getApplicationContext(), prefData);
        preferencesListView.setAdapter(prefAdap);
        preferencesListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
            {
                SystemInfoPreference preferenceObject = (SystemInfoPreference) preferencesListView.getAdapter().getItem(arg2);

                if (String.format(getResources().getString(R.string.config_colorstyle)).equals(preferenceObject.title))
                {
                    showCustomDialog(0);
                }
                else if (String.format(getResources().getString(R.string.config_temp_title)).equals(
                        preferenceObject.title))
                {
                    showCustomDialog(1);
                }
                else if (String.format(getResources().getString(R.string.config_entrys_start_up)).equals(
                        preferenceObject.title))
                {

                }
                else if (String.format(getResources().getString(R.string.config_general_rate)).equals(
                        preferenceObject.title))
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                            + getApplicationContext().getPackageName())));
                }
                else if (String.format(getResources().getString(R.string.config_general_friend)).equals(
                        preferenceObject.title))
                {
                    StringBuffer buffer = new StringBuffer();
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
                }
                else {
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
                    } else if (String.format(getResources().getString(R.string.config_entrys_rights_legal_title)).equals(
                            preferenceObject.title)) {
                        startActivity(new Intent(Intent.ACTION_VIEW, null, getApplicationContext(),
                                RightsLegalActivity.class));
                    }
                }

            }

            private void showCustomDialog(int id)
            {
                switch (id)
                {
                    case 0:
                        final CharSequence[] colorItems =
                                {
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_blue)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_red)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_orange)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_lila)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_green)),
                                        String.format(mContext.getResources().getString(R.string.config_colorstyle_black)) };
                        int checkedColorItem = 0;
                        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        colorScheme = prefs.getString(COLOR_SCHEME, COLOR_BLUE);

                        switch (colorScheme)
                        {
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
                        builder.setSingleChoiceItems(colorItems, checkedColorItem, new OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_BLUE).commit();
                                        }
                                        else
                                        {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_BLUE).commit();
                                        }
                                        break;

                                    case 1:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_RED).commit();
                                        }
                                        else
                                        {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_RED).commit();
                                        }
                                        break;
                                    case 2:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_ORANGE).commit();
                                        }
                                        else
                                        {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_ORANGE).commit();
                                        }
                                        break;
                                    case 3:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_LILA).commit();
                                        }
                                        else
                                        {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_LILA).commit();
                                        }
                                        break;
                                    case 4:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_GREEN).commit();
                                        }
                                        else
                                        {
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_GREEN).commit();
                                        }
                                        break;
                                    case 5:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                            prefs.edit().putString(COLOR_SCHEME, COLOR_BLACK).commit();
                                        }
                                        else
                                        {
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
                                { String.format(mContext.getResources().getString(R.string.config_temp_celcius)),
                                        String.format(mContext.getResources().getString(R.string.config_temp_fahrenheit)) };
                        int checkedTempItem = 0;
                        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        tempFormat = prefs.getString(TEMP_FORMAT, TEMP_CELSIUS);
                        if (TEMP_CELSIUS.equals(tempFormat))
                        {
                            checkedTempItem = 0;
                        }
                        else if (TEMP_FAHRENHEIT.equals(tempFormat))
                        {
                            checkedTempItem = 1;
                        }
                        final AlertDialog.Builder tempBuilder = new AlertDialog.Builder(mContext);
                        tempBuilder.setTitle(String.format(getResources().getString(R.string.config_colorstyle)));
                        tempBuilder.setSingleChoiceItems(tempItems, checkedTempItem, new OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case 0:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            prefs.edit().putString(TEMP_FORMAT, TEMP_CELSIUS).commit();
                                        }
                                        else
                                        {
                                            prefs.edit().putString(TEMP_FORMAT, TEMP_CELSIUS).commit();
                                        }
                                        break;
                                    case 1:
                                        if (prefs == null)
                                        {
                                            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            prefs.edit().putString(TEMP_FORMAT, TEMP_FAHRENHEIT).commit();
                                        }
                                        else
                                        {
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
                }
            }

        });

        AppRater.app_launched(mContext);

    }

    private String getAppVersionNumber(String packageName, PackageManager packageManager)
    {
        try
        {
            return packageManager.getPackageInfo(packageName, 0).versionName;
        }
        catch (NameNotFoundException e)
        {
            return "?";
        }
    }

    @Override
    protected void onPause()
    {
        updateAppWidget();
        Toast.makeText(mContext, mContext.getResources().getString(R.string.config_save_changes), Toast.LENGTH_LONG).show();
        super.onPause();
    }

    private void updateAppWidget()
    {
        final Context context = ConfigPreferencesActivity.this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.sysinfo_main);
        remoteView.setInt(R.id.btnThird, SET_BACKGROUND_RES, R.drawable.general_btn);
        remoteView.setInt(R.id.btnFourth, SET_BACKGROUND_RES, R.drawable.more_btn);
        remoteView.setInt(R.id.btnFirst, SET_BACKGROUND_RES, R.drawable.display_btn);
        remoteView.setInt(R.id.btnSecond, SET_BACKGROUND_RES, R.drawable.camera_btn);
        remoteView.setInt(R.id.btnFifth, SET_BACKGROUND_RES, R.drawable.memory_btn);
        remoteView.setInt(R.id.btnSixth, SET_BACKGROUND_RES, R.drawable.akku_btn);
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
        remoteView.setViewVisibility(R.id.devicememory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.usbmemory_percent, View.GONE);
        remoteView.setViewVisibility(R.id.devicememory_progressBar, View.GONE);
        remoteView.setViewVisibility(R.id.usbmemory_progressBar, View.GONE);
        remoteView.setViewVisibility(R.id.imgRestore, View.VISIBLE);
        remoteView.setViewVisibility(R.id.txtConfigClick, View.VISIBLE);

        final String colorScheme;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        colorScheme = prefs.getString(ConfigPreferencesActivity.COLOR_SCHEME, ConfigPreferencesActivity.COLOR_BLUE);

        switch (colorScheme)
        {
            case ConfigPreferencesActivity.COLOR_BLUE:
                remoteView.setInt(R.id.relaGeneral, SET_BACKGROUND_RES, R.drawable.rela_background_blue);
                break;
            case ConfigPreferencesActivity.COLOR_RED:
                remoteView.setInt(R.id.relaGeneral, SET_BACKGROUND_RES, R.drawable.rela_background_red);
                break;
            case ConfigPreferencesActivity.COLOR_ORANGE:
                remoteView.setInt(R.id.relaGeneral, SET_BACKGROUND_RES, R.drawable.rela_background_orange);
                break;
            case ConfigPreferencesActivity.COLOR_LILA:
                remoteView.setInt(R.id.relaGeneral, SET_BACKGROUND_RES, R.drawable.rela_background_purple);
                break;
            case ConfigPreferencesActivity.COLOR_GREEN:
                remoteView.setInt(R.id.relaGeneral, SET_BACKGROUND_RES, R.drawable.rela_background_green);
                break;
            case ConfigPreferencesActivity.COLOR_BLACK:
                remoteView.setInt(R.id.relaGeneral, SET_BACKGROUND_RES, R.drawable.rela_background_black);
                break;
        }

        SysInfoMainProvider.updateAppWidget(appWidgetManager, mAppWidgetId, remoteView);
    }

}