package com.vatsal.voltstudy.create_course_section;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.vatsal.voltstudy.models.Video;
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

/** Activity to Add Video Content to Course */

public class AddVideoActivity extends AppCompatActivity {

    private static final int PREQCode = 2 ;
    private static final int REQUESTCode = 2 ;
    private Uri videoUri;
    private StorageReference videoRef;
    private DatabaseReference videoDatabaseRef;
    private VideoView videoView;
    private String courseCode;
    FirebaseAuth mAuth;
    EditText videoNumber, videoTitle;
    ImageView addVideoImg, nextButton;
    TextView addVideoTxt, nextOrSkipText;
    Button uploadBtn, changeVideoBtn;
    ProgressBar addVideoProgress;
    MediaController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        getSupportActionBar();
        getDataOverIntent();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Videos To Course");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff333745));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        videoDatabaseRef = FirebaseDatabase.getInstance().getReference().child("videos").child(courseCode);
        

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        videoRef = storageReference.child("courseVideos").child(currentUser.getUid());

        addVideoImg = findViewById(R.id.img_add_video);
        addVideoTxt = findViewById(R.id.txt_select_video);
        videoNumber = findViewById(R.id.editText_video_number);
        videoTitle = findViewById(R.id.editText_video_title);
        videoView = findViewById(R.id.add_video_view);
        uploadBtn = findViewById(R.id.btn_upload_video);
        nextButton = findViewById(R.id.next_to_course_text);
        changeVideoBtn = findViewById(R.id.btn_change_video);
        nextOrSkipText = findViewById(R.id.btn_video_next_skip_to_text);
        addVideoProgress = findViewById(R.id.add_video_progress_circle);

        nrVideos(videoNumber, courseCode);

        addVideoImg.setOnClickListener(view -> {
            // here when image clicked we need to open the gallery
            // before we open the gallery we need to check if our app have the access to user files
            // we did this before in register activity I'm just going to copy the code to save time ...
            checkAndRequestForPermission();
        });

        changeVideoBtn.setOnClickListener(view -> {
            checkAndRequestForPermission();
        });


