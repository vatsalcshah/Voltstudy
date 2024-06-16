package com.vatsal.voltstudy.splash_section;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.auth_controller.LoginActivity;
import com.varunest.loader.TheGlowingLoader;

/** Page Switch Animation */

public class SplashActivity extends AppCompatActivity {

    private TheGlowingLoader splashProgressBar;

    /** Called when the activity is first created. */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashProgressBar = findViewById(R.id.avi);
        splashProgressBar.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.alpha);
        anim.reset();
        ConstraintLayout l = findViewById(R.id.lin_lay);
        l.clearAnimation();
        anim=AnimationUtils.loadAnimation(this,R.anim.translate);
        anim.reset();
        ImageView iv=  findViewById(R.id.imageView2);
        iv.clearAnimation();
        iv.startAnimation(anim);
        //fillableLoaders.setProgress(40,1000);
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/


        /* Duration of wait **/
        int SPLASH_DISPLAY_LENGTH = 2500;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                SplashActivity.this.startActivity(mainIntent);
                splashProgressBar.setVisibility(View.INVISIBLE);
                SplashActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
