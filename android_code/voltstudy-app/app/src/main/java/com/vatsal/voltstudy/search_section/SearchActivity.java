package com.vatsal.voltstudy.search_section;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vatsal.voltstudy.models.Languages;
import com.vatsal.voltstudy.my_courses_section.MyCoursesActivity;
import com.vatsal.voltstudy.viewholders.LanguagesAdapter;
import com.vatsal.voltstudy.viewholders.VerticalListAdapter;
import com.vatsal.voltstudy.discussion_section.DiscussionActivity;
import com.vatsal.voltstudy.models.Course;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.util.Objects.requireNonNull;

/** Search Activity for finding courses */

public class SearchActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageView mSearchButton;
    private RecyclerView mRecyclerViewCourse;
    private VerticalListAdapter courseAdapter;

    private ProgressBar progressBar;

    // Model Variables
    private ArrayList<String> courseName = new ArrayList<>();
    private ArrayList<String> courseCode = new ArrayList<>();
    private ArrayList<String> courseDesc = new ArrayList<>();
    private ArrayList<String> courseAuthor = new ArrayList<>();
    private ArrayList<String> courseLanguage = new ArrayList<>();
    private ArrayList<String> courseCategory = new ArrayList<>();
    private ArrayList<String> courseSubCategory = new ArrayList<>();
    private ArrayList<String> courseImageUrl = new ArrayList<>();
    private ArrayList<String> courseType = new ArrayList<>();
    private ArrayList<Double> coursePrice = new ArrayList<>();

    private RecyclerView mRecyclerViewCourseLang;
    private LanguagesAdapter courseLangAdapter;

    private ArrayList<String> courseLangName = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar =  findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);


        mRecyclerViewCourse = findViewById(R.id.rvSearchCourses);
        courseAdapter = new VerticalListAdapter(this, courseName, courseAuthor, courseLanguage, courseCode, courseDesc, courseCategory, courseSubCategory, courseImageUrl, courseType, coursePrice);

        getFirebaseCourseData("");
        getFirebaseCourseLangData();


        mRecyclerViewCourseLang = findViewById(R.id.rvCourseLanguages);
        courseLangAdapter = new LanguagesAdapter(this, courseLangName);

        mSearchField = findViewById(R.id.editTextSearch);
        mSearchButton = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progress_search_activity);
        progressBar.setVisibility(View.GONE);

        mSearchButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String searchText = mSearchField.getText().toString().toUpperCase().trim();
            getFirebaseCourseData(searchText);

        });


        /* BottomBar For Navigation */
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem1 -> {

            switch (menuItem1.getItemId()) {
                case R.id.action_home:
                    startActivity(new Intent(SearchActivity.this, HomeActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_my_courses:
                    startActivity(new Intent(SearchActivity.this, MyCoursesActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_search:
                    Toast.makeText(SearchActivity.this, "You are already here", Toast.LENGTH_LONG).show();
                    bottomNavigationView.getMenu().getItem(2).setEnabled(false);
                    break;
                case R.id.action_forum:
                    startActivity(new Intent(SearchActivity.this, DiscussionActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
            }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchActivity.this);
            alertDialogBuilder.setMessage("Are you sure you want to exit app ?");
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                finishAffinity();
            }).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel()).create().show();
        }



    /* Getting Data From Firebase for Course Languages */
    private void getFirebaseCourseLangData() {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_node_course_lang))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        courseLangName.clear();

                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            fetchCourseLangData(singleSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void fetchCourseLangData(DataSnapshot singleSnapshot) {
        Languages languages = singleSnapshot.getValue(Languages.class);

        if (languages != null) {
            courseLangName.add(languages.getCourses_languages());
            initializeCourseLang();
        }


    }

    private void initializeCourseLang() {

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        mRecyclerViewCourseLang.setLayoutManager(layoutManager);


        mRecyclerViewCourseLang.setAdapter(courseLangAdapter);
        courseLangAdapter.notifyDataSetChanged();

    }


    /* Getting Data From Firebase for Courses */
    private void getFirebaseCourseData(String str) {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_courses)).orderByChild("name").startAt(str).endAt(str+"\uf8ff")
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
                            courseDesc.clear();
                            courseImageUrl.clear();
                            courseType.clear();
                            coursePrice.clear();

                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                fetchCourseData(singleSnapshot);
                            }
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }

                    @SuppressLint("ShowToast")
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SearchActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT);
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
            courseDesc.add(course.getDesc());
            courseCategory.add(course.getCategory());
            courseSubCategory.add(course.getSub_category());
            courseImageUrl.add(course.getImageUrl());
            courseType.add(course.getCourse_type());
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


}
