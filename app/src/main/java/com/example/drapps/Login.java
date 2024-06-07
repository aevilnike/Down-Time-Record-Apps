package com.example.drapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drapps.admin.AdminActivity;
import com.example.drapps.admin.Register;
import com.example.drapps.user.BottomNavigation;
import com.example.drapps.user.EmployeeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private TextView etFP;
    private EditText etEmail, etPass;
    private Button loginBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "drApps";
    private boolean isChecked = false;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etFP = findViewById(R.id.etFP);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        loginBtn = findViewById(R.id.loginBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading..");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("SETTINGS_SP", MODE_PRIVATE);
        isChecked = sharedPreferences.getBoolean("FCM_ENABLED", false);

        // Always subscribe to the FCM topic when the activity starts
        subscribeToTopic();

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // User is already logged in, redirect to appropriate activity
            redirectUser();
        }

        etFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open register seller
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email pattern", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || password.length() > 12) {
            Toast.makeText(this, "Fill your Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //logged in successfully
                        saveLoginState();
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            // Handle case where the account doesn't exist
                            Toast.makeText(Login.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                        } else {
                            // Other login failure, show a generic message
                            Toast.makeText(Login.this, "Failed to log in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveLoginState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void makeMeOnline() {
        progressDialog.setMessage("Checking User...");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            if (accountType.equals("Admin")) {
                                progressDialog.dismiss();
                                //user is admin
                                startActivity(new Intent(Login.this, AdminActivity.class));
                                finish();
                            } else {
                                progressDialog.dismiss();
                                //user is not admin
                                startActivity(new Intent(Login.this, BottomNavigation.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectUser() {
        progressDialog.setMessage("Redirecting...");
        progressDialog.show();
        checkUserType(); // Redirect based on user type
    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Subscribe successfully
                        // Save setting in shared preferences
                        spEditor = sharedPreferences.edit();
                        spEditor.putBoolean("FCM_ENABLED", true);
                        spEditor.apply();

                        Toast.makeText(Login.this, "Noti on", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to subscribe
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
