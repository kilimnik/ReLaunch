package com.kickflip.myfirstapp.floating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kickflip.myfirstapp.settings.MyActivity;

public class BootStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_REBOOT)) {
            Intent serviceIntent = new Intent(context, Float.class);
            serviceIntent.setAction(MyActivity.STARTFOREGROUND_ACTION);
            context.startService(serviceIntent);
        }
    }
}