package com.vatsal.voltstudy.create_course_section;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.tech_trainer_section.TechTrainerActivity;
import com.vatsal.voltstudy.models.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Form for Trainer to Create Course */

public class CreateCourseActivity extends AppCompatActivity {

    private static final int PREQCode = 2 ;
    private static final int REQUESTCode = 2 ;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    ImageView courseThumbnail, nextButton, iconCourseThumbnail;
    TextView instrCourseThumbnail;
    EditText courseTitle, courseSubCategory,courseDescription;
    AutoCompleteTextView courseLanguage;
    Spinner categorySpinner, courseTypeSpinner, coursePriceSpinner;
    String selectedCategory,selectedCourseType;
    String selectedPrice;
    ProgressBar createCourseProgress;
    private Uri pickedImgUri = null;
    private byte[] fileInBytes;
    Uri userPhoto;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,categoryReference,languageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create New Course");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff333745));


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userPhoto = currentUser.getPhotoUrl();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("courses");
        categoryReference = firebaseDatabase.getReference("categories");
        languageReference = firebaseDatabase.getReference("languages");

        courseThumbnail = findViewById(R.id.create_course_thumbnail);
        courseTitle = findViewById(R.id.edittext_course_name);
        courseSubCategory = findViewById(R.id.edittext_course_subcategory);
        courseDescription = findViewById(R.id.edittext_course_desc);
        courseLanguage = findViewById(R.id.edittext_course_language);
        iconCourseThumbnail = findViewById(R.id.icon_create_course_thumbnail);
        instrCourseThumbnail = findViewById(R.id.intsr_create_course_thumbnail);

        categorySpinner = findViewById(R.id.dropdown_course_category);
        coursePriceSpinner = findViewById(R.id.dropdown_course_price);
        courseTypeSpinner = findViewById(R.id.dropdown_course_type);

        nextButton = findViewById(R.id.next_to_course_content);
        createCourseProgress = findViewById(R.id.create_course_progress);

