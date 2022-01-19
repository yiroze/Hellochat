package com.example.hellochat.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.hellochat.MainActivity;
import com.example.hellochat.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RestartService extends Service {
    private static final String TAG = "RestartService";

    public RestartService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(null);
        builder.setContentText(null);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_NONE));
        }

        Notification notification = builder.build();
        startForeground(9, notification);

        /////////////////////////////////////////////////////////////////////
        JSONObject jo = new JSONObject();
        try {
            jo.put("user_idx" , Integer.parseInt(getPref()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent in = new Intent(this, ClientService.class);
        in.putExtra("reconnection" , jo.toString());
        startService(in);
        stopForeground(true);
        stopSelf();
        return START_NOT_STICKY;
    }
    public String getPref() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
