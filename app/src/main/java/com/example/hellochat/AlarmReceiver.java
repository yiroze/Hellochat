package com.example.hellochat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.hellochat.Service.ClientService;
import com.example.hellochat.Service.RestartService;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: onReceive: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, RestartService.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, ClientService.class);
            context.startService(in);
        }
    }
}
