package com.derpcaf.derpzone.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.derpcaf.derpzone.preferences.CustomSeekBarPreference;
import com.derpcaf.derpzone.preferences.SystemSettingSwitchPreference;
import com.android.settings.Utils;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class NetworkTraffic extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private CustomSeekBarPreference mThreshold;
    private ListPreference mNetTrafficType;
    private SystemSettingSwitchPreference mNetMonitor;
    private SystemSettingSwitchPreference mNetMonitorSB;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.x_network_traffic);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        boolean isNetMonitorEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT) == 1;
        mNetMonitor = (SystemSettingSwitchPreference) findPreference("network_traffic_state");
        mNetMonitor.setChecked(isNetMonitorEnabled);
        mNetMonitor.setOnPreferenceChangeListener(this);

	boolean isNetMonitorSBEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE_SB, 0, UserHandle.USER_CURRENT) == 1;
        mNetMonitorSB = (SystemSettingSwitchPreference) findPreference("network_traffic_state_sb");
        mNetMonitorSB.setChecked(isNetMonitorSBEnabled);
        mNetMonitorSB.setOnPreferenceChangeListener(this);

        int value = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold = (CustomSeekBarPreference) findPreference("network_traffic_autohide_threshold");
        mThreshold.setValue(value);
        mThreshold.setOnPreferenceChangeListener(this);
	mThreshold.setEnabled(
	    (isNetMonitorEnabled || isNetMonitorSBEnabled) ? true : false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
	final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetMonitor) {
            boolean value = (Boolean) objValue;
	    boolean netmonitorSBState = Settings.System.getIntForUser(resolver,
	            Settings.System.NETWORK_TRAFFIC_STATE_SB, 0, UserHandle.USER_CURRENT) == 1;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mNetMonitor.setChecked(value);
	    mThreshold.setEnabled((value == true || netmonitorSBState) ? true : false);
            return true;
	} else if (preference == mNetMonitorSB) {
	    boolean value = (Boolean) objValue;
	    boolean netmonitorState = Settings.System.getIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT) == 1;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE_SB, value ? 0 : 0,
                    UserHandle.USER_CURRENT);
            mNetMonitorSB.setChecked(value);
	    mThreshold.setEnabled((value == true || netmonitorState) ? true : false);
            return true;
        } else if (preference == mThreshold) {
            int val = (Integer) objValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DERPZONE;
    }
}

