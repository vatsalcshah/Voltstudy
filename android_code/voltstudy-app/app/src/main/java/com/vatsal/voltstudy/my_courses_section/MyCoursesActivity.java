package com.vatsal.voltstudy.my_courses_section;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.discussion_section.DiscussionActivity;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.vatsal.voltstudy.search_section.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

/** Fragments for Purchased Courses & Saved Courses */

public class MyCoursesActivity extends AppCompatActivity {

    Toolbar toolbar,toolbarTab;
    ViewPager viewPager;
    TabLayout tabLayout;

    MyCoursesPageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        toolbar = findViewById(R.id.my_courses_toolbar);
        toolbarTab = findViewById(R.id.my_courses_toolbarTab);
        viewPager = findViewById(R.id.my_courses_viewpager);
        tabLayout = findViewById(R.id.my_courses_tabLayout);

        setSupportActionBar(toolbar);

        pageAdapter = new MyCoursesPageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new PurchasedCoursesFragment(), "Purchased Courses");
        pageAdapter.addFragment(new SavedCoursesFragment(), "Saved Courses");

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        /* BottomBar Navigation */
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem1 -> {

            switch (menuItem1.getItemId()) {
                case R.id.action_home:
                    startActivity(new Intent(MyCoursesActivity.this, HomeActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_my_courses:
                    Toast.makeText(MyCoursesActivity.this, "You are already here", Toast.LENGTH_LONG).show();
                    bottomNavigationView.getMenu().getItem(1).setEnabled(false);
                    break;
                case R.id.action_search:
                    startActivity(new Intent(MyCoursesActivity.this, SearchActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_forum:
                    startActivity(new Intent(MyCoursesActivity.this, DiscussionActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
            }
            return false;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCoursesActivity.this);
        alertDialogBuilder.setMessage("Are you sure you want to exit app ?");
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            finishAffinity();
        }).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel()).create().show();
    }
}
