 package com.example.hellochat.Service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hellochat.Activity.Chatting.Activity_Chatting;
import com.example.hellochat.Activity.Chatting.Activity_Receive;
import com.example.hellochat.Activity.Feed.Activity_Detail;
import com.example.hellochat.MainActivity;
import com.example.hellochat.R;
import com.example.hellochat.Recever.AlarmReceiver;
import com.example.hellochat.webRTC.CallActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClientService extends Service {
    private static final String TAG = "ClientService";
    private final static String SERVER_HOST = "3.37.204.197";
    private final static int SERVER_PORT = 5001;
    private final static int SENDMESSAGE = 1;
    Handler subHandler;
    Handler handler;
    public static Intent serviceIntent = null;
    ReadThread readThread;
    private int notificationId;
    private Thread mainThread;

    public ClientService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        new SocketIntiThread().start();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ??????????????? " + subHandler);
        if (intent.getStringExtra("msg") != null) {
            if(serviceIntent !=null){
                serviceIntent = intent;
            }
            //???????????? ????????? ????????? ?????????
            if (subHandler != null) {
                Message resultMsg = Message.obtain();
                resultMsg.what = SENDMESSAGE;
                resultMsg.obj = intent.getStringExtra("msg");
                subHandler.sendMessage(resultMsg);
            }
        }//RestartService??????
        else if(intent.getStringExtra("reconnection") != null){
            if(serviceIntent !=null){
                serviceIntent = intent;
            }
            mainThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (subHandler != null) {
                        showToast(getApplication(), "Start Service");
                        Message resultMsg = Message.obtain();
                        resultMsg.what = SENDMESSAGE;
                        resultMsg.obj = intent.getStringExtra("reconnection");
                        subHandler.sendMessage(resultMsg);
                    }
                }
            });
            mainThread.start();
        }
        else {
            if (intent.getIntExtra("user_idx", -1) != -1) {
                Message resultMsg = Message.obtain();
                resultMsg.obj = intent.getIntExtra("user_idx", -1);
                handler.sendMessage(resultMsg);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    class ReadThread extends Thread {
        DataInputStream dataInputStream;
        int state_data;
        Socket socket;

        public ReadThread(Socket mSocket) {
            socket = mSocket;
            Log.d(TAG, "ReadThread: " + mSocket);
            try {
                dataInputStream = new DataInputStream(mSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            HandlerThread handlerThread = new HandlerThread("HandlerName");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    state_data = Integer.parseInt(msg.obj.toString());
                    Log.d(TAG, "handleMessage: " + state_data);
                }
            };
        }

        @Override
        public void run() {
            super.run();
            Read(dataInputStream);

        }

        public void Read(DataInputStream dataInputStream) {
            while (dataInputStream != null) {
                try {
                    String accept_msg = dataInputStream.readUTF();
                    JSONObject jsonObject = new JSONObject(accept_msg);
                    String accept_user_idx = jsonObject.getString("accept_user_idx");
                    int send_user_idx = jsonObject.getInt("user_idx");
                    String content = jsonObject.getString("content");
                    String user_name = jsonObject.getString("name");
                    int content_type = jsonObject.getInt("content_type");
                    String profile = null;
                    if (content_type == 9 || content_type == 11 || content_type == 12) {
                        profile = jsonObject.getString("profile");
                    }
                    if (state_data == send_user_idx) {
                        if (content_type != 3) {
                            JSONObject jo = new JSONObject();
                            jo.put("content", "");
                            jo.put("accept_user_idx", send_user_idx);
                            jo.put("content_type", 3);
                            WriteThread writeThread = new WriteThread(socket, jo.toString());
                            writeThread.start();
                        }
                    }
                    Log.d(TAG, "Read: " + accept_msg);
                    Log.d(TAG, "Read: ?????????????????? ?????????????????? ??????????????? ???????????? ?????? " + isForegroundActivity(ClientService.this, Activity_Chatting.class));
                    //?????? ??????????????? ??????????????? ??????
                    //1. ??????????????????????????? ????????????????????? ?????????????????? ???????????? ??????
                    //2. ?????? ?????? ?????????????????? ????????????????????? ??????????????? ??????????????? send_result??? ????????????.
                    //3. ?????? ????????? ????????????????????? ???????????? ??? ????????? ???????????? ??????????????????????????? ????????? ????????????.
                    if (content_type == 3) {
                        if (isForegroundActivity(ClientService.this, Activity_Chatting.class)) {
                            Intent intent = new Intent(getApplicationContext(), Activity_Chatting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } else if (content_type == 4) {
                        Intent intent = new Intent(ClientService.this, Activity_Receive.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("RoomID", content);
                        intent.putExtra("send_user_idx", send_user_idx);
                        startActivity(intent);
                    } else if (content_type == 5) {
                        Intent intent = new Intent(ClientService.this, CallActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("refuse", true);
                        startActivity(intent);
                    } else if (content_type == 6) {
                        Intent intent = new Intent(ClientService.this, Activity_Receive.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("cancel", true);
                        startActivity(intent);
                    } else if (content_type == 7) {
                        if (isForegroundActivity(ClientService.this, CallActivity.class)) {
                            Intent intent = new Intent(ClientService.this, CallActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("start", true);
                            startActivity(intent);
                        }
                    } else if (content_type == 8) {
                        if (isForegroundActivity(ClientService.this, Activity_Chatting.class)) {
                            Intent intent = new Intent(ClientService.this, Activity_Chatting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } else if (content_type == 9) {
                        //???????????? ??? ?????????
                        //?????? ???
                        if (isForegroundActivity(ClientService.this, MainActivity.class)) {
                            Intent intent = new Intent(ClientService.this, MainActivity.class);
                            intent.putExtra("initFeed", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        feedNotification(user_name, "?????? ????????? ?????? ?????????????????????", Integer.parseInt(content), profile);
                    } else if (content_type == 10) {
                        //??? ????????? ?????????
                        //?????? ???
                        if (isForegroundActivity(ClientService.this, MainActivity.class)) {
                            Intent intent = new Intent(ClientService.this, MainActivity.class);
                            intent.putExtra("initFeed", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } else if (content_type == 11) {
                        //????????? ?????? ??????
                        //?????? ???
                        if (isForegroundActivity(ClientService.this, MainActivity.class)) {
                            Intent intent = new Intent(ClientService.this, MainActivity.class);
                            intent.putExtra("initFeed", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        feedNotification(user_name, "?????? ????????????????????????", Integer.parseInt(content), profile);
                    } else if (content_type == 12) {
                        //????????? ?????? ??????
                        //?????? ???
                        if (isForegroundActivity(ClientService.this, MainActivity.class)) {
                            Intent intent = new Intent(ClientService.this, MainActivity.class);
                            intent.putExtra("initFeed", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        feedNotification(user_name, "?????? ???????????? ?????????????????????", Integer.parseInt(content), profile);
                    } else {
                        if (isForegroundActivity(ClientService.this, Activity_Chatting.class)) {
                            Intent intent = new Intent(getApplicationContext(), Activity_Chatting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Log.d(TAG, "Read: ???????????? ?????? idx-" + accept_user_idx + "???????????? idx-" + getPref());
                            if (!accept_user_idx.equals(getPref())) {
                                Log.d(TAG, "putExtra");
                                intent.putExtra("send_result", true);
                            }
                            startActivity(intent);
                            Log.d(TAG, "Read: state =" + state_data + "send_user_idx" + send_user_idx);
                            if (state_data != send_user_idx) {
                                Log.d(TAG, "Read: ?????? ?????????????????? ??? ?????? ??????");
                                if (accept_user_idx.equals(getPref())) {
                                    if (content_type == 1) {
                                        createNotificationChannel(user_name, content, send_user_idx);
                                    } else {
                                        createNotificationChannel(user_name, "?????????", send_user_idx);
                                    }
                                }
                            }

                        }
                        //????????????????????? ?????????????????? ????????????
                        else {
                            //??????????????? ???????????? ??????
                            if (accept_user_idx.equals(getPref())) {
                                if (content_type == 1) {
                                    createNotificationChannel(user_name, content, send_user_idx);
                                } else {
                                    createNotificationChannel(user_name, "?????????", send_user_idx);
                                }
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("initChat", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class WriteThread extends Thread {
        DataOutputStream dataOutputStream;
        String Content;
        int accept_user_idx, content_type;
        Socket mSocket;

        public WriteThread(Socket socket, String msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg);
                Content = jsonObject.getString("content");
                accept_user_idx = jsonObject.getInt("accept_user_idx");
                content_type = jsonObject.getInt("content_type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket = socket;
            Log.d(TAG, "WriteThread:?????? ????????? " + msg);
        }

        @Override
        public void run() {
            super.run();
            try {
                Log.d(TAG, "run: " + mSocket);
                dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
                SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
                int MY_IDX = Integer.parseInt(pref.getString("Login_data", ""));
                JSONObject jo = new JSONObject();
                jo.put("user_idx", MY_IDX);
                jo.put("content", Content);
                jo.put("accept_user_idx", accept_user_idx);
                jo.put("content_type", content_type);
//                dataOutputStream.writeUTF(String.valueOf(jo));
                dataOutputStream.writeUTF(jo.toString());
                Log.d(TAG, "WriteThread: ????????? ??????" + jo + mSocket);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    class SocketIntiThread extends Thread {
        Socket socket;
        String Content;

        public SocketIntiThread() {
            Log.d(TAG, "SocketIntiThread: ??????!");
            subHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    //???????????? ????????? ??????????????? ??????
                    if (msg.what == SENDMESSAGE) {
                        Log.d(TAG, "handleMessage: ????????????????????? ????????? ?????? , ????????????" + socket);
                        Log.d(TAG, "handleMessage: ????????????????????? ????????? ?????? , ?????????" + msg.obj.toString());
                        WriteThread writeThread = new WriteThread(socket, msg.obj.toString());
                        writeThread.start();
                    } else {
//                        try {
//                            socket.close();
//                            Log.d(TAG, "handleMessage:????????????" + socket);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            };
        }

        @Override
        public void run() {
            super.run();

            try {
                Log.d(TAG, "run:????????????");
                Log.d(TAG, "run:??????????????? " + SERVER_PORT);
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                Log.d(TAG, "run:???????????? " + socket);
                readThread = new ReadThread(socket);
                readThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Looper.prepare();
            Looper.loop();
            Log.d(TAG, "run: ???????????????");
        }

    }

    private void createNotificationChannel(String name, String description, int send_user_idx) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name_char = "Channel Name";
            String channelId = "channel";
            Log.d(TAG, "createNotificationChannel: " + name_char);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name_char, importance);
            channel.setDescription(description);
            Log.d(TAG, "createNotificationChannel: " + channel);
            Intent notifyIntent = new Intent(this, Activity_Chatting.class);
            notifyIntent.putExtra("user_idx", send_user_idx);
            notifyIntent.putExtra("name", name);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder nofityBuilder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(name)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(notifyPendingIntent)
                    .setAutoCancel(true);
            notificationManager.notify(1, nofityBuilder.build());
        }
    }

    private void feedNotification(String name, String description, int feed_idx, String profile) {
        Log.d(TAG, String.format("feedNotification-> name :%s description:%s feed_idx:%d profile:%s", name, description, feed_idx, profile));
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            contentView = new RemoteViews(getPackageName() , R.layout.notification_custom);
//            contentView.setTextViewText(R.id.target_name,name);
//            contentView.setTextViewText(R.id.message ,description);
            String imageUrl = "http://3.37.204.197/hellochat/" + profile;
            CharSequence name_char = "Channel Name";
            String channelId = "channel";
            Log.d(TAG, "createNotificationChannel: " + name_char);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name_char, importance);
            channel.setDescription(description);
            Log.d(TAG, "createNotificationChannel: " + channel);
            Intent notifyIntent = new Intent(this, Activity_Detail.class);
            notifyIntent.putExtra("feed_idx", feed_idx);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 1, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationManager.createNotificationChannel(channel);


            NotificationCompat.Builder nofityBuilder;
            nofityBuilder = new NotificationCompat.Builder(this, channelId)
//                    .setContent(contentView)
                    .setContentTitle(name)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(notifyPendingIntent)
                    .setAutoCancel(true);
            final Bitmap[] bm = new Bitmap[1];
            if (!profile.equals("")) {
                Glide.with(getApplicationContext()).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        bm[0] = resource;
                    }
                });
                Log.d(TAG, "feedNotification: " + bm[0]);
                nofityBuilder.setLargeIcon(bm[0]);
            } else {
                nofityBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.no_profile));
            }


            notificationId = (int) System.currentTimeMillis();

            notificationManager.notify(notificationId, nofityBuilder.build());
        }
    }

    public static boolean isForegroundActivity(Context context, Class<?> cls) {
        if (cls == null)
            return false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo running = info.get(0);
        ComponentName componentName = running.topActivity;

        return cls.getName().equals(componentName.getClassName());
    }

    public String getPref() {
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceIntent = null;
        setAlarmTimer();

        Thread.currentThread().interrupt();
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        setAlarmTimer();
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }
    public void showToast(final Application application, final String msg) {
        Handler h = new Handler(application.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(application, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";//getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)//drawable.splash)
                        .setContentTitle("Service test")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,"Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

