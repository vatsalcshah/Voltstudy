package com.vatsal.voltstudy.tech_trainer_section;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.models.Course;
import com.vatsal.voltstudy.viewholders.InstructorCoursesListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/** List Adapter For Instructor Courses*/

public class InstructorCoursesList extends AppCompatActivity {

    private RecyclerView mRecyclerViewCourse;
    private InstructorCoursesListAdapter courseAdapter;

    private ProgressBar progressBar;
    private LinearLayout notFoundLayout;

    // Model Variables
    private ArrayList<String> courseName = new ArrayList<>();
    private ArrayList<String> courseCode = new ArrayList<>();
    private ArrayList<String> courseAuthor = new ArrayList<>();
    private ArrayList<String> courseLanguage = new ArrayList<>();
    private ArrayList<String> courseCategory = new ArrayList<>();
    private ArrayList<String> courseSubCategory = new ArrayList<>();
    private ArrayList<String> courseImageUrl = new ArrayList<>();
    private ArrayList<String> courseType = new ArrayList<>();
    private ArrayList<String> promoCode = new ArrayList<>();
    private ArrayList<Double> coursePrice = new ArrayList<>();

    // Variables
    private String varInstructorID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_courses_list);
        getDataOverIntent();
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Your Courses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notFoundLayout = findViewById(R.id.empty_instructor_list_layout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference();

        mRecyclerViewCourse = findViewById(R.id.rvInstructorCoursesList);
        courseAdapter = new InstructorCoursesListAdapter(this, courseName, courseAuthor, courseLanguage, courseCode, courseCategory, courseSubCategory, courseImageUrl, courseType, coursePrice, promoCode);

        initializeWidgets();
        getFirebaseCourseData();


    }
    private void getDataOverIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            varInstructorID = Objects.requireNonNull(intent.getExtras()).getString("instructorID");
        }
    }

    private void initializeWidgets() {
        progressBar = findViewById(R.id.progress_instructor_list);
        progressBar.setVisibility(View.VISIBLE);
    }


    /* Getting Data From Firebase for Courses */
    private void getFirebaseCourseData() {

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_courses)).orderByChild("author_id").equalTo(varInstructorID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            courseName.clear();
                            courseAuthor.clear();
                            courseLanguage.clear();
                            courseCategory.clear();
                            courseSubCategory.clear();
                            courseCode.clear();
                            courseImageUrl.clear();
                            courseType.clear();
                            promoCode.clear();
                            coursePrice.clear();

                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                fetchCourseData(singleSnapshot);
                            }
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            notFoundLayout.setVisibility(View.VISIBLE);
                            mRecyclerViewCourse.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    private void fetchCourseData(DataSnapshot singleSnapshot) {
        Course course = singleSnapshot.getValue(Course.class);

        if (course != null) {
            courseName.add(course.getName());
            courseAuthor.add(course.getAuthor());
            courseLanguage.add(course.getLanguage());
            courseCode.add(course.getCourse_code());
            courseCategory.add(course.getCategory());
            courseSubCategory.add(course.getSub_category());
            courseImageUrl.add(course.getImageUrl());
            courseType.add(course.getCourse_type());
            promoCode.add(course.getPromocode());
            coursePrice.add(course.getCourse_price());
            initializeCourse();
        }
        progressBar.setVisibility(View.GONE);

    }
    private void initializeCourse() {

        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewCourse.setLayoutManager(manager1);

        mRecyclerViewCourse.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, TechTrainerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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