package com.vatsal.voltstudy.tech_trainer_section;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;


import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Instructor Portfolio Form */

public class InstructorPortfolioActivity extends AppCompatActivity {

    EditText etInstructorSchoolName, etInstructorCollegeName, etInstructorFieldOfStudy, etInstructorCurrentPosition, etInstructorDegree;
    Button btnBecomeTechInstructor;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private Double doubleZeroValue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_portfolio);
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Enter Academic Background");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        
        databaseReference = FirebaseDatabase.getInstance().getReference();

        avLoadingIndicatorView = findViewById(R.id.portfolio_progress_bar);
        btnBecomeTechInstructor = findViewById(R.id.btn_become_instructor_form);
        
        etInstructorSchoolName = findViewById(R.id.edittext_portfolio_schoolName);
        etInstructorCollegeName = findViewById(R.id.edittext_portfolio_collegeName);
        etInstructorFieldOfStudy = findViewById(R.id.edittext_portfolio_fieldOfStudy);
        etInstructorCurrentPosition = findViewById(R.id.edittext_portfolio_currentPosition);
        etInstructorDegree = findViewById(R.id.edittext_portfolio_degree);


        btnBecomeTechInstructor.setOnClickListener(v -> {

            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.smoothToShow();

            if(!etInstructorSchoolName.getText().toString().isEmpty()
                    && !etInstructorCollegeName.getText().toString().isEmpty()
                    && !etInstructorFieldOfStudy.getText().toString().isEmpty()
                    && !etInstructorCurrentPosition.getText().toString().isEmpty()
                    && !etInstructorDegree.getText().toString().isEmpty()){

                DatabaseReference instructorReference = databaseReference.child("instructors").child(currentUser.getUid());

                instructorReference.child("schoolName").setValue(etInstructorSchoolName.getText().toString());
                instructorReference.child("collegeName").setValue(etInstructorCollegeName.getText().toString());
                instructorReference.child("fieldOfStudy").setValue(etInstructorFieldOfStudy.getText().toString());
                instructorReference.child("currentPosition").setValue(etInstructorCurrentPosition.getText().toString());
                instructorReference.child("degree").setValue(etInstructorDegree.getText().toString());
                instructorReference.child("monthly_revenue").setValue(doubleZeroValue);
                instructorReference.child("total_courses").setValue(doubleZeroValue);
                instructorReference.child("total_purchased_courses").setValue(doubleZeroValue);

                            DatabaseReference adminRef = databaseReference.child("admins").child(currentUser.getUid());
                            adminRef.setValue(true).addOnSuccessListener(aVoid1 -> {

                                Toasty.success(this, "You are now a Tech Trainer", Toasty.LENGTH_LONG).show();

                                avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }).addOnFailureListener(e -> {
                                Toasty.error(InstructorPortfolioActivity.this, "Something went wrong", Toasty.LENGTH_LONG).show();
                                avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                            });



            }
            else{
                Toasty.error(InstructorPortfolioActivity.this, "Please Input All Fields", Toasty.LENGTH_LONG).show();
                avLoadingIndicatorView.setVisibility(View.INVISIBLE);
            }

        });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
