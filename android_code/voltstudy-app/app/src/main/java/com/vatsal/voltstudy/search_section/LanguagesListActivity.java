package com.vatsal.voltstudy.search_section;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.viewholders.VerticalListAdapter;
import com.vatsal.voltstudy.models.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/** Activity loaded when user selects language in HomeActivity */

public class LanguagesListActivity extends AppCompatActivity{

    private RecyclerView mRecyclerViewLang;
    private VerticalListAdapter courseAdapter;

    private ProgressBar progressBar;
    private LinearLayout notFoundLayout;

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


    // Variables
    private String varCourseLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang_list);
        getDataOverIntent();
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle(varCourseLanguage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        notFoundLayout = findViewById(R.id.empty_lang_list_layout);

        mRecyclerViewLang = findViewById(R.id.rvListLangActivity);
        courseAdapter = new VerticalListAdapter(this, courseName, courseAuthor, courseLanguage, courseCode, courseDesc, courseCategory, courseSubCategory, courseImageUrl, courseType, coursePrice);

        initializeWidgets();
        getFirebaseCourseData();

    }

    private void getDataOverIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            varCourseLanguage = Objects.requireNonNull(intent.getExtras()).getString("courseLangName");
        }
    }

    private void initializeWidgets() {
        progressBar = findViewById(R.id.progress_lang_list);
        progressBar.setVisibility(View.VISIBLE);
    }


    /* Getting Data From Firebase for Courses */
    private void getFirebaseCourseData() {

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_courses)).orderByChild("language").equalTo(varCourseLanguage)
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
                        else{
                            progressBar.setVisibility(View.GONE);
                            notFoundLayout.setVisibility(View.VISIBLE);
                            mRecyclerViewLang.setVisibility(View.GONE);
                            
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
        mRecyclerViewLang.setLayoutManager(manager1);

        mRecyclerViewLang.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
