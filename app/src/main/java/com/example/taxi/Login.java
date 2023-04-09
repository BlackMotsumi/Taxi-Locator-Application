package com.example.taxi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText etName, etPassword;
    TextView tvForgotPassword,tvLogin;
    Button btnSignUp, btnLogin;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._=]+@[A-Z]+\\.+[A-Z]+";
    FirebaseAuth auth;
    ImageView ivGoogleIcon;
    GoogleSignInOptions gsio;
    GoogleSignInClient gsic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etName);
        etPassword =findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        ivGoogleIcon = findViewById(R.id.ivGoogleIcon);
        tvLogin = findViewById(R.id.tvLogin);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsic = GoogleSignIn.getClient(this,gsio);
        
        ivGoogleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformLogin();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });


    }

    private void SignIn() {
        Intent intent = gsic.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {

            task.getResult(ApiException.class);
            HomeActivity();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void HomeActivity() {
        finish();

    }

    private void PerformLogin() {

        final String nameText = etName.getText().toString();
        final String passwordText = etPassword.getText().toString();

        if(nameText.matches(emailPattern))
        {
            Toast.makeText(this, "Please enter correct Email format!", Toast.LENGTH_SHORT).show();
        }
        else if(nameText.isEmpty() || passwordText.isEmpty())
        {
            Toast.makeText(Login.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
        }else
        {
            progressDialog.setMessage("Please wait while Login!");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

           auth.signInWithEmailAndPassword(nameText,passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful())
                   {
                       progressDialog.dismiss();
                       singInUser();
                       Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                   }else
                   {
                       progressDialog.dismiss();
                       Toast.makeText(Login.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                   }
               }
           });

        }
    }

    private void singInUser() {
        Intent intent = new Intent(Login.this,FragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}