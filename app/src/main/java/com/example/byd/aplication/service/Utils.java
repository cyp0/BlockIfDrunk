package com.example.byd.aplication.service;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Process;

import static android.app.AppOpsManager.MODE_ALLOWED;

@SuppressWarnings("deprecation")
public class Utils {
    public static boolean checkPermission(Context context){
        AppOpsManager appOpsManager = ((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE));

        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(),context.getPackageName());

        return mode == MODE_ALLOWED;

    }
}
