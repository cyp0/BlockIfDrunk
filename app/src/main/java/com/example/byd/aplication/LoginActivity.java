package com.example.byd.aplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.aplication.ui.MainActivity;
import com.example.byd.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        usernameEditText = binding.username;
        passwordEditText = binding.password;

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {

                } else if (user.isEmailVerified()) {
                    Intent toEngine = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(toEngine);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user.isEmailVerified())
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void login(View view) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), "Contraseña incorrecta", Snackbar.LENGTH_LONG );
            snackbar.show();
//            View snackbarView= snackbar.getView();
//            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
//            textView.setTextAlignment(textView.TEXT_ALIGNMENT_CENTER);
        }
        else {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Error al Iniciar Sesion", Toast.LENGTH_SHORT).show();
                    }
                    if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "Correo no verificado", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent toEngine = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(toEngine);
                    }
                }
            });
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}
