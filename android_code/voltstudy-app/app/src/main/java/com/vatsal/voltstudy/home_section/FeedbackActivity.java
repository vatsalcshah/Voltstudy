package com.vatsal.voltstudy.home_section;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.vatsal.voltstudy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Submit Feedback to Database */

public class FeedbackActivity extends AppCompatActivity {

    EditText feedbackEditText;
    Button submitFeedback;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        feedbackEditText = findViewById(R.id.edittext_feedback);
        submitFeedback = findViewById(R.id.btn_submit_feedback);

        submitFeedback.setOnClickListener(v -> {

            if(!feedbackEditText.getText().toString().isEmpty()){

                DatabaseReference feedbackRef = databaseReference.child("feedback").child(currentUser.getUid());

                Map<String,Object> taskMap = new HashMap<>();
                taskMap.put("name", currentUser.getDisplayName());
                taskMap.put("email", currentUser.getEmail());
                taskMap.put("feedback", feedbackEditText.getText().toString());

                feedbackRef.updateChildren(taskMap)
                        .addOnSuccessListener(aVoid -> {
                            Toasty.success(FeedbackActivity.this, "Feedback Submitted Successfully", Toasty.LENGTH_LONG).show();
                            feedbackEditText.getText().clear();
                        })
                        .addOnFailureListener(e -> {
                            Toasty.error(FeedbackActivity.this, "Something went wrong", Toasty.LENGTH_LONG).show();
                        });

            }
            else{
                Toasty.error(FeedbackActivity.this, "Please Input Feedback", Toasty.LENGTH_LONG).show();
            }

        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
