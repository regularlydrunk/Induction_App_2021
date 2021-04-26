package com.example.inductionapp2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout inputEmail, inputPassword, inputConfirmPassword;
    Button btnRegister;
    TextView alreadyHaveAnAccount;
    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        alreadyHaveAnAccount = findViewById(R.id.createNewAccount);
        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttemptRegistration();
            }
        });

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });
    }

    private void AttemptRegistration() {
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        String confirmPassword = inputConfirmPassword.getEditText().getText().toString();

        if (email.isEmpty() || !email.contains("@mmu.ac.uk")) //Filters so only MMU emails can register
        {
            showError(inputEmail, "Email is not valid"); //If email doesn't match error is thrown
        } else if (password.isEmpty() || password.length() < 6) {
            showError(inputPassword, "Password must be greater than 6 characters");
        } else if (!confirmPassword.equals(password)) {
            showError(inputConfirmPassword, "Passwords did not match");
        } else {
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait while we check your credentials");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration is complete.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }
}