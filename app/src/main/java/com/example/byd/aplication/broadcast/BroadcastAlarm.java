package com.example.byd.aplication.broadcast;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.byd.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;


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


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

        Bundle extras = intent.getExtras();
        String dateBlock = (String) extras.get("Date");
        Date dateOfBlock = null;
        Date dateNow = null;
        String now = dateFormat.format(new Date());

        try {
            dateOfBlock = dateFormat.parse(dateBlock);
            dateNow = dateFormat.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean hasDatePassed = false;
        if (dateNow.compareTo(dateOfBlock) < 0) {
            hasDatePassed = false;
        } else if (dateNow.compareTo(dateOfBlock) > 0 || dateNow.compareTo(dateOfBlock) == 0) {
            hasDatePassed = true;
        }


//|| topPackageName.equals("com.android.dialer")
//        com.facebook.katana
//        com.whatsapp
//        com.twitter.android.lite
//        com.facebook.orca
//        com.instagram.android
        ArrayList<String> apps = new ArrayList<>();
        apps.add("com.facebook.katana");
        apps.add("com.whatsapp");
        apps.add("com.twitter.android.lite");
        apps.add("com.facebook.orca");
        apps.add("com.instagram.android");

        AtomicBoolean found = new AtomicBoolean(false);
        String finalTopPackageName = topPackageName;
        apps.forEach(app -> {
            if(finalTopPackageName.equals(app)){
                found.set(true);
            }
        });

        if(found.get()  && !hasDatePassed){
            Intent startHomescreen = new Intent(Intent.ACTION_MAIN);
            startHomescreen.addCategory(Intent.CATEGORY_HOME);
            startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startHomescreen);
            Toast.makeText(context, R.string.bro, Toast.LENGTH_SHORT).show();
        }

//        Intent intent1 = new Intent(context, BackgroundService.class);
//        context.startService(intent1);

//        System.out.println(topPackageName);
        Log.e("Task List", "Current App in foreground is: " + topPackageName);
    }


}
