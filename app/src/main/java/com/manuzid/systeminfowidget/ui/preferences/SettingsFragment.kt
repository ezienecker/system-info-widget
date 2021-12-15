package com.manuzid.systeminfowidget.ui.preferences

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.manuzid.systeminfowidget.R
import com.manuzid.systeminfowidget.R.array.config_categories
import com.manuzid.systeminfowidget.data.interactors.impl.BatteryCategory.Companion.BATTERY
import com.manuzid.systeminfowidget.data.interactors.impl.CameraCategory.Companion.CAMERA
import com.manuzid.systeminfowidget.data.interactors.impl.DisplayCategory.Companion.DISPLAY
import com.manuzid.systeminfowidget.data.interactors.impl.GeneralInformationCategory.Companion.GENERAL
import com.manuzid.systeminfowidget.data.interactors.impl.MoreInformationCategory.Companion.MORE
import com.manuzid.systeminfowidget.data.interactors.impl.NetworkCategory.Companion.NETWORK
import java.util.*

/**
 * Created by Emanuel Zienecker on 15.11.21.
 * Copyright (c) 2021 Emanuel Zienecker. All rights reserved.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    private val categories = listOf(
        GENERAL,
        MORE,
        DISPLAY,
        CAMERA,
        BATTERY,
        NETWORK
    ).sorted().toTypedArray()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val activityFragment: FragmentActivity = requireActivity()
        val appVersion: String =
            getAppVersion(activityFragment.packageName, activityFragment.packageManager)

        checkCameraAccess(activityFragment.baseContext)

        findPreference<MultiSelectListPreference>("manuzid_category_selection")?.apply {
            setEntries(config_categories)
            entryValues = categories
            setOnPreferenceChangeListener { preference, newValue ->
                checkCategorySelection(activityFragment, preference, newValue)

                false
            }
        }

        findPreference<Preference>("system_info_widget_version")?.apply {
            summary = String.format(
                resources.getString(R.string.config_entries_about_app_summary),
                appVersion
            )
        }

        findPreference<Preference>("rate_app")?.apply {
            intent = Intent(Intent.ACTION_VIEW, getAppLink(activityFragment.applicationContext))
        }

        findPreference<Preference>("recommend")?.apply {
            setOnPreferenceClickListener {
                sendRecommendation()

                false
            }
        }

        findPreference<Preference>("feedback")?.apply {
            setOnPreferenceClickListener {
                sendFeedback(appVersion)

                false
            }
        }

        findPreference<Preference>("market")?.apply {
            intent = Intent(Intent.ACTION_VIEW, getMarketLink())
        }

        findPreference<Preference>("rights_legal")?.apply {
            intent =
                Intent(Intent.ACTION_VIEW, null, activity, RightsLegalActivity::class.java)
        }

        findPreference<Preference>("privacy_policy_settings")?.apply {
            intent =
                Intent(Intent.ACTION_VIEW, null, activity, PrivacyPolicyActivity::class.java)
        }
    }

    private fun checkCategorySelection(
        activityFragment: FragmentActivity,
        preference: Preference?,
        newValue: Any?
    ) {
        if (preference?.key != "manuzid_category_selection") {
            return
        }

        val selectedValues = newValue as HashSet<*>
        if (selectedValues.size != 6) {
            Toast.makeText(
                activityFragment.applicationContext,
                activityFragment.applicationContext.getString(R.string.config_category_choice_wrong),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getAppVersion(packageName: String, packageManager: PackageManager): String =
        try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "?"
        }

    private fun sendFeedback(versionNumber: String) {
        val feedback =
            """
            mailto:manuzidtv@gmail.com?subject=Feedback&body=${resources.getString(R.string.feedback_app_version)} $versionNumber
            ${resources.getString(R.string.feedback_os_version)} ${Build.VERSION.RELEASE}
            ${resources.getString(R.string.feedback_device_name)} ${Build.MODEL}
            ${resources.getString(R.string.feedback_problem_disc)}
            """.trimIndent().replace(" ", "%20")

        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SENDTO, Uri.parse(feedback)),
                resources.getString(R.string.settings_general_friend)
            )
        )
    }

    private fun sendRecommendation() {
        val recommendation =
            """
            mailto:?subject=${resources.getString(R.string.support_subject)}&body=${
                resources.getString(R.string.support_text)
            }
            ${getAppLink(requireActivity())}
            """.trimIndent().replace(" ", "%20")

        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SENDTO, Uri.parse(recommendation)),
                resources.getString(R.string.feedback_mail_subject)
            )
        )
    }

    private fun getAppLink(context: Context): Uri? =
        Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)

    private fun getMarketLink(): Uri? =
        Uri.parse("https://play.google.com/store/apps/developer?id=ManuZiD")

    private fun checkCameraAccess(context: Context) {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                SYSTEM_INFO_CAMERA
            )
        }
    }

    companion object {
        private const val SYSTEM_INFO_CAMERA = 1
    }
}
