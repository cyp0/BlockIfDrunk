package com.example.byd.aplication.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.example.byd.aplication.broadcast.BroadcastAlarm;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private static BroadcastReceiver m_ScreenOffReceiver;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                final Handler h = new Handler();
                final int delay = 5000; //milliseconds

                h.postDelayed(new Runnable() {
                    public void run() {
                        //do something
                        Intent alarmIntent = new Intent(getApplicationContext(), BroadcastAlarm.class);
                        sendBroadcast(alarmIntent);

                        h.postDelayed(this, delay);
                    }
                }, delay);
//                registerScreenOffReceiver();
//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                handler.postDelayed(runnable, 3000);
            }
        };

        handler.postDelayed(runnable, 3000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    private void startJob() {
//        Toast.makeText(context, "HELLO", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
//        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        super.onDestroy();

    }

    private void registerScreenOffReceiver() {
        m_ScreenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String topPackageName = null;
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    topPackageName = foregroundTaskInfo.topActivity.getPackageName();
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                } else {
                    UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                    long time = System.currentTimeMillis();
                    List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                    if (stats != null) {
                        SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                        for (UsageStats usageStats : stats) {
                            runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                        }
                        if (runningTask.isEmpty()) {
                            topPackageName = "None";
                        }
                        topPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
                    }
                }


                if (topPackageName.equals("com.android.contacts")) {
                    Toast.makeText(context, "Contactos", Toast.LENGTH_SHORT).show();
                    Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
                    startHomescreen.addCategory(Intent.CATEGORY_HOME);
                    startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(startHomescreen);
                }

//        Intent intent1 = new Intent(context, BackgroundService.class);
//        context.startService(intent1);

                System.out.println(topPackageName);
                Log.e("Task List", "Current App in foreground is: " + topPackageName);
            }
        };
    }

//    @Override
//    public void onStart(Intent intent, int startid) {
//        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
//    }
}

//package com.applocker.service;
//
//        import android.app.Service;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.os.Handler;
//        import android.os.IBinder;
//        import android.widget.Toast;
//
//        import androidx.annotation.Nullable;
//
//public class BackgroundService extends Service {
//
//    public Context context = this;
//    public Handler handler = null;
//    public static Runnable runnable = null;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
//
//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
//                handler.postDelayed(runnable, 3000);
//            }
//        };
//
//        handler.postDelayed(runnable, 6000);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
//        //handler.removeCallbacks(runnable);
//        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onStart(Intent intent, int startid) {
//        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
//    }
//}
