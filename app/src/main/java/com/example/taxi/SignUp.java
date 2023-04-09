package com.example.taxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText etName, etEmail, etContact, etPassword, etConfirmPassword;
    Button btnRegister;
    FirebaseAuth auth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._=]+@[A-Z]+\\.+[A-Z]+";
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        mUser = auth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAction();
            }
        });

    }
    private void PerformAction(){
        String etNameText = etName.getText().toString();
        String etEmailText = etEmail.getText().toString();
        String etContactText = etContact.getText().toString();
        String etPasswordText = etPassword.getText().toString();
        String etConfirmPasswordText = etConfirmPassword.getText().toString();

        if(etNameText.matches(emailPattern))
        {
            Toast.makeText(this, "Please enter correct Email format!", Toast.LENGTH_SHORT).show();
        }
        else if(etNameText.isEmpty() || etEmailText.isEmpty() || etContactText.isEmpty() ||
                etPasswordText.isEmpty() || etConfirmPasswordText.isEmpty())
        {
            Toast.makeText(SignUp.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(etPasswordText.equals(etConfirmPasswordText))
            {
                progressDialog.setMessage("Please wait while Registering!");
                progressDialog.setTitle("Registration");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                auth.createUserWithEmailAndPassword(etEmailText,etPasswordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            userID = auth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name",etNameText);
                            user.put("email", etEmailText);
                            user.put("contact", etContactText);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                            progressDialog.dismiss();
                            sendUserToNextActivity();
                            Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }else
            {
                Toast.makeText(SignUp.this, "Make sure the two fields match!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(SignUp.this,Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}