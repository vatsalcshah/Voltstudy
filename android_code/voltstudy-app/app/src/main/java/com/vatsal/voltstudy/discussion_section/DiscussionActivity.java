package com.vatsal.voltstudy.discussion_section;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.my_courses_section.MyCoursesActivity;
import com.vatsal.voltstudy.viewholders.DiscussionPostAdapter;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.vatsal.voltstudy.models.Post;
import com.vatsal.voltstudy.search_section.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Discussion Activity */

public class DiscussionActivity extends AppCompatActivity {


    private static final int PREQCode = 2 ;
    private static final int REQUESTCode = 2 ;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    Dialog popAddPost ;
    ImageView popupPostImage, popupAddBtn;
    ImageView popupUserImage, popupImageIcon;
    EditText popupTitle, popupDescription;
    TextView popupImageInstructions;
    ProgressBar popupClickProgress, DiscussionsProgress;
    private Uri pickedImgUri = null;
    byte[] fileInBytes = null;

    RecyclerView postRecyclerView ;
    LinearLayoutManager mLayoutManager;
    DiscussionPostAdapter discussionPostAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference ;
    List<Post> postList;
    FloatingActionButton fab;
    RelativeLayout fabLayout;
    ImageButton discussionRules;
    Uri userPhoto;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        Toolbar toolbar = findViewById(R.id.discussion_toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        DiscussionsProgress = findViewById(R.id.progressBar_discussion);
        fab = findViewById(R.id.create_question);
        fabLayout = findViewById(R.id.discussion_fab_layout);


        if(mAuth.getCurrentUser()!= null) {
            userPhoto = currentUser.getPhotoUrl();

            // ini popup
            iniPopup();
            setupPopupImageClick();
            fab.setOnClickListener(view -> popAddPost.show());
        }
        else {
            fab.setVisibility(View.GONE);
            fabLayout.setVisibility(View.GONE);
        }

        /* fragment for term & conditions! */
        discussionRules = findViewById(R.id.bt_discussion_rules);
        discussionRules.setOnClickListener(v -> {
            DiscussionRulesSheetFragment sheetFragment = new DiscussionRulesSheetFragment();
            sheetFragment.show(getSupportFragmentManager(), sheetFragment.getTag());
        });

        postRecyclerView  = findViewById(R.id.postRV);
        mLayoutManager = new LinearLayoutManager(DiscussionActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        postRecyclerView.setLayoutManager(mLayoutManager);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");

        /* BottomBar Navigation */
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem1 -> {

            switch (menuItem1.getItemId()) {
                case R.id.action_home:
                    startActivity(new Intent(DiscussionActivity.this, HomeActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_my_courses:
                    startActivity(new Intent(DiscussionActivity.this, MyCoursesActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_search:
                    startActivity(new Intent(DiscussionActivity.this, SearchActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_forum:
                    Toast.makeText(DiscussionActivity.this, "You are already here", Toast.LENGTH_LONG).show();
                    bottomNavigationView.getMenu().getItem(3).setEnabled(false);
                    break;
            }
            return false;
        });


    }

    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(view -> {
            // here when image clicked we need to open the gallery
            // before we open the gallery we need to check if our app have the access to user files
            // we did this before in register activity I'm just going to copy the code to save time ...
            checkAndRequestForPermission();
            popupImageInstructions.setVisibility(View.GONE);
            popupImageIcon.setVisibility(View.GONE);
        });
    }


    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(DiscussionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DiscussionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(DiscussionActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(DiscussionActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PREQCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }



    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCode);
    }



    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCode && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostImage.setImageURI(pickedImgUri);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedImgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i chose 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            fileInBytes = baos.toByteArray();


        }
        else if(resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Image selection cancelled",Toast.LENGTH_LONG).show();
            popupImageInstructions.setVisibility(View.VISIBLE);
            popupImageIcon.setVisibility(View.VISIBLE);

        } else{
            Toast.makeText(this, "Failed To Select Image",Toast.LENGTH_LONG).show();
            popupImageInstructions.setVisibility(View.VISIBLE);
            popupImageIcon.setVisibility(View.VISIBLE);

        }
    }

    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_question);
        Objects.requireNonNull(popAddPost.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // ini popup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);
        popupImageIcon = popAddPost.findViewById(R.id.icon_popup_image);
        popupImageInstructions = popAddPost.findViewById(R.id.instr_popup_image);

        // load Current user profile photo

        Glide.with(DiscussionActivity.this)
                .load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.ic_user_place_holder)
                .circleCrop()
                .into(popupUserImage);


        // Add post click Listener

        popupAddBtn.setOnClickListener(view -> {

            popupAddBtn.setVisibility(View.INVISIBLE);
            popupClickProgress.setVisibility(View.VISIBLE);

            // we need to test all input fields (Title and description ) and post image

            if (!popupTitle.getText().toString().isEmpty()
                && !popupDescription.getText().toString().isEmpty()
                && pickedImgUri != null ) {

                //everything is okay no empty or null value
                // TODO Create Post Object and add it to firebase database
                // first we need to upload post Image
                // access firebase storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                final StorageReference imageFilePath = storageReference.child(Objects.requireNonNull(pickedImgUri.getLastPathSegment()));
                imageFilePath.putBytes(fileInBytes).addOnSuccessListener(taskSnapshot -> imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageDownloadLink = uri.toString();

                    // create post Object
                    if(userPhoto!=null) {
                        Post post = new Post(popupTitle.getText().toString(),
                                popupDescription.getText().toString(),
                                imageDownloadLink,
                                currentUser.getUid(),
                                currentUser.getDisplayName(),
                                Objects.requireNonNull(currentUser.getPhotoUrl()).toString());

                        // Add post to firebase database
                        addPost(post);
                    }else{
                        Post post = new Post(popupTitle.getText().toString(),
                                popupDescription.getText().toString(),
                                imageDownloadLink,
                                currentUser.getUid(),
                                currentUser.getDisplayName(),
                                null);

                        // Add post to firebase database
                        addPost(post);
                    }

                }).addOnFailureListener(e -> {
                    // something goes wrong uploading picture

                    showMessage(e.getMessage());
                    popupClickProgress.setVisibility(View.INVISIBLE);
                    popupAddBtn.setVisibility(View.VISIBLE);

                }));

            }
            else if(!popupTitle.getText().toString().isEmpty()
                    && !popupDescription.getText().toString().isEmpty()
                    && pickedImgUri == null ) {

                // create post Object
                if(userPhoto!=null) {
                    Post post = new Post(popupTitle.getText().toString(),
                            popupDescription.getText().toString(),
                            null,
                            currentUser.getUid(),
                            currentUser.getDisplayName(),
                            Objects.requireNonNull(currentUser.getPhotoUrl()).toString());

                    // Add post to firebase database
                    addPost(post);
                }
                else{
                    Post post = new Post(popupTitle.getText().toString(),
                            popupDescription.getText().toString(),
                            null,
                            currentUser.getUid(),
                            currentUser.getDisplayName(),
                            null);

                    // Add post to firebase database
                    addPost(post);
                }

            }
            else {
                showMessage("Please input question & description") ;
                popupAddBtn.setVisibility(View.VISIBLE);
                popupClickProgress.setVisibility(View.INVISIBLE);
            }

        });

    }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID and update post key
        String key = myRef.getKey();
        post.setPostKey(key);


        // add post data to firebase database

        myRef.setValue(post).addOnSuccessListener(aVoid -> {

            popupTitle.getText().clear();
            popupDescription.getText().clear();
            pickedImgUri = null;
            popupPostImage.setImageURI(null);
            popupPostImage.setImageResource(0);

            showMessage("Post Added successfully");
            popupClickProgress.setVisibility(View.INVISIBLE);
            popupAddBtn.setVisibility(View.VISIBLE);
            popAddPost.dismiss();
        });

    }


    private void showMessage(String message) {

        Toast.makeText(DiscussionActivity.this,message,Toast.LENGTH_LONG).show();

    }


    @Override
    public void onStart() {
        super.onStart();

        // Get List Posts from the database

        databaseReference.orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {

                    Post post = postsnap.getValue(Post.class);
                    postList.add(post) ;

                }
                discussionPostAdapter = new DiscussionPostAdapter(DiscussionActivity.this,postList);
                postRecyclerView.setAdapter(discussionPostAdapter);
                DiscussionsProgress.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiscussionActivity.this);
        alertDialogBuilder.setMessage("Are you sure you want to exit app ?");
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            finishAffinity();
        }).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel()).create().show();
    }


}
