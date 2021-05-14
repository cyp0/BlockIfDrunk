package com.example.byd.aplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
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
        progressBar = binding.progressBarLogin;
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
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.loginLayout), R.string.empty_password, Snackbar.LENGTH_LONG );
            snackbar.show();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Snackbar.make(view, R.string.wrong_credentials, Snackbar.LENGTH_SHORT).show();
//                        Toast.makeText(LoginActivity.this, "El correo o contraseña no existe", Toast.LENGTH_SHORT).show();
                    }
                   else if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(findViewById(R.id.loginLayout).getWindowToken(), 0);
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Snackbar.make(view, R.string.mail_unveryfied, Snackbar.LENGTH_SHORT).show();
//                        Toast.makeText(LoginActivity.this,"Correo no verificado" , Toast.LENGTH_SHORT).show();
                    }
                    else if(task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Intent toEngine = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(toEngine);
                    }
                }
            });
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.loginLayout).getWindowToken(), 0);
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {

    }


    public void toReset(View view) {
        startActivity(new Intent(this, ResetActivity.class));
    }
}