        //Course Category Spinner
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add(0,"Select Course Category");
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String category = Objects.requireNonNull(datas.child("category_name").getValue()).toString();
                    categoryList.add(category);
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateCourseActivity.this, android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                Log.e("Error", "Failed to read user", error.toException());
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                if (!adapter.getItemAtPosition(position).equals("Select Course Category")) {
                    selectedCategory = adapter.getItemAtPosition(position).toString();
                }else{
                    selectedCategory = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        //Course Language AutoComplete TextView
        ArrayList<String> languageList = new ArrayList<>();
        languageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    String languages = Objects.requireNonNull(datas.child("courses_languages").getValue()).toString();
                    languageList.add(languages);
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateCourseActivity.this, android.R.layout.simple_spinner_item, languageList);
                    courseLanguage.setThreshold(1);
                    courseLanguage.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                Log.e("Error", "Failed to read user", error.toException());
            }
        });


        //Course Type Spinner
        List<String> courseTypeArray = new ArrayList<>();
        courseTypeArray.add(0,"Choose Course Type");
        courseTypeArray.add("Video & Text");
        courseTypeArray.add("Video");
        courseTypeArray.add("Text");
        ArrayAdapter<String> courseTypeAdapter = new ArrayAdapter<>(CreateCourseActivity.this,
                android.R.layout.simple_spinner_item, courseTypeArray);
        courseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseTypeSpinner.setAdapter(courseTypeAdapter);
        courseTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                if (!adapter.getItemAtPosition(position).equals("Choose Course Type")) {
                    selectedCourseType = adapter.getItemAtPosition(position).toString();
                }
                else{
                    selectedCourseType = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //Course Price Spinner
        List<String> coursePriceArray = new ArrayList<>();
        coursePriceArray.add(0,"Choose Course Price");
        coursePriceArray.add("499");
        coursePriceArray.add("699");
        coursePriceArray.add("999");
        coursePriceArray.add("1499");
        coursePriceArray.add("1999");
        coursePriceArray.add("2499");
        coursePriceArray.add("2999");
        coursePriceArray.add("3499");
        coursePriceArray.add("3999");
        coursePriceArray.add("4499");
        coursePriceArray.add("4999");
        coursePriceArray.add("5499");
        coursePriceArray.add("5999");
        ArrayAdapter<String> coursePriceAdapter = new ArrayAdapter<>(CreateCourseActivity.this,
                android.R.layout.simple_spinner_item, coursePriceArray);
        coursePriceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursePriceSpinner.setAdapter(coursePriceAdapter);
        coursePriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                if (!adapter.getItemAtPosition(position).equals("Choose Course Price")) {
                    selectedPrice = adapter.getItemAtPosition(position).toString();
                }
                else{
                    selectedPrice = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        setupCourseImageClick();


        // Add post click Listener
        nextButton.setOnClickListener(view -> {

            nextButton.setVisibility(View.INVISIBLE);
            createCourseProgress.setVisibility(View.VISIBLE);

            // we need to test all input fields (Title and description ) and post image

            if (!courseTitle.getText().toString().isEmpty()
                    && !courseSubCategory.getText().toString().isEmpty()
                    && !courseDescription.getText().toString().isEmpty()
                    && !courseLanguage.getText().toString().isEmpty()
                    && selectedCourseType != null
                    && selectedCategory != null
                    && selectedPrice != null
                    && pickedImgUri != null ) {

                //everything is okay no empty or null value
                // first we need to upload post Image
                // access firebase storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("course_thumbnails");
                final StorageReference imageFilePath = storageReference.child(Objects.requireNonNull(pickedImgUri.getLastPathSegment()));
                imageFilePath.putBytes(fileInBytes).addOnSuccessListener(taskSnapshot -> imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageDownloadLink = uri.toString();
                    Course course;
                    if(userPhoto!=null) {

                        // create post Object
                        course = new Course(
                                currentUser.getDisplayName(),
                                currentUser.getUid(),
                                Objects.requireNonNull(currentUser.getPhotoUrl()).toString(),
                                courseDescription.getText().toString(),
                                courseLanguage.getText().toString().toUpperCase().trim(),
                                courseTitle.getText().toString().toUpperCase().trim(),
                                selectedCategory,
                                courseSubCategory.getText().toString(),
                                imageDownloadLink,
                                selectedCourseType,
                                Double.parseDouble(selectedPrice));
                    }
                    else {
                        // create post Object
                        course = new Course(
                                currentUser.getDisplayName(),
                                currentUser.getUid(),
                                null,
                                courseDescription.getText().toString(),
                                courseLanguage.getText().toString().toUpperCase().trim(),
                                courseTitle.getText().toString().toUpperCase().trim(),
                                selectedCategory,
                                courseSubCategory.getText().toString(),
                                imageDownloadLink,
                                selectedCourseType,
                                Double.parseDouble(selectedPrice));
                    }

                    // Add post to firebase database
                    addCourse(course);


                }).addOnFailureListener(e -> {
                    // something goes wrong uploading picture

                    showMessage(e.getMessage());
                    createCourseProgress.setVisibility(View.INVISIBLE);
                    nextButton.setVisibility(View.VISIBLE);

                }));

            }
            else {
                Toasty.warning(this, "Please Input All Fields & Select Image",Toasty.LENGTH_LONG).show();
                nextButton.setVisibility(View.VISIBLE);
                createCourseProgress.setVisibility(View.INVISIBLE);
            }

        });


    }

    private void setupCourseImageClick() {
        courseThumbnail.setOnClickListener(view -> {
            // here when image clicked we need to open the gallery
            // before we open the gallery we need to check if our app have the access to user files
            // we did this before in register activity I'm just going to copy the code to save time ...
            checkAndRequestForPermission();

        });
    }


    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(CreateCourseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateCourseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toasty.info(CreateCourseActivity.this,"Please accept for required permission",Toasty.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(CreateCourseActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PREQCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }



    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCode);
        iconCourseThumbnail.setVisibility(View.GONE);
        instrCourseThumbnail.setVisibility(View.GONE);
    }



    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCode && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            courseThumbnail.setImageURI(pickedImgUri);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedImgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i chose 25)
            if (bmp != null) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            }
            fileInBytes = baos.toByteArray();
        }
        else if(resultCode == RESULT_CANCELED){
            Toasty.warning(this, "Image selection cancelled",Toasty.LENGTH_LONG).show();
            iconCourseThumbnail.setVisibility(View.VISIBLE);
            instrCourseThumbnail.setVisibility(View.VISIBLE);

        }else {
            Toasty.error(this, "Failed To Select Image",Toasty.LENGTH_LONG).show();
            iconCourseThumbnail.setVisibility(View.VISIBLE);
            instrCourseThumbnail.setVisibility(View.VISIBLE);

        }
    }

    private void addCourse(Course course) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("courses").push();

        // get post unique ID and upadte post key
        String key = myRef.getKey();
        course.setCourse_code(key);

        String promoCode = "0";
        course.setPromocode(promoCode);

        //Transfer Course Code to Next Activity
        // add post data to firebase database
        myRef.setValue(course).addOnSuccessListener(aVoid -> {
            showMessage("Course Added successfully");

            createCourseProgress.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.VISIBLE);

            Intent AddCourseVideoIntent = new Intent(CreateCourseActivity.this, AddVideoActivity.class);
            AddCourseVideoIntent.putExtra("courseTitle", courseTitle.getText().toString());
            AddCourseVideoIntent.putExtra("courseCode",key);
            startActivity(AddCourseVideoIntent);
            finish();

        });

    }


    private void showMessage(String message) {

        Toast.makeText(CreateCourseActivity.this,message,Toast.LENGTH_LONG).show();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, TechTrainerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, TechTrainerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
