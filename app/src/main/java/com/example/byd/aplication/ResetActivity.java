package com.example.byd.aplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.databinding.ActivityResetBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    EditText emailEditText;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        ActivityResetBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_reset);
        emailEditText = binding.newEmail2;
    }

    public void resetPassword(View view) {
        String email = emailEditText.getText().toString().trim();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Snackbar snackbar =  Snackbar.make(findViewById(R.id.resetLayout), R.string.password_reset, Snackbar.LENGTH_SHORT);
                snackbar.show();

            }
        });
    }
}
