package com.vatsal.voltstudy.tech_trainer_section;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.splash_section.SplashAnimation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/** TechTrainer Dashboard */

public class TechTrainerActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private FirebaseAuth auth;
    LinearLayout techTrainerLayout;
    Button createCourseButton, yourCoursesButton;
    CircleImageView techTrainerImg;
    TextView trainerName, trainerTotalCourses, trainerPurchasedCourses, trainerPendingRevenue, trainerEmail, trainerPhone, trainerField, trainerBdate;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_trainer);

        auth = FirebaseAuth.getInstance();

        currentUser = auth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        techTrainerLayout = findViewById(R.id.tech_trainer_layout);
        checkForAdmin();

    }

    public void checkForAdmin() {
        myRef.child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Objects.requireNonNull(auth.getUid()))
                        .exists() && Objects.requireNonNull(dataSnapshot.child(auth.getUid())
                        .getValue()).toString().equals("true")) {

                    techTrainerLayout.setVisibility(View.VISIBLE);

                    createCourseButton = findViewById(R.id.btn_create_course);
                    createCourseButton.setOnClickListener(v -> {
                        startActivity(new Intent(TechTrainerActivity.this, SplashAnimation.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    });

                    yourCoursesButton = findViewById(R.id.btn_courses_by_you);
                    yourCoursesButton.setOnClickListener(v -> {
                        Intent intent = new Intent(TechTrainerActivity.this, InstructorCoursesList.class);
                        intent.putExtra("instructorID", currentUser.getUid());
                        TechTrainerActivity.this.startActivity(intent);
                    });


                    techTrainerImg = findViewById(R.id.techTrainR_img);
                    trainerName = findViewById(R.id.tech_trainer_name);
                    trainerTotalCourses = findViewById(R.id.tv_instructor_total_courses);
                    trainerPurchasedCourses = findViewById(R.id.tv_instructor_purchased_courses);
                    trainerPendingRevenue = findViewById(R.id.tv_instructor_pending_revenue);
                    trainerEmail = findViewById(R.id.tv_instructor_email_display);
                    trainerPhone = findViewById(R.id.tv_instructor_phone_display);
                    trainerField = findViewById(R.id.tv_instructor_field_display);
                    trainerBdate = findViewById(R.id.tv_instructor_birth_display);

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference instructorRef = rootRef.child("instructors").child(currentUser.getUid());

                    instructorRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerName.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    instructorRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerEmail.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    instructorRef.child("mobile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerPhone.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    instructorRef.child("fieldOfStudy").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerField.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    instructorRef.child("birthdate").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerBdate.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    instructorRef.child("total_courses").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerTotalCourses.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    instructorRef.child("total_purchased_courses").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerPurchasedCourses.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    instructorRef.child("monthly_revenue").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            trainerPendingRevenue.setText(Objects.requireNonNull(snapshot.getValue()).toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                    instructorRef.child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Glide.with(TechTrainerActivity.this)
                                    .load(snapshot.getValue())
                                    .placeholder(R.drawable.ic_user_place_holder)
                                    .circleCrop()
                                    .into(techTrainerImg);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                }
                else{
                    if(!isFinishing()) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TechTrainerActivity.this);
                        alertDialogBuilder.setMessage("You are not a Trainer. Do you want to join us as a Trainer ?").setTitle("Become A Trainer");
                        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                            startActivity(new Intent(TechTrainerActivity.this, InstructorEmailActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }).setNegativeButton("No, Go Back", (dialog, id) -> finish()).create().show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
