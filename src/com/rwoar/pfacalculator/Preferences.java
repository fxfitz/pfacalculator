package com.rwoar.pfacalculator;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private ListPreference mListPreference;
	private ListPreference altitudeAdjustment;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		mListPreference = (ListPreference) getPreferenceScreen().findPreference("aerobicCompPref");
		altitudeAdjustment = (ListPreference) getPreferenceScreen().findPreference("altitudePref");
		// lp.setSummary(lp.getEntry());
	}
	
	protected void onResume(){
		super.onResume();
		
		mListPreference.setSummary(mListPreference.getEntry());
		altitudeAdjustment.setSummary(altitudeAdjustment.getEntry());
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	protected void onPause() {
		super.onPause();
		
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		mListPreference.setSummary(mListPreference.getEntry());
		altitudeAdjustment.setSummary(altitudeAdjustment.getEntry());
	}

}
