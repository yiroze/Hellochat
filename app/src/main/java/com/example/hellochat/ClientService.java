package com.example.hellochat;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.hellochat.Activity.Activity_Chatting;
import com.example.hellochat.DTO.ChatData;
import com.example.hellochat.DTO.MsgInfo;
import com.example.hellochat.DTO.ResultData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientService extends Service {
    private static final String TAG = "ClientService";
    private final static String SERVER_HOST = "3.37.204.197";
    private final static int SERVER_PORT = 5001;
    public Socket mSocket = null;
    SocketIntiThread socketIntiThread;
    private final static int SENDMESSAGE = 1;
    Handler subHandler;
    Handler handler;
    String name;
    static int state;
    static Intent serviceIntent;
    ReadThread readThread;

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
        Log.d(TAG, "onStartCommand: 서브핸들러 " + subHandler);
        if (intent.getStringExtra("msg") != null) {
            if (subHandler != null) {
                Message resultMsg = Message.obtain();
                resultMsg.what = SENDMESSAGE;
                resultMsg.obj = intent.getStringExtra("msg");
                subHandler.sendMessage(resultMsg);
            }
        } else {
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
//            Looper.prepare();
//            Looper.loop();
//            handler = new Handler(Looper.myLooper()) {
//                @Override
//                public void handleMessage(@NonNull Message msg) {
//                    super.handleMessage(msg);
//                    state =Integer.parseInt(msg.obj.toString());
//                    Log.d(TAG, "handleMessage: state상태 = "+state);
//                }
//            };
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

                    if (state_data == send_user_idx) {
                        if(content_type != 3){
                            JSONObject jo = new JSONObject();
                            jo.put("content", "");
                            jo.put("accept_user_idx", send_user_idx);
                            jo.put("content_type", 3);
                            WriteThread writeThread = new WriteThread(socket, jo.toString());
                            writeThread.start();
                        }
                    }
                    Log.d(TAG, "Read: " + accept_msg);
                    Log.d(TAG, "Read: " + user_name);
                    Log.d(TAG, "Read: 채팅화면으로 인텐트보낼때 포그라운드 상태인지 확인 " + isForegroundActivity(ClientService.this, Activity_Chatting.class));
                    //채팅 액티비티가 포그라운드 상태
                    //1. 채팅중인상태이므로 채팅액티비티를 갱신하기위해 인텐트를 보냄
                    //2. 내가 보낸 데이터일경우 리사이클러뷰를 최하단으로 내리기위해 send_result를 넣어준다.
                    //3. 지금 떠있는 채팅액티비티가 메시지가 온 유저와 다를경우 채팅액티비티에서도 알림을 띄워준다.
                    if (content_type == 3) {
                        if (isForegroundActivity(ClientService.this, Activity_Chatting.class)) {
                            Intent intent = new Intent(getApplicationContext(), Activity_Chatting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } else {
                        if (isForegroundActivity(ClientService.this, Activity_Chatting.class)) {
                            Intent intent = new Intent(getApplicationContext(), Activity_Chatting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Log.d(TAG, "Read: 서버에서 받은 idx-" + accept_user_idx + "로그인된 idx-" + getPref());
                            if (!accept_user_idx.equals(getPref())) {
                                Log.d(TAG, "putExtra");
                                intent.putExtra("send_result", true);
                            }
                            startActivity(intent);
                            Log.d(TAG, "Read: state =" + state_data + "send_user_idx" + send_user_idx);
                            if (state_data != send_user_idx) {
                                Log.d(TAG, "Read: 다른 채팅상대에게 온 알림 표시");
                                if (accept_user_idx.equals(getPref())) {
                                    if (content_type == 1) {
                                        createNotificationChannel(user_name, content , send_user_idx);
                                    } else {
                                        createNotificationChannel(user_name, "이미지" ,send_user_idx);
                                    }
                                }
                            }

                        }
                        //채팅액티비티가 포그라운드가 아닌상태
                        else {
                            //받는사람이 나일경우 알림
                            if (accept_user_idx.equals(getPref())) {
                                if (content_type == 1) {
                                    createNotificationChannel(user_name, content ,send_user_idx);
                                } else {
                                    createNotificationChannel(user_name, "이미지" ,send_user_idx);
                                }
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
            Log.d(TAG, "WriteThread:보낼 메시지 " + msg);
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
                dataOutputStream.writeUTF(String.valueOf(jo));
                Log.d(TAG, "WriteThread: 메시지 전송");
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
            subHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    //메시지를 받으면 쓰기쓰레드 시작
                    if (msg.what == SENDMESSAGE) {
                        Log.d(TAG, "handleMessage: 서브쓰레드에서 메시지 받음 , 소켓확인" + socket);
                        WriteThread writeThread = new WriteThread(socket, msg.obj.toString());
                        writeThread.start();
                    } else {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "handleMessage: 서브쓰레드에서 메시지 받음 , 소켓확인" + socket);
                    }
                }
            };
        }

        @Override
        public void run() {
            super.run();
            try {
                Log.d(TAG, "run:소켓생성");
                Log.d(TAG, "run:호스트포트 " + SERVER_PORT);
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                Log.d(TAG, "run:소켓상태 " + socket);
                readThread = new ReadThread(socket);
                readThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Looper.prepare();
            Looper.loop();
            Log.d(TAG, "run: 루퍼도는중");

        }

    }

    private void createNotificationChannel(String name, String description , int send_user_idx) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name_char = "Channel Name";
            String channelId = "channel";
            Log.d(TAG, "createNotificationChannel: " + name_char);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name_char, importance);
            channel.setDescription(description);
            Log.d(TAG, "createNotificationChannel: " + channel);
            Intent notifyIntent = new Intent(this , Activity_Chatting.class);
            notifyIntent.putExtra("user_idx" , send_user_idx);
            notifyIntent.putExtra("name" , name);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this , 1 , notifyIntent , PendingIntent.FLAG_UPDATE_CURRENT);

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

//        Message msg = Message.obtain();
//        msg.what = 2;
//        subHandler.sendMessage(msg);

        Log.d(TAG, "onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: onDestroy: ");
        serviceIntent = null;

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 2);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);

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
        Log.d(TAG, "setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: setAlarmTimer: ");
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 3);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }

}

