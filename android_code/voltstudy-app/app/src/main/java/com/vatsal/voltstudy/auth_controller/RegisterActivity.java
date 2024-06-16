package com.vatsal.voltstudy.auth_controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vatsal.voltstudy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/** Register Activity */

public class RegisterActivity extends AppCompatActivity {

    CircleImageView ImgUserPhoto;
    static int PRERequestCode = 1 ;
    static int REQUESTCode = 1 ;
    Uri pickedImgUri ;
    byte[] fileInBytes;

    private EditText inputEmail, inputPassword, inputPassword2,inputUserName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.register_sign_in_button);
        btnSignUp = findViewById(R.id.register_sign_up_button);
        inputEmail =  findViewById(R.id.register_input_email);
        inputPassword =  findViewById(R.id.register_input_password);
        inputPassword2 = findViewById(R.id.register_input_confirm_password);
        inputUserName = findViewById(R.id.register_input_name);
        avLoadingIndicatorView = findViewById(R.id.register_loader);
        btnResetPassword = findViewById(R.id.register_btn_reset_password);


        btnResetPassword.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, ResetPasswordActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnSignIn.setOnClickListener(v -> {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        btnSignUp.setOnClickListener(v -> {

            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.smoothToShow();

            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String password2 = inputPassword2.getText().toString();
            String name = inputUserName.getText().toString();

            if( email.isEmpty() || name.isEmpty() || password.isEmpty()  || !password.equals(password2)) {
                // something goes wrong : all fields must be filled
                // we need to display an error message
                Toasty.warning(this, "Please Verify all fields", Toasty.LENGTH_LONG).show();
                btnSignUp.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.setVisibility(View.GONE);
                avLoadingIndicatorView.hide();
            }
            else {
                    // everything is ok and all fields are filled now we can start creating user account
                    // CreateUserAccount method will try to create the user if the email is valid
                    CreateUserAccount(email, name, password);

            }


        });

        ImgUserPhoto = findViewById(R.id.regUserPhoto) ;
        ImgUserPhoto.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission();
            }
            else
            {
                openGallery();
            }

        });
    }
    private void CreateUserAccount(String email, final String name, String password) {
        // this method create user account with specific email and password
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        // after we created user account we need to update his profile picture and name
                        updateUserInfo( name ,pickedImgUri,mAuth.getCurrentUser());

                        Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification();
                        Toasty.success(RegisterActivity.this,R.string.email_sent,Toasty.LENGTH_SHORT).show();


                    }
                    else
                    {
                        // account creation failed
                        Toasty.error(this, "account creation failed" + Objects.requireNonNull(task.getException()).getMessage(), Toasty.LENGTH_LONG).show();
                        btnSignUp.setVisibility(View.VISIBLE);
                        avLoadingIndicatorView.setVisibility(View.GONE);
                        avLoadingIndicatorView.hide();
                    }
                });
    }


    // update user photo and name

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        if(pickedImgUri!= null) {
            // first we need to upload user photo to firebase storage and get url
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
            final StorageReference imageFilePath = mStorage.child(Objects.requireNonNull(pickedImgUri.getLastPathSegment()));
            imageFilePath.putBytes(fileInBytes).addOnSuccessListener(taskSnapshot -> {

                // image uploaded successfully
                // now we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    // uri contain user image url
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(uri)
                            .build();

                    currentUser.updateProfile(profileUpdate)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // user info updated successfully
                                    Toasty.success(this, "Register Complete", Toasty.LENGTH_LONG).show();

                                    updateUI();
                                }
                            });
                });
            });
        }
        else{
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            currentUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // user info updated successfully
                            Toasty.success(this, "Register Complete", Toasty.LENGTH_LONG).show();
                            updateUI();
                        }
                    });
        }
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(homeActivity);
        finish();
    }


    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCode);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PRERequestCode);
            }

        }
        else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCode && data != null ) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            ImgUserPhoto.setImageURI(pickedImgUri);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedImgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i chose 25)
            Objects.requireNonNull(bmp).compress(Bitmap.CompressFormat.JPEG, 25, baos);
            fileInBytes = baos.toByteArray();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        avLoadingIndicatorView.setVisibility(View.GONE);
        avLoadingIndicatorView.hide();
    }
}

