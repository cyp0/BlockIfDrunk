package com.example.byd.aplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView profilePic;
    private EditText userEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneEditText;
    private EditText coloniaEditText;
    private EditText calleYNumeroEditText;
    private EditText confirmPasswordEditText;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    //Database
    DatabaseReference databaseReference;
    //Firebase storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ActivitySignUpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userEditText = binding.newUserName;
        emailEditText = binding.newEmail;
        passwordEditText = binding.newPassword;
        profilePic = binding.profilePic;
        phoneEditText = binding.editTextPhone;
        coloniaEditText = binding.editTextColonia;
        calleYNumeroEditText = binding.editTextCalle;
        confirmPasswordEditText = binding.passwordEdidText;

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
        final String username = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        final String nombre = userEditText.getText().toString().trim();

        final String celular = phoneEditText.getText().toString().trim();

        final String colonia = coloniaEditText.getText().toString().trim();

        final String calleYNumero = calleYNumeroEditText.getText().toString().trim();

        final String confirmPassword = confirmPasswordEditText.getText().toString();

        if (nombre.isEmpty() && celular.isEmpty() && colonia.isEmpty() && calleYNumero.isEmpty()) {
            createSnackbar(R.string.emptyFields);
        } else if (!password.equals(confirmPassword)) {
            createSnackbar(R.string.confirmPassword);

        } else {

            createUser(username, password, nombre, celular, colonia, calleYNumero);

        }
    }



    private void createUser(final String username, String password, final String nombre, final String celular, final String colonia, final String calleYNumero) {
        firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Error al Registrar", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> value = new HashMap<>();
                    value.put("nombre", nombre);
                    value.put("correo", username);
                    value.put("celular", celular);
                    value.put("colonia", colonia);
                    value.put("calleYNumero", calleYNumero);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String id = firebaseAuth.getCurrentUser().getUid();

                    databaseReference.child("Users").child(id).setValue(value);
                    assert user != null;

                    Snackbar.make(findViewById(R.id.signUpLayout), R.string.confirmEmail, Snackbar.LENGTH_SHORT)
                            .addCallback(new Snackbar.Callback(){
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }).show();

                    user.sendEmailVerification();
                }
            }
        });
    }

    public void profileAvatarHandler(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Bitmap bitmap = ((Bitmap) data.getExtras().get("data"));
            profilePic.setImageBitmap(bitmap);
            handleUpload(bitmap);
//            imageUri = data.getData();
//            profilePic.setImageURI(imageUri);
//            uploadPicture();

        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        storageReference.child("profileImages").child(firebaseAuth.getCurrentUser().getUid() + ".jpeg");

        storageReference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SignUpActivity.this, "Imagen Subida", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPicture() {
        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);


        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SignUpActivity.this, "Imagen subida", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(SignUpActivity.this, "Fallo al subir", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createSnackbar(int title) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.signUpLayout), title, Snackbar.LENGTH_LONG);
        snackbar.show();

    }
}

