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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import java.util.ArrayList;

public class UiSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "UiSettings";

    private static final String KEY_STATUSBAR_TYPE = "status_bar_type";
    private static final String KEY_AUTOHIDE_QUICKNAV = "quicknav_navbar_autohide";
    private static final String KEY_QUICKNAV_PRESS_HIDE = "quicknav_navbar_hide_on_press";
    private static final String KEY_AUTOHIDE_NAVBAR = "sliding_navbar_autohide";
    private static final String KEY_AUTOHIDE_TIMER = "autohide_time";

    private static final String TYPE_SYSTEM_BAR_NORMAL = "system_bar_normal";
    private static final String TYPE_SYSTEM_BAR_SLIDER = "system_bar_slider";
    private static final String TYPE_SYSTEM_BAR_QUICKNAV = "system_bar_quicknav";
    private static final String TYPE_SYSTEM_BAR_QUICKNAV_V2 = "system_bar_quicknav_v2";
    private static final String TYPE_SYSTEM_BAR_PHONE = "phone_statusbar";
    private static final String TYPE_SYSTEM_BAR_PHONE_QUICKNAV = "phone_statusbar_quicknav";

	private ListPreference mStatusbarType;
	private CheckBoxPreference mAutoHide;
	private CheckBoxPreference mQuicknavAutoHide;
	private CheckBoxPreference mQuicknavPressHide;
	private ListPreference mAutoHideTime;

    private final Configuration mCurConfig = new Configuration();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.ui_settings);

        mStatusbarType = (ListPreference) findPreference(KEY_STATUSBAR_TYPE);
        mStatusbarType.setOnPreferenceChangeListener(this);
        String type = mStatusbarType.getValue();
        if (type == null || type.equals(""))
            type = TYPE_SYSTEM_BAR_NORMAL;

       	mAutoHide = (CheckBoxPreference) findPreference(KEY_AUTOHIDE_NAVBAR);
        mAutoHide.setPersistent(false);
		mAutoHide.setChecked(Settings.System.getInt(getContentResolver(),
								Settings.System.NAVIGATION_BAR_AUTOHIDE_SLIDER, 0) == 1);
		mAutoHide.setOnPreferenceChangeListener(this);

        mAutoHideTime = (ListPreference) findPreference(KEY_AUTOHIDE_TIMER);
        mAutoHideTime.setOnPreferenceChangeListener(this);

       	mQuicknavAutoHide = (CheckBoxPreference) findPreference(KEY_AUTOHIDE_QUICKNAV);
        mQuicknavAutoHide.setPersistent(false);
		mQuicknavAutoHide.setChecked(Settings.System.getInt(getContentResolver(),
								Settings.System.NAVIGATION_BAR_AUTOHIDE_QUICKNAV, 0) == 1);
		mQuicknavAutoHide.setOnPreferenceChangeListener(this);

       	mQuicknavPressHide = (CheckBoxPreference) findPreference(KEY_QUICKNAV_PRESS_HIDE);
        mQuicknavPressHide.setPersistent(false);
		mQuicknavPressHide.setChecked(Settings.System.getInt(getContentResolver(),
								Settings.System.NAVIGATION_BAR_QUICKNAV_HIDE_ON_PRESS, 1) == 1);
		mQuicknavPressHide.setOnPreferenceChangeListener(this);

        if (!TYPE_SYSTEM_BAR_SLIDER.equals(type)) {
            getPreferenceScreen().removePreference(mAutoHide);
        }
        if (!TYPE_SYSTEM_BAR_QUICKNAV.equals(type) && !TYPE_SYSTEM_BAR_QUICKNAV_V2.equals(type)
            && !TYPE_SYSTEM_BAR_PHONE_QUICKNAV.equals(type)) {
            getPreferenceScreen().removePreference(mQuicknavAutoHide);
            getPreferenceScreen().removePreference(mQuicknavPressHide);
        }
        if (TYPE_SYSTEM_BAR_NORMAL.equals(type) || TYPE_SYSTEM_BAR_PHONE.equals(type)) {
            getPreferenceScreen().removePreference(mAutoHideTime);
        }
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
		if (KEY_STATUSBAR_TYPE.equals(key)) {
			String value = ((String) objValue);
			Settings.System.putString(getContentResolver(), Settings.System.NAVIGATION_BAR_TYPE,
					value);
			Log.d(TAG, "Statusbar type = " + value);
            if (!TYPE_SYSTEM_BAR_SLIDER.equals(value)) {
                getPreferenceScreen().removePreference(mAutoHide);
            } else {
                getPreferenceScreen().addPreference(mAutoHide);
                getPreferenceScreen().addPreference(mAutoHideTime);
            }
            if (!TYPE_SYSTEM_BAR_QUICKNAV.equals(value) && !TYPE_SYSTEM_BAR_QUICKNAV_V2.equals(value)
                && !TYPE_SYSTEM_BAR_PHONE_QUICKNAV.equals(value)) {
                getPreferenceScreen().removePreference(mQuicknavAutoHide);
                getPreferenceScreen().removePreference(mQuicknavPressHide);
            } else {
                getPreferenceScreen().addPreference(mQuicknavPressHide);
                getPreferenceScreen().addPreference(mQuicknavAutoHide);
                getPreferenceScreen().addPreference(mAutoHideTime);
            }
            if (TYPE_SYSTEM_BAR_NORMAL.equals(value) || TYPE_SYSTEM_BAR_PHONE.equals(value))
                getPreferenceScreen().removePreference(mAutoHideTime);

		}

        if (KEY_QUICKNAV_PRESS_HIDE.equals(key)) {
			boolean value = ((Boolean) objValue.equals(Boolean.TRUE));
			Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_QUICKNAV_HIDE_ON_PRESS,
					value ? 1 : 0);
        }

        if (KEY_AUTOHIDE_QUICKNAV.equals(key)) {
			boolean value = ((Boolean) objValue.equals(Boolean.TRUE));
			Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_AUTOHIDE_QUICKNAV,
					value ? 1 : 0);
            //mAutoHideTime.setEnabled(value);
        }

        if (KEY_AUTOHIDE_NAVBAR.equals(key)) {
			boolean value = ((Boolean) objValue.equals(Boolean.TRUE));
			Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_AUTOHIDE_SLIDER,
					value ? 1 : 0);
            //mAutoHideTime.setEnabled(value);
        }

        if (KEY_AUTOHIDE_TIMER.equals(key)) {
            int value = Integer.parseInt((String) objValue);
			Settings.System.putInt(getContentResolver(), 
                Settings.System.NAVIGATION_BAR_AUTOHIDE_TIME, value);
        }

        return true;
    }
}