            uploadBtn.setOnClickListener(v -> {
                if(!videoTitle.getText().toString().isEmpty()
                        && !videoNumber.getText().toString().isEmpty()
                        && videoUri != null) {
                    uploadBtn.setVisibility(View.INVISIBLE);
                    addVideoProgress.setVisibility(View.VISIBLE);

                    final StorageReference videoFilePath = videoRef.child(Objects.requireNonNull(videoUri.getLastPathSegment()));
                    videoFilePath.putFile(videoUri).addOnSuccessListener(taskSnapshot -> videoFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        String videoDownloadLink = uri.toString();
                        Double vidNoInDouble = Double.parseDouble(videoNumber.getText().toString());
                        // create video Object
                        Video video = new Video(
                                videoDownloadLink,
                                videoTitle.getText().toString(),
                                vidNoInDouble
                        );

                        // Add post to firebase database
                        addVideo(video);

                    }).addOnFailureListener(e -> {
                        // something goes wrong uploading picture

                        showMessage(e.getMessage());

                    })).addOnProgressListener(taskSnapshot -> {
                        uploadProgress(taskSnapshot);
                    });
                }
                else{
                    Toasty.info(AddVideoActivity.this, "Please Select A Video & Input All Fields",Toasty.LENGTH_SHORT).show();
                }
            });

            nextButton.setOnClickListener(v -> {
                if(nextOrSkipText.getText().toString().equals("Skip")) {
                    String isVideo = "";
                    Intent AddCourseVideoIntent = new Intent(AddVideoActivity.this, AddTextActivity.class);
                    AddCourseVideoIntent.putExtra("courseCode", courseCode);
                    AddCourseVideoIntent.putExtra("isVideo", isVideo);
                    startActivity(AddCourseVideoIntent);
                }
                else {
                    String isVideo = "Video";
                    Intent AddCourseVideoIntent = new Intent(AddVideoActivity.this, AddTextActivity.class);
                    AddCourseVideoIntent.putExtra("courseCode", courseCode);
                    AddCourseVideoIntent.putExtra("isVideo", isVideo);
                    startActivity(AddCourseVideoIntent);
                }
            });

    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AddVideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddVideoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toasty.info(AddVideoActivity.this, "Please accept for required permission",Toasty.LENGTH_SHORT).show();
            }

            else
            {
                ActivityCompat.requestPermissions(AddVideoActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PREQCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            selectVideo();

    }

    public void getDataOverIntent(){
        courseCode = getIntent().getExtras().getString("courseCode");
    }

    public void uploadProgress(UploadTask.TaskSnapshot taskSnapshot){

        long fileSize = taskSnapshot.getTotalByteCount();
        long uploadBytes = taskSnapshot.getBytesTransferred();
        long progress = (100 * uploadBytes)/fileSize ;

        ProgressBar progressBar = findViewById(R.id.add_video_progressbar);
        progressBar.setProgress((int) progress);
    }

    public void selectVideo(){
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("video/*");
            startActivityForResult(galleryIntent,REQUESTCode);
    }

    private void addVideo(Video video) {


        DatabaseReference myRef = videoDatabaseRef.push();

        String key = myRef.getKey();
        video.setVideoID(key);

        // add video data to firebase database
        myRef.setValue(video).addOnSuccessListener(aVoid -> {

            Intent intent = new Intent(AddVideoActivity.this, QuizFormActivity.class);
            intent.putExtra("courseCode", courseCode);
            intent.putExtra("ModuleID",key);
            startActivity(intent);

            Toasty.success(this, "Video Added Successfully", Toasty.LENGTH_SHORT).show();
            addVideoProgress.setVisibility(View.GONE);
            videoTitle.getText().clear();
            videoNumber.getText().clear();
            videoUri = null;
            mController.removeAllViewsInLayout();
            mController.refreshDrawableState();
            videoView.setVideoURI(null);
            videoView.setBackgroundColor(Color.BLACK);

            uploadBtn.setVisibility(View.VISIBLE);
            uploadBtn.setText("Click To Upload Another Video");
            addVideoTxt.setVisibility(View.VISIBLE);
            addVideoTxt.setText("Click To Select Video");
            nrVideos(videoNumber, courseCode);
            addVideoImg.setVisibility(View.VISIBLE);
            addVideoImg.setClickable(true);
            addVideoImg.setFocusable(true);
            changeVideoBtn.setVisibility(View.INVISIBLE);

            ProgressBar progressBar = findViewById(R.id.add_video_progressbar);
            progressBar.setProgress(0);




        });

    }

    private void showMessage(String message) {

        Toast.makeText(AddVideoActivity.this,message,Toast.LENGTH_LONG).show();
    }

    private void nrVideos(EditText videos, String courseId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("videos")
                .child(courseId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    videos.setText(dataSnapshot.getChildrenCount()+1 + "");
                    nextOrSkipText.setText("Next");

                }
                else {
                    videos.setText("1");
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
                videoView.setBackgroundColor(0);
                videoUri = data.getData();
                Toasty.success(this, "Video Saved to: \n" + videoUri, Toasty.LENGTH_LONG).show();
                videoView.setVideoURI(videoUri);
                mController = new MediaController(this);
                mController.setAnchorView(videoView);
                videoView.setMediaController(mController);
                videoView.seekTo(1);
                videoView.pause();
                addVideoImg.setVisibility(View.GONE);
                addVideoImg.setClickable(false);
                addVideoImg.setFocusable(false);
                addVideoTxt.setVisibility(View.GONE);
                changeVideoBtn.setVisibility(View.VISIBLE);

        }else if(resultCode == RESULT_CANCELED){
            Toasty.warning(this, "Video selection cancelled",Toasty.LENGTH_LONG).show();
            if(videoUri!=null) {
                videoUri = null;
                mController.removeAllViewsInLayout();
                mController.refreshDrawableState();
                videoView.setVideoURI(null);
                addVideoImg.setVisibility(View.VISIBLE);
                addVideoImg.setClickable(true);
                addVideoImg.setFocusable(true);
                addVideoTxt.setVisibility(View.VISIBLE);
                changeVideoBtn.setVisibility(View.INVISIBLE);
            }


        }else {
            Toasty.error(this, "Failed To Select Video",Toasty.LENGTH_LONG).show();
            if(videoUri!=null) {
                videoUri = null;
                mController.removeAllViewsInLayout();
                mController.refreshDrawableState();
                videoView.setVideoURI(null);
                addVideoImg.setVisibility(View.VISIBLE);
                addVideoImg.setClickable(true);
                addVideoImg.setFocusable(true);
                addVideoTxt.setVisibility(View.VISIBLE);
                changeVideoBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(nextOrSkipText.getText().toString().equals("Skip")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AddVideoActivity.this);
            builder.setTitle("Do you want to exit?");
            builder.setMessage("Your course will be deleted and you will have to start from scratch.");
            builder.setPositiveButton("Yes", (dialog, id) -> {
                DatabaseReference mCourseReference = FirebaseDatabase.getInstance().getReference().child("courses").child(courseCode);

                mCourseReference.child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String storageUrl = (String) dataSnapshot.getValue();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storageUrl);
                        storageReference.delete().addOnSuccessListener(aVoid -> {
                            // File deleted successfully
                            Log.d("TAG", "onSuccess: deleted file");
                        }).addOnFailureListener(exception -> {
                            // Uh-oh, an error occurred!
                            Log.d("TAG", "onFailure: did not delete file");
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
                mCourseReference.removeValue();


                Intent HomeIntent = new Intent(AddVideoActivity.this, HomeActivity.class);
                startActivity(HomeIntent);
                finish();
            });
            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            builder.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddVideoActivity.this);
            builder.setTitle("We are sorry but you cannot exit course creation process at this stage");
            builder.setPositiveButton("OK", (dialog, id) -> {
                dialog.cancel();
            });
            builder.show();
        }

    }

}

