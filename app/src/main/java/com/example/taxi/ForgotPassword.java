package com.example.taxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
public class ForgotPassword extends AppCompatActivity {

    ProgressDialog progressDialog;
    Button btnSubmitRest;
    EditText etResetP;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnSubmitRest = findViewById(R.id.btnSubmitRest);
        etResetP = findViewById(R.id.etResetP);
        progressDialog =new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();
        
        resetPassword();
    }

    private void resetPassword() {
        String email = etResetP.getText().toString().trim();
        if(email.isEmpty()){
            etResetP.setError("Please provide Email");
            etResetP.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etResetP.setError("Please provide valid Email");
            etResetP.requestFocus();
            return;
        }
        progressDialog.setMessage("Please wait while resetting Password!");
        progressDialog.setTitle("Reset Password");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(ForgotPassword.this, "Reset Instructions ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(e -> progressDialog.dismiss());
    }
}