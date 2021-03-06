package com.example.android.sunshine.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {
    // PreferenceActivity는 settings(환경설정, preference)의 내용을 담고있는 xml file의
    // display를 담당
    // http://blog.naver.com/2hyoin/220471574820 설명 참조
   /* PreferenceActivity를 이용해 Preference를 구현하기 위한 두가지 단계
    - PreferenceActivity를 상속하는 커스텀 클래스 정의
    - onCreate()에 내부에서 Preference 계층 구조를 가져오는 메서드 호출
    [출처] [Android 사용법] Preference 사용법|작성자 사자머리님
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        // TODO: Add preferences from XML
        addPreferencesFromResource(R.xml.pref_general);
        // even though this method was deprecated, it works fine. No problem!
        // public void addPreferenceFromResource(int prederencedResId)
        // this method was deprecated in API level 11.
        // This function is not relevant for a modern fragment-based PreferenceActivity.
        // inflates the given Xml resource.
        // PreferenceActivity - PreferenceFragment로 바뀌었음

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        // TODO: Add preferences
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
        // 뒷 부분에 정의된 새로운 method
        // public Preference findPreference(CharSequence key)
        // finds a Preference based on its key.
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
        // public void setOnPreferenceChangeListner(Preference.OnPreferenceChangeListner onPreferenceChangeListner)
        // sets the callback to be invoked when this Preference is changed by the user.

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
        // public abstract boolean onPreferenceChange(Preference preference, Object newValue)
        // called when a Preference has been changed by the user.
        // This is called before the state of the Preference is about to be updated.
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
            // public void setSummary(CharSequence summary)
        }
        return true;
    }

}