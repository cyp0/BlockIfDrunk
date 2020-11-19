package com.example.byd.aplication.ui.lifeguardUI.lifeguard;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Advertencia")
                .setMessage("Al utilizar esta funcion se te impedira acceder a tu app de contactos" +
                        " por el tiempo de bloqueo, por lo que para llamar utiliza la funcion de " +
                        "telefono y contactos de la aplicacion")
                .setPositiveButton("Entiendo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
