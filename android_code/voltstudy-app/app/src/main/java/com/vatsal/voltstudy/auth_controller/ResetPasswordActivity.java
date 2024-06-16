package com.vatsal.voltstudy.auth_controller;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import es.dmoral.toasty.Toasty;

/** Reset Password Activity */

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private FirebaseAuth auth;
    private AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail =  findViewById(R.id.email);
        Button btnReset = findViewById(R.id.btn_reset_password);
        Button btnBack = findViewById(R.id.btn_back);
        avLoadingIndicatorView = findViewById(R.id.loader1);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> finish());

        btnReset.setOnClickListener(v -> {

            String email = inputEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toasty.info(getApplication(),"Enter your registered email id",
                        Toasty.LENGTH_SHORT).show();
                return;
            }

            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.show();
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toasty.success(ResetPasswordActivity.this,"We have sent you instructions to reset your password!",
                                    Toasty.LENGTH_SHORT).show();
                        } else {
                            Toasty.success(ResetPasswordActivity.this,"Failed to send reset email!",
                                    Toasty.LENGTH_SHORT).show();
                        }

                        avLoadingIndicatorView.setVisibility(View.GONE);
                        avLoadingIndicatorView.hide();
                    });
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}