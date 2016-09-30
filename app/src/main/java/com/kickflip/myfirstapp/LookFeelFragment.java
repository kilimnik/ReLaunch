package com.kickflip.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.pavelsikun.seekbarpreference.SeekBarPreference;

public class LookFeelFragment extends PreferenceFragment {

    public static final String LOOK_FEEL_ACTION = "com.kickflip.myfirstapp.look_feel";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.look_feel);

        Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Intent intent = new Intent(LOOK_FEEL_ACTION);
                intent.putExtra("key", preference.getKey());

                if (newValue instanceof Boolean) {
                    intent.putExtra("value", (boolean) newValue);
                }else {
                    intent.putExtra("value", (int) newValue);
                }

                getActivity().sendBroadcast(intent);

                return true;
            }
        };

        findPreference("color_picker").setOnPreferenceChangeListener(changeListener);
        findPreference("switch_show").setOnPreferenceChangeListener(changeListener);
        findPreference("slider_width").setOnPreferenceChangeListener(changeListener);
    }
}
