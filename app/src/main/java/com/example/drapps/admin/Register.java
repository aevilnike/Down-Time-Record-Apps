package com.example.drapps.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drapps.Login;
import com.example.drapps.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    ImageButton backBtn;
    EditText etEmpNo,etName,etEmail;
    TextInputEditText etPass,etCPass;
    Button registerBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backBtn = findViewById(R.id.backBtn);
        etEmpNo = findViewById(R.id.etEmpNo);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etCPass = findViewById(R.id.etCPass);
        registerBtn = findViewById(R.id.registerBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register user
                inputData();
            }
        });

    }

    private String fullName, empNo, email, password, confirmPassword;

    private void inputData() {
        //input data
        fullName = etName.getText().toString().trim();
        empNo = etEmpNo.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPass.getText().toString().trim();
        confirmPassword = etCPass.getText().toString().trim();

        //Validate data & Clear error
        // Check for empty fields or invalid input
        if (fullName.isEmpty() || empNo.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            // Show toast for empty fields
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Show toast for invalid email format
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            // Show toast for password length less than 6 characters
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            // Show toast for password and confirm password mismatch
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            // All validations passed, proceed with registeration
            createAccount();
        }
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account..");
        progressDialog.show();

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account created
                saverFirebaseData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed creating account
                progressDialog.dismiss();
                Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Register Account...");
        String timestamp = ""+System.currentTimeMillis();

        //Saving user data into firebase
        HashMap<String, Object> haspMap = new HashMap<>();
        haspMap.put("uid",""+firebaseAuth.getUid());
        haspMap.put("email",""+email);
        haspMap.put("name",""+fullName);
        haspMap.put("employeeNumber",""+empNo);
        haspMap.put("password",""+password);
        haspMap.put("timestamp",""+timestamp);
        haspMap.put("accountType","User");

        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(haspMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //db updated
                        progressDialog.dismiss();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating db
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}