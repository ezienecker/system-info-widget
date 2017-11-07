package com.manuzid.systeminfowidget.preferences;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.kobakei.ratethisapp.RateThisApp;
import com.manuzid.systeminfowidget.R;
import com.manuzid.systeminfowidget.SystemInfoMainProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.manuzid.systeminfowidget.R.array.config_categories;
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
public class ConfigPreferencesActivity extends PreferenceActivity {
    // Camera Constant Permission
    private static final int SYSTEM_INFO_CAMERA = 1;

    // Keys für die Auswahlen
    public static final String CATEGORY_SELECTION = "manuzid-category-selection";
    public static final String TEMP_FORMAT = "temp_format";
    public static final String COLOR_SCHEME = "color_scheme";

    // Values für die Farbauswahl innerhalb der Preferences, neue werden hier und in der array.xml
    // hinzugefügt.
    public static final String COLOR_BLUE = "color_blue";
    public static final String COLOR_RED = "color_red";
    public static final String COLOR_LILA = "color_lila";
    public static final String COLOR_ORANGE = "color_orange";
    public static final String COLOR_GREEN = "color_green";
    public static final String COLOR_BLACK = "color_black";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
            Preference.OnPreferenceChangeListener {
        private static final String TAG = PrefsFragment.class.getSimpleName();

        private Context mContext;
        private Preference sendReport;
        private Preference tellFriend;
        private MultiSelectListPreference multiSelectListPreference;
        private String mVersionNumber;

        final Set<String> categorySelection = new LinkedHashSet<>(Arrays.asList(GENERAL, MORE, DISPLAY, CAMERA, MEMORY, BATTERY, NETWORK));
        final List<String> listCategorySelection = new ArrayList<>(categorySelection);

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            mContext = getActivity().getBaseContext();
            Preference bowVersionSum = findPreference(getResources().getString(R.string.settings_key_sys_version));
            Collections.sort(listCategorySelection);
            CharSequence[] charCategorySelection = listCategorySelection.toArray(new CharSequence[listCategorySelection.size()]);

            mVersionNumber = getAppVersionNumber(mContext.getPackageName(), mContext.getPackageManager());

            try {
                bowVersionSum.setSummary(String.format(getResources().getString(R.string.config_entries_about_app_summary), mVersionNumber));
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                bowVersionSum.setSummary(getResources().getString(R.string.config_entries_about_app_summary_fail));
            }

            Preference rateLink = findPreference(getResources().getString(R.string.settings_key_rate_bow));
            rateLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));

            sendReport = findPreference(getResources().getString(R.string.settings_key_feedback));
            sendReport.setOnPreferenceClickListener(this);

            Preference marketLink = findPreference(getResources().getString(R.string.settings_key_market));
            marketLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=ManuZiD")));

            tellFriend = findPreference(getResources().getString(R.string.settings_key_forward));
            tellFriend.setOnPreferenceClickListener(this);

            Preference rightsLegal = findPreference(getResources().getString(R.string.settings_key_rights));
            rightsLegal.setIntent(new Intent(Intent.ACTION_VIEW, null, mContext, RightsLegalActivity.class));

            Preference privacyPolicySettings = findPreference("privacy_policy_settings");
            privacyPolicySettings.setIntent(new Intent(Intent.ACTION_VIEW, null, mContext, PrivacyPolicyActivity.class));

            // MultiSelect für die Kategorien
            multiSelectListPreference = (MultiSelectListPreference) findPreference(CATEGORY_SELECTION);
            multiSelectListPreference.setEntries(config_categories);
            multiSelectListPreference.setEntryValues(charCategorySelection);

            if (multiSelectListPreference.getValues().isEmpty())
                multiSelectListPreference.setValues(new LinkedHashSet<>(Arrays.asList(GENERAL, MORE, DISPLAY, CAMERA, MEMORY, BATTERY)));

            multiSelectListPreference.setOnPreferenceChangeListener(this);
            multiSelectListPreference.setOnPreferenceClickListener(this);

            int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.CAMERA);

            if (permissionCheck == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CAMERA}, SYSTEM_INFO_CAMERA);

            // Custom strings
            RateThisApp.Config config = new RateThisApp.Config();
            config.setTitle(R.string.config_general_rate);
            config.setMessage(R.string.rate_dialog_text);
            config.setYesButtonText(R.string.rate_dialog_ok);
            config.setNoButtonText(R.string.rate_dialog_no_thanks);
            config.setCancelButtonText(R.string.rate_dialog_remind_later);
            RateThisApp.init(config);
            // Monitor launch times and interval from installation
            RateThisApp.onCreate(this.getActivity());
            // If the condition is satisfied, "Rate this app" dialog will be shown
            RateThisApp.showRateDialogIfNeeded(this.getActivity());
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.equals(tellFriend)) {
                String tellFriendStr = "mailto:" +
                        "" +
                        "?subject=" +
                        getResources().getString(R.string.support_subject) +
                        "&body=" +
                        getResources().getString(R.string.support_text) +
                        "\n" +
                        "http://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
                String uriString = tellFriendStr.replace(" ", "%20");

                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)),
                        getResources().getString(R.string.config_general_friend)));
            } else if (preference.equals(sendReport)) {
                String sendReport = "mailto:" +
                        "manuzidtv@gmail.com" +
                        "?subject=" +
                        "Feedback" +
                        "&body=" +
                        getResources().getString(R.string.feedback_app_version) + " " + mVersionNumber +
                        "\n" +
                        getResources().getString(R.string.feedback_os_version) + " " + android.os.Build.VERSION.RELEASE +
                        "\n" +
                        getResources().getString(R.string.feedback_device_name) + " " + android.os.Build.MODEL +
                        "\n" +
                        getResources().getString(R.string.feedback_problem_disc);
                String uriString = sendReport.replace(" ", "%20");

                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)),
                        getResources().getString(R.string.feedback_mail_subject)));
            }

            return false;
        }

        private String getAppVersionNumber(String packageName, PackageManager packageManager) {
            try {
                return packageManager.getPackageInfo(packageName, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, e.getLocalizedMessage());
                return "?";
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(CATEGORY_SELECTION)) {
                @SuppressWarnings("unchecked")
                HashSet<String> selectedValues = (HashSet<String>) newValue;

                if (selectedValues.size() == 6) {
                    return true;
                }

                Toast.makeText(mContext, mContext.getString(R.string.config_category_choice_wrong), Toast.LENGTH_SHORT).show();

            }

            return false;
        }
    }

    @Override
    protected void onPause() {
        updateAppWidget();
        Toast.makeText(this, this.getResources().getString(R.string.config_save_changes), Toast.LENGTH_LONG).show();
        super.onPause();
    }

    private void updateAppWidget() {
        Intent intent = new Intent(this, SystemInfoMainProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);
    }

}