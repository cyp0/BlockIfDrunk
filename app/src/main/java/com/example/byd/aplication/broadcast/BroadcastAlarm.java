package com.example.byd.aplication.broadcast;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


@SuppressWarnings("deprecation")
public class BroadcastAlarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String topPackageName = null;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            topPackageName = foregroundTaskInfo.topActivity.getPackageName();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        }else{
            UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    topPackageName="None";
                }
                topPackageName =  runningTask.get(runningTask.lastKey()).getPackageName();
            }
        }



        if(topPackageName.equals("com.android.contacts")){
            Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
            startHomescreen.addCategory(Intent.CATEGORY_HOME);
            startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startHomescreen);
            Toast.makeText(context, "App esta bloqueada", Toast.LENGTH_SHORT).show();
        }

//        Intent intent1 = new Intent(context, BackgroundService.class);
//        context.startService(intent1);

        System.out.println(topPackageName);
        Log.e("Task List", "Current App in foreground is: " + topPackageName);
    }


}
