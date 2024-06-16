package com.vatsal.voltstudy.tech_trainer_section;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Instructor Details Forms */

public class InstructorFormActivity extends AppCompatActivity {

    EditText etInstructorBirthDate, etInstructorName, etInstructorMobile;
    CheckBox marketingCheckbox;
    Button btnNextToPortfolio;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    Uri UserPhotoUrl;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_form);
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Enter Your Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth= FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            UserPhotoUrl = currentUser.getPhotoUrl();
        }

        DialogFragment dialogFragment = new DatePickerDialogTheme();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        avLoadingIndicatorView = findViewById(R.id.become_instructor_loader);
        btnNextToPortfolio = findViewById(R.id.btn_next_to_portfolio);
        etInstructorName = findViewById(R.id.edittext_instructor_name);
        etInstructorMobile = findViewById(R.id.edittext_instructor_mobile);
        marketingCheckbox = findViewById(R.id.marketing_checkbox);

        etInstructorBirthDate = findViewById(R.id.edittext_instructor_birthdate);
        etInstructorBirthDate.setInputType(InputType.TYPE_NULL);
        etInstructorBirthDate.setOnClickListener(v -> {
            dialogFragment.show(getSupportFragmentManager(), "DateDialogTheme" );

        });

        btnNextToPortfolio.setOnClickListener(v -> {

            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.smoothToShow();

            if(!etInstructorName.getText().toString().isEmpty()
                    && !etInstructorMobile.getText().toString().isEmpty()
                    && !etInstructorBirthDate.getText().toString().isEmpty()){


                DatabaseReference instructorReference = databaseReference.child("instructors").child(currentUser.getUid());
                instructorReference.child("name").setValue(etInstructorName.getText().toString());
                instructorReference.child("birthdate").setValue(etInstructorBirthDate.getText().toString());
                instructorReference.child("mobile").setValue(etInstructorMobile.getText().toString());
                instructorReference.child("Marketing").setValue(marketingCheckbox.isChecked());

                if(UserPhotoUrl != null) {
                    instructorReference.child("profileImage").setValue(currentUser.getPhotoUrl().toString());
                }
                else {
                    instructorReference.child("profileImage").setValue(null);
                }

                avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, InstructorPortfolioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
            else{
                Toasty.error(InstructorFormActivity.this, "Please Input All Fields", Toasty.LENGTH_LONG).show();
                avLoadingIndicatorView.setVisibility(View.INVISIBLE);
            }

        });



    }

    public static class DatePickerDialogTheme extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            EditText etInstructorBirthDate = getActivity().findViewById(R.id.edittext_instructor_birthdate);
            etInstructorBirthDate.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
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
