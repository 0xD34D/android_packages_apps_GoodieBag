/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.goodiebag;

import android.app.ActivityManagerNative;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import java.util.ArrayList;

public class BatterySettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "BatterySettings";

    private static final String KEY_SHOW_BATTERY_BAR = "show_battery_bar";
    private static final String KEY_SHOW_BATTERY_PERCENTAGE = "show_battery_percentage";

	private CheckBoxPreference mBatteryBar;
	private CheckBoxPreference mBatteryPercentage;

    private final Configuration mCurConfig = new Configuration();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.battery_settings);

       	mBatteryBar = (CheckBoxPreference) findPreference(KEY_SHOW_BATTERY_BAR);
        mBatteryBar.setPersistent(false);
		mBatteryBar.setChecked(Settings.System.getInt(getContentResolver(),
								Settings.System.SHOW_BATTERY_BAR, 0) == 1);
		mBatteryBar.setOnPreferenceChangeListener(this);

       	mBatteryPercentage = (CheckBoxPreference) findPreference(KEY_SHOW_BATTERY_PERCENTAGE);
        mBatteryPercentage.setPersistent(false);
		mBatteryPercentage.setChecked(Settings.System.getInt(getContentResolver(),
								Settings.System.SHOW_BATTERY_PERCENTAGE, 0) == 1);
		mBatteryPercentage.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (KEY_SHOW_BATTERY_BAR.equals(key)) {
			boolean value = ((Boolean) objValue.equals(Boolean.TRUE));
			Settings.System.putInt(getContentResolver(), Settings.System.SHOW_BATTERY_BAR,
					value ? 1 : 0);
        }

        if (KEY_SHOW_BATTERY_PERCENTAGE.equals(key)) {
			boolean value = ((Boolean) objValue.equals(Boolean.TRUE));
			Settings.System.putInt(getContentResolver(), Settings.System.SHOW_BATTERY_PERCENTAGE,
					value ? 1 : 0);
        }

        return true;
    }
}
