package com.example.byd.aplication.ui.home.startEngine.devicesList;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.byd.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

    }

    void dismissDialog(){
        dialog.dismiss();
    }
}
