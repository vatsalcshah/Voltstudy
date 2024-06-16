package com.vatsal.voltstudy.tech_trainer_section;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.vatsal.voltstudy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import es.dmoral.toasty.Toasty;

/** Instructor Email Verification */

public class InstructorEmailActivity extends AppCompatActivity {

    EditText etInstructorEmail;
    ImageView nextButton, backButton;
    ProgressBar instructorEmailProgress;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_email);

        mAuth= FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        etInstructorEmail = findViewById(R.id.edittext_instructor_email);
        backButton = findViewById(R.id.btn_instr_email_back);
        nextButton = findViewById(R.id.next_to_instructor_form);
        instructorEmailProgress = findViewById(R.id.instructor_email_progress);

        rootRef = FirebaseDatabase.getInstance().getReference();

        nextButton.setOnClickListener(v -> {

            instructorEmailProgress.setVisibility(View.VISIBLE);
            if(!etInstructorEmail.getText().toString().isEmpty())
            {
                if(etInstructorEmail.getText().toString().trim().equals(currentUser.getEmail())){

                    DatabaseReference instructorsRef = rootRef.child("instructors").child(currentUser.getUid()).child("email");
                    instructorsRef.setValue(etInstructorEmail.getText().toString());

                    instructorEmailProgress.setVisibility(View.GONE);
                    startActivity(new Intent(InstructorEmailActivity.this, InstructorFormActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    Toasty.success(InstructorEmailActivity.this, "Verification Successful", Toasty.LENGTH_LONG).show();

                }
                else { Toasty.error(InstructorEmailActivity.this, "Entered Email Doesn't Match Registered Email", Toasty.LENGTH_LONG).show();
                    instructorEmailProgress.setVisibility(View.GONE);
                }
            }
            else { Toasty.error(InstructorEmailActivity.this, "Please Enter Registered Email", Toasty.LENGTH_LONG).show();
                instructorEmailProgress.setVisibility(View.GONE);
            }

                });

        backButton.setOnClickListener(v -> {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });



            }
    }


