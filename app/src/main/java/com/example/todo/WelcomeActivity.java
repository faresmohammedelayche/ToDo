package com.example.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button btn_sign = findViewById(R.id.btn_signin);
        Button btn_log = findViewById(R.id.btn_login);

        btn_sign.setOnClickListener(view -> openBottomSheetSignin());
        btn_log.setOnClickListener(view -> openBottomSheetLogin());
    }

    private void openBottomSheetLogin() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_login);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        EditText fullname = bottomSheetDialog.findViewById(R.id.fullname);
        EditText email = bottomSheetDialog.findViewById(R.id.email);
        EditText password = bottomSheetDialog.findViewById(R.id.password);
        Button login = bottomSheetDialog.findViewById(R.id.login);

        if (login != null) {
            login.setOnClickListener(view -> {
                String fullnameEdit = fullname.getText().toString().trim();
                String emailEdit = email.getText().toString().trim();
                String passwordEdit = password.getText().toString().trim();

                if (fullnameEdit.isEmpty() || emailEdit.isEmpty() || passwordEdit.isEmpty()) {
                    Toast.makeText(this, "Complete all information", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(fullnameEdit, emailEdit, passwordEdit);
                    bottomSheetDialog.dismiss();
                }
            });
        }
        bottomSheetDialog.show();
    }

    private void registerUser(String fullnameEdit, String emailEdit, String passwordEdit) {
        mAuth.createUserWithEmailAndPassword(emailEdit, passwordEdit)
                .addOnSuccessListener(authResult -> {
                    String userId = mAuth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("fullName", fullnameEdit);
                    user.put("email", emailEdit);

                    db.collection("Users").document(userId).set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "You are registered", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error in saving information", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openBottomSheetSignin() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_signin);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        EditText email = bottomSheetDialog.findViewById(R.id.email);
        EditText password = bottomSheetDialog.findViewById(R.id.password);
        Button signin = bottomSheetDialog.findViewById(R.id.signin);

        if (signin != null) {
            signin.setOnClickListener(view -> {
                String emailTxt = email.getText().toString().trim();
                String passwordTxt = password.getText().toString().trim();

                if (emailTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(emailTxt, passwordTxt);
                    bottomSheetDialog.dismiss();
                }
            });
        }
        bottomSheetDialog.show();
    }

    private void loginUser(String emailTxt, String passwordTxt) {
        mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    }