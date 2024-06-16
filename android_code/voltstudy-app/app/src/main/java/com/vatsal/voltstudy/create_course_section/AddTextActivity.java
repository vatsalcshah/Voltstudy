package com.vatsal.voltstudy.create_course_section;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vatsal.voltstudy.MailHelpers.SendMail;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.vatsal.voltstudy.models.TextCourse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Activity to Add Textual Content to Course */

public class AddTextActivity extends AppCompatActivity {

    private static final int PREQCode = 2 ;
    private static final int REQUESTCode = 2 ;
    private Uri textUri;
    private StorageReference textStorageRef;
    private CardView cardView;
    FirebaseAuth mAuth;

    private DatabaseReference textCourseRef;
    private String courseCode, isVideoCourse;
    EditText textCourseNumber, textCourseTitle;
    ImageView nextButton;
    TextView nextOrSkipText;
    Button uploadBtn;
    ProgressBar addTextProgress, circleProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_text);
        getSupportActionBar();
        getDataOverIntent();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Textual Course");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff333745));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        textCourseRef = FirebaseDatabase.getInstance().getReference().child("textcourses").child(courseCode);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        textStorageRef = storageReference.child("textContent").child(currentUser.getUid());


        textCourseNumber = findViewById(R.id.editText_course_text_number);
        textCourseTitle = findViewById(R.id.edittext_course_text_title);

        uploadBtn = findViewById(R.id.btn_upload_course_text);
        nextButton = findViewById(R.id.next_to_add_quiz);
        nextOrSkipText = findViewById(R.id.tv_next_course_text);
        addTextProgress = findViewById(R.id.add_text_course_progress);
        circleProgress = findViewById(R.id.upload_text_pdf_progressbar);
        cardView = findViewById(R.id.upload_text_card);

        nrTextCourses(textCourseNumber, courseCode);
        
        cardView.setOnClickListener(v -> {
            checkAndRequestForPermission();
        });
        

        uploadBtn.setOnClickListener(v -> {
            if(!textCourseNumber.getText().toString().isEmpty()
               && !textCourseTitle.getText().toString().isEmpty()
               && textUri != null) {

                final StorageReference textFilePath = textStorageRef.child(Objects.requireNonNull(textUri.getLastPathSegment()));
                textFilePath.putFile(textUri).addOnSuccessListener(taskSnapshot -> textFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String textDownloadLink = uri.toString();
                    
                    // create video Object
                    Double textNumberInDouble = Double.parseDouble(textCourseNumber.getText().toString());
                    TextCourse textCourse = new TextCourse(
                            textDownloadLink,
                            textCourseTitle.getText().toString(),
                            textNumberInDouble
                    );


                    // Add post to firebase database
                    addTextCourse(textCourse);

                }).addOnFailureListener(e -> {
                    // something goes wrong uploading picture

                    showMessage(e.getMessage());

                })).addOnProgressListener(taskSnapshot -> {
                    uploadBtn.setVisibility(View.INVISIBLE);
                    circleProgress.setVisibility(View.VISIBLE);
                    uploadProgress(taskSnapshot);
                });
            }
            else{
                Toasty.info(AddTextActivity.this, "Please Select A Pdf & Input All Fields",Toasty.LENGTH_SHORT).show();
            }
                    
        });
        

        nextButton.setOnClickListener(v -> {
            if(nextOrSkipText.getText().toString().equals("Skip")) {
                if(isVideoCourse.equals("Video")) {
                    String isTextCourse = isVideoCourse;
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("courses").child(courseCode);
                    mDatabase.child("course_type").setValue(isTextCourse);

                    SendMail sm = new SendMail(this, "voltstudy.app@gmail.com", "Course Verification", "Please Verify This Course Code: " + courseCode);
                    sm.execute();

                    Toasty.success(AddTextActivity.this, "Course Successfully Added Successfully", Toasty.LENGTH_LONG).show();

                    Intent AddCourseVideoIntent = new Intent(AddTextActivity.this, HomeActivity.class);
                    startActivity(AddCourseVideoIntent);
                    finish();
                }
                else{
                    Toasty.warning(AddTextActivity.this, "You need to add a video or textual content to course!", Toasty.LENGTH_LONG).show();
                }
            }
            else {
                if(isVideoCourse.equals("Video")){
                    String isTextCourse = "Text" + " & " + isVideoCourse;
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("courses").child(courseCode);
                    mDatabase.child("course_type").setValue(isTextCourse);

                    SendMail sm = new SendMail(this, "voltstudy.app@gmail.com", "Course Verification", "Please Verify This Course Code: "+courseCode);
                    sm.execute();

                    Toasty.success(AddTextActivity.this, "Course Successfully Added Successfully", Toasty.LENGTH_LONG).show();

                    Intent AddCourseVideoIntent = new Intent(AddTextActivity.this, HomeActivity.class);
                    startActivity(AddCourseVideoIntent);
                    finish();
                }
                else{
                    String isTextCourse = "Text";
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("courses").child(courseCode);
                    mDatabase.child("course_type").setValue(isTextCourse);

                    SendMail sm = new SendMail(this, "voltstudy.app@gmail.com", "Course Verification", "Please Verify This Course Code: "+courseCode);
                    sm.execute();

                    Toasty.success(AddTextActivity.this, "Course Successfully Added Successfully", Toasty.LENGTH_LONG).show();

                    Intent AddCourseVideoIntent = new Intent(AddTextActivity.this, HomeActivity.class);
                    startActivity(AddCourseVideoIntent);
                    finish();
                }
            }
        });

    }
    
    public void uploadProgress(UploadTask.TaskSnapshot taskSnapshot){

        long fileSize = taskSnapshot.getTotalByteCount();
        long uploadBytes = taskSnapshot.getBytesTransferred();
        long progress = (100 * uploadBytes)/fileSize ;

        ProgressBar progressBar = findViewById(R.id.select_text_progress);
        progressBar.setProgress((int) progress);
    }

    private void showMessage(String message) {

        Toast.makeText(AddTextActivity.this,message,Toast.LENGTH_LONG).show();
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AddTextActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddTextActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toasty.info(AddTextActivity.this, "Please accept for required permission",Toasty.LENGTH_SHORT).show();
            }

            else
            {
                ActivityCompat.requestPermissions(AddTextActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PREQCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            selectText();

    }

    public void selectText(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent,REQUESTCode);
    }
    

    public void getDataOverIntent(){
        courseCode = getIntent().getExtras().getString("courseCode");
        isVideoCourse = getIntent().getExtras().getString("isVideo");
    }
    

    private void addTextCourse(TextCourse textCourse) {


        DatabaseReference myRef = textCourseRef.push();

        String key = myRef.getKey();
        textCourse.setTextCourseID(key);


        // add video data to firebase database
        myRef.setValue(textCourse).addOnSuccessListener(aVoid -> {

            Intent intent = new Intent(AddTextActivity.this, QuizFormActivity.class);
            intent.putExtra("courseCode", courseCode);
            intent.putExtra("ModuleID",key);
            startActivity(intent);

            Toasty.success(AddTextActivity.this, "Textual Course Added successfully", Toasty.LENGTH_SHORT).show();
            circleProgress.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.VISIBLE);
            textCourseNumber.getText().clear();
            textCourseTitle.getText().clear();
            textUri = null;

            uploadBtn.setText("Click To Upload Another Course Text");
            nrTextCourses(textCourseNumber, courseCode);

        });

    }

    private void nrTextCourses(EditText textCourse, String courseId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("textcourses")
                .child(courseId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    textCourse.setText(dataSnapshot.getChildrenCount()+1 + "");
                    nextOrSkipText.setText("Next");

                }
                else {
                    textCourse.setText("1");
                    nextOrSkipText.setText("Skip");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCode && data != null) {
            textUri = data.getData();
            Toasty.success(this, "Text Saved to: \n" + textUri, Toasty.LENGTH_LONG).show();
        }else if(resultCode == RESULT_CANCELED){
            Toasty.warning(this, "Text selection cancelled",Toasty.LENGTH_LONG).show();
            if(textUri!=null) {
                textUri = null;
            }
        }else {
            Toasty.error(this, "Failed To Select Text",Toasty.LENGTH_LONG).show();
            if(textUri!=null) {
                textUri = null;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(nextOrSkipText.getText().toString().equals("Next")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddTextActivity.this);
            builder.setTitle("Go to Previous Step");
            builder.setMessage("We are sorry but you cannot go to the previous step. Please mail us at voltstudy.app@gmail.com for further guidance.");
            builder.setPositiveButton("OK", (dialog, id) -> dialog.cancel());
            builder.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddTextActivity.this);
            builder.setTitle("Do you want to exit?");
            builder.setPositiveButton("Yes", (dialog, id) -> {
                finish();
            });
            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            builder.show();
        }

    }


}


