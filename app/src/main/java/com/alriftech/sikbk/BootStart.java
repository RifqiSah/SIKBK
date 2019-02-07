package com.alriftech.sikbk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, InternetService.class);
        context.startActivity(i);

        Log.d("SikbkLog", "InternetService started from boot!");
    }
}
