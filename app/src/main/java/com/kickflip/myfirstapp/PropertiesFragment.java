package com.kickflip.myfirstapp;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

public class PropertiesFragment extends PreferenceFragment{

    public static final String PROPERTIES_ACTION = "com.kickflip.myfirstapp.prop";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.properties);

        Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Intent intent = new Intent(PROPERTIES_ACTION);
                intent.putExtra("key", preference.getKey());

                if (newValue instanceof Boolean) {
                    intent.putExtra("value", (boolean) newValue);
                }else {
                    intent.putExtra("value", (String) newValue);
                }

                getActivity().sendBroadcast(intent);

                return true;
            }
        };

        findPreference("switch_enable").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Intent intent = new Intent(getActivity(), Float.class);

                if ((boolean) newValue) {
                    intent.setAction(MyActivity.STARTFOREGROUND_ACTION);
                    intent.putExtra("switch_notification", ((SwitchPreference) findPreference("switch_notification")).isChecked());
                    intent.putExtra("switch_haptic", ((SwitchPreference) findPreference("switch_haptic")).isChecked());
                    intent.putExtra("list_delay", Integer.valueOf(((ListPreference) findPreference("list_delay")).getValue()));

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    intent.putExtra("switch_show", preferences.getBoolean("switch_show", false));

                }else {
                    intent.setAction(MyActivity.STOPFOREGROUND_ACTION);
                }

                getActivity().startService(intent);

                return true;
            }
        });

        findPreference("list_icon_size").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Intent intent = new Intent(PROPERTIES_ACTION + ".icon_size");

                intent.putExtra("value", Integer.parseInt((String) newValue));
                getActivity().sendBroadcast(intent);

                return true;
            }
        });

        //findPreference("switch_start_boot").setOnPreferenceChangeListener(changeListener);
        findPreference("switch_notification").setOnPreferenceChangeListener(changeListener);
        findPreference("switch_haptic").setOnPreferenceChangeListener(changeListener);
        findPreference("switch_snap").setOnPreferenceChangeListener(changeListener);
        findPreference("list_orientation").setOnPreferenceChangeListener(changeListener);
        findPreference("list_delay").setOnPreferenceChangeListener(changeListener);

    }
}
