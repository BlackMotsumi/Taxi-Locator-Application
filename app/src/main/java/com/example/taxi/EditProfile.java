package com.example.taxi;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    String full_name,contact,email;
    TextView etName,etContact,etEmail;
    ActivityResultLauncher<String> changePicture;
    Uri imageUri;
    StorageReference storageReference;
    FirebaseAuth f_Auth;
    FirebaseStorage storage;
    boolean isPermissionGranted;
    Button btnSave;
    FirebaseUser user;
    FirebaseFirestore rDog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        etContact = findViewById(R.id.etContact);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        storageReference = FirebaseStorage.getInstance().getReference();
        f_Auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = f_Auth.getCurrentUser();
        rDog = FirebaseFirestore.getInstance();



        Intent data = getIntent();
        full_name = data.getStringExtra("name");
        contact = data.getStringExtra("contact");
        email = data.getStringExtra("email");
        etName.setText(full_name);
        etContact.setText(contact);
        etEmail.setText(email);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(etEmail.getText().toString().isEmpty() || etName.getText().toString().isEmpty()
              || etContact.getText().toString().isEmpty()){
                  Toast.makeText(EditProfile.this, "One or fields is Empty!!", Toast.LENGTH_SHORT).show();
              }

              final String email = etEmail.getText().toString();
              user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                      DocumentReference docRef = rDog.collection("users").document(user.getUid());
                      Map<String,Object> edited = new HashMap<>();
                      edited.put("email", email);
                      edited.put("name",etName.getText().toString());
                      edited.put("contact",etContact.getText().toString());
                      docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              Toast.makeText(EditProfile.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      });
                      Toast.makeText(EditProfile.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {

                  }
              });

            }
        });

    }

    private void checkMyPermission () {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                Toast.makeText(EditProfile.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(EditProfile.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}