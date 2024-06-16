package com.vatsal.voltstudy.auth_controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vatsal.voltstudy.home_section.HomeActivity;
import com.vatsal.voltstudy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Login Activity */

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private AVLoadingIndicatorView avLoadingIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            if(auth.getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }else {
                Toasty.warning(getApplicationContext(), R.string.email_unverified, Toasty.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
            }
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail =  findViewById(R.id.login_input_email);
        inputPassword =  findViewById(R.id.login_input_password);
        avLoadingIndicatorView =  findViewById(R.id.login_loader);
        Button btnSignup = findViewById(R.id.login_btn_signup);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnReset = findViewById(R.id.login_btn_reset_password);
        Button btnSkip = findViewById(R.id.btn_skip_signin);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();



        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnReset.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toasty.warning(getApplicationContext(),"Enter email address!", Toasty.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toasty.warning(getApplicationContext(),"Enter password!", Toasty.LENGTH_SHORT).show();
                return;
            }

            avLoadingIndicatorView.setVisibility(View.VISIBLE);
            avLoadingIndicatorView.smoothToShow();

            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        //progressBar.setVisibility(View.GONE);
                        avLoadingIndicatorView.setVisibility(View.GONE);
                        avLoadingIndicatorView.smoothToHide();
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toasty.warning(LoginActivity.this,getString(R.string.auth_failed), Toasty.LENGTH_LONG).show();
                            }
                        } else {
                            if(Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                            else {
                                Toasty.error(LoginActivity.this,R.string.email_unverified, Toasty.LENGTH_SHORT).show();
                                Objects.requireNonNull(auth.getCurrentUser().sendEmailVerification());
                                Toasty.warning(LoginActivity.this,"We have resent email verification",Toasty.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();

                            }

                        }
                    });
        });

        btnSkip.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

}