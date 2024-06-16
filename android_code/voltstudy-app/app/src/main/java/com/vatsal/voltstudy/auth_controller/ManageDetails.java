package com.vatsal.voltstudy.auth_controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/** ManageDetails Activity to get or update user information */

public class ManageDetails extends AppCompatActivity {

    private static final int PREQCode = 2 ;
    private static final int REQUESTCode = 2 ;
    private EditText editTextName, editTextEmail;
    private TextView TVreferralPoints;
    private FirebaseAuth mAuth;
    private CircleImageView userImage;
    private Uri pickedImgUri = null;
    private byte[] fileInBytes;
    private AVLoadingIndicatorView avLoadingIndicatorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_details);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();

        Button updateBtn = findViewById(R.id.button_update_details);
        Button resetPasswordBtn = findViewById(R.id.button_reset_password);
        Button signOutBtn = findViewById(R.id.button_sign_out);

        editTextName=findViewById(R.id.edittext_update_name);
        editTextEmail=findViewById(R.id.edittext_update_email);
        userImage = findViewById(R.id.userImage);
        TVreferralPoints = findViewById(R.id.user_referral_points);

        avLoadingIndicatorView = findViewById(R.id.manage_details_progress);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference referralRef = rootRef.child("referralProg");

        referralRef.child(mAuth.getCurrentUser().getUid()).child("referred_number_users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    TVreferralPoints.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                }
                else {
                    TVreferralPoints.setText("0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        editTextName.setHint(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
        editTextEmail.setHint(mAuth.getCurrentUser().getEmail());

        Glide.with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .placeholder(R.drawable.ic_user_place_holder)
                .circleCrop()
                .into(userImage);

        userImage.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestForPermission();
            }
            else
            {
                openGallery();
            }

        });


        updateBtn.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String name = editTextName.getText().toString();
            updateUserInfo(email, name, pickedImgUri, mAuth.getCurrentUser());
        });

        resetPasswordBtn.setOnClickListener(v -> {
            startActivity(new Intent(ManageDetails.this, ResetPasswordActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        signOutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ManageDetails.this, LoginActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


    }



    // update user photo and name

    private void updateUserInfo(final String email, final String name, Uri ImgUri, final FirebaseUser currentUser) {

        if(ImgUri != null){
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.smoothToShow();

            // first we need to upload user photo to firebase storage and get url
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
            final StorageReference imageFilePath = mStorage.child(Objects.requireNonNull(pickedImgUri.getLastPathSegment()));
            imageFilePath.putBytes(fileInBytes).addOnSuccessListener(taskSnapshot -> {

                // image uploaded successfully
                // now we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    // uri contain user image url
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build();

                    currentUser.updateProfile(profileUpdate)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // user info updated successfully
                                    Toasty.success(ManageDetails.this, "Image has been updated successfully", Toasty.LENGTH_LONG).show();
                                    avLoadingIndicatorView.setVisibility(View.GONE);
                                    avLoadingIndicatorView.smoothToHide();
                                }
                            }).addOnFailureListener(e -> {
                                Toasty.error(ManageDetails.this, Objects.requireNonNull(e.getMessage()),Toasty.LENGTH_SHORT).show();
                            avLoadingIndicatorView.setVisibility(View.GONE);
                            avLoadingIndicatorView.smoothToHide();
                            });
                });
            });
            }

        if(!editTextEmail.getText().toString().isEmpty()){
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.smoothToShow();

            LayoutInflater myLayout = LayoutInflater.from(ManageDetails.this);
            final View view = myLayout.inflate(R.layout.password_alert_dialog, null);
            AlertDialog alertDialog = new AlertDialog.Builder(ManageDetails.this).create();
            alertDialog.setTitle("Please enter password to proceed");
            alertDialog.setCancelable(false);

            final EditText etPassword = view.findViewById(R.id.et_enter_password);

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {

                if(!etPassword.getText().toString().isEmpty()){

                    String password = etPassword.getText().toString();

                    // Get auth credentials from the user for re-authentication
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(Objects.requireNonNull(currentUser.getEmail()), password);

                    // Current Login Credentials \\
                    // Prompt the user to re-provide their sign-in credentials
                        currentUser.reauthenticate(credential)
                                .addOnCompleteListener(task -> {
                                    Log.d("TAG", "User re-authenticated.");

                                    //Now change your email address \\
                                    //----------------Code for Changing Email Address----------\\
                                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                        user1.updateEmail(email)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Toasty.success(ManageDetails.this, "Email Successfully Updated.", Toasty.LENGTH_LONG).show();
                                                        alertDialog.dismiss();
                                                        avLoadingIndicatorView.setVisibility(View.GONE);
                                                        avLoadingIndicatorView.smoothToHide();
                                                    }
                                                    else{
                                                        Toasty.error(ManageDetails.this, "There was some problem updating email", Toasty.LENGTH_LONG).show();
                                                        avLoadingIndicatorView.setVisibility(View.GONE);
                                                        avLoadingIndicatorView.smoothToHide();
                                                    }
                                                });
                                    //----------------------------------------------------------\\
                                }).addOnFailureListener(e -> {
                            Toasty.error(ManageDetails.this, "There was some problem updating email", Toasty.LENGTH_LONG).show();
                            avLoadingIndicatorView.setVisibility(View.GONE);
                            avLoadingIndicatorView.smoothToHide();
                            alertDialog.dismiss();
                        });

                }
                else{
                    Toasty.error(ManageDetails.this, "Please Enter Password",Toasty.LENGTH_LONG).show();
                    avLoadingIndicatorView.setVisibility(View.GONE);
                    avLoadingIndicatorView.smoothToHide();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> alertDialog.dismiss());
            alertDialog.setView(view);
            alertDialog.show();
        }

        if(!editTextName.getText().toString().isEmpty()){
            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.smoothToShow();

            UserProfileChangeRequest profileUpdate2 = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            currentUser.updateProfile(profileUpdate2)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // user info updated successfully
                            Toasty.success(ManageDetails.this, "Name has been updated successfully", Toasty.LENGTH_LONG).show();
                            avLoadingIndicatorView.setVisibility(View.GONE);
                            avLoadingIndicatorView.smoothToHide();
                        }
                    }).addOnFailureListener(e -> {
                Toasty.error(ManageDetails.this, Objects.requireNonNull(e.getMessage()),Toasty.LENGTH_SHORT).show();
                avLoadingIndicatorView.setVisibility(View.GONE);
                avLoadingIndicatorView.smoothToHide();
                    });
        }

    }



    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCode);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(ManageDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ManageDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(ManageDetails.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(ManageDetails.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PREQCode);
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
            userImage.setImageURI(pickedImgUri);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
