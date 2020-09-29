package com.example.byd.aplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        ActivitySignUpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        usernameEditText = binding.newUsername;
        passwordEditText = binding.newPassword;

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                    startActivity(intent);
                } else {

                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void SignUp(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Error al Registrar", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    user.sendEmailVerification();
                    Toast.makeText(SignUpActivity.this, "Verifica tu Cuenta", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}

