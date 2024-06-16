package com.vatsal.voltstudy.home_section;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vatsal.voltstudy.tech_trainer_section.TechTrainerActivity;
import com.vatsal.voltstudy.my_courses_section.MyCoursesActivity;
import com.vatsal.voltstudy.viewholders.CategoriesAdapter;
import com.vatsal.voltstudy.viewholders.CourseAdapter;
import com.vatsal.voltstudy.auth_controller.ManageDetails;
import com.vatsal.voltstudy.discussion_section.DiscussionActivity;
import com.vatsal.voltstudy.models.Categories;
import com.vatsal.voltstudy.models.Course;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.auth_controller.LoginActivity;
import com.vatsal.voltstudy.search_section.SearchActivity;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


/** HomePage Activity displays the courses and other features in feed */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    FirebaseUser currentUser;


    private ImageView imgBannerAd;
    private ProgressBar progressBar;
    private TextView titleCourses;
    private TextView titleCategories;

    private RecyclerView mRecyclerViewCourse;
    private CourseAdapter courseAdapter;


    private RecyclerView mRecyclerViewCategories;
    private CategoriesAdapter categoriesAdapter;


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
    private ArrayList<String> courseAuthorID = new ArrayList<>();


    private ArrayList<String> courseCategoriesList = new ArrayList<>();

    NavigationView navigationView;
    View headerView;
    ImageView navUserPhoto;
    TextView navUsername;
    TextView navUserMail;


    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();


        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);


        navUserPhoto = headerView.findViewById(R.id.nav_user_photo);
        navUsername = headerView.findViewById(R.id.nav_username);
        navUserMail = headerView.findViewById(R.id.nav_user_mail);


        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            updateNavHeader();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        imgBannerAd = findViewById(R.id.img_ad_banner);
        titleCourses = findViewById(R.id.title_latest_courses);
        titleCategories = findViewById(R.id.title_categories);

        mRecyclerViewCourse = findViewById(R.id.rvCourseList);
        courseAdapter = new CourseAdapter(this, courseName, courseAuthor, courseLanguage, courseCode, courseDesc, courseCategory, courseSubCategory, courseImageUrl, courseType, coursePrice, courseAuthorID);


        mRecyclerViewCategories = findViewById(R.id.rvCourseCategories);
        categoriesAdapter = new CategoriesAdapter(this, courseCategoriesList);


        initializeWidgets();

        getFirebaseCourseData();
        getFirebaseCategoriesData();

        getBannerAd();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        /* BottomNavigation Bar */
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        Menu Bmenu = bottomNavigationView.getMenu();
        MenuItem menuItem = Bmenu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem1 -> {

            switch (menuItem1.getItemId()) {
                case R.id.action_home:
                    Toast.makeText(HomeActivity.this, "You are already here", Toast.LENGTH_LONG).show();
                    bottomNavigationView.getMenu().getItem(0).setEnabled(false);
                    break;
                case R.id.action_my_courses:
                    startActivity(new Intent(HomeActivity.this, MyCoursesActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_search:
                    startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
                case R.id.action_forum:
                    startActivity(new Intent(HomeActivity.this, DiscussionActivity.class));
                    this.overridePendingTransition(0, 0);
                    break;
            }
            return false;
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
            alertDialogBuilder.setMessage("Are you sure you want to exit app ?");
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> finishAffinity()).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel()).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (auth.getCurrentUser() != null) {
            navigationView.getMenu().findItem(R.id.group_account).setVisible(true);
            navigationView.getMenu().findItem(R.id.group_extras).setVisible(true);
            navigationView.getMenu().findItem(R.id.group_instructor).setVisible(true);


        } else {
            navigationView.getMenu().findItem(R.id.nav_new_user).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage_account) {
            if (isNetworkAvailable(HomeActivity.this)) {
                startActivity(new Intent(HomeActivity.this, ManageDetails.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else
                alertNoConnection();
        } else if (id == R.id.nav_new_user) {
            if (isNetworkAvailable(HomeActivity.this)) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else
                alertNoConnection();
        } else if (id == R.id.nav_instructor_account) {
            if (isNetworkAvailable(HomeActivity.this)) {
                startActivity(new Intent(HomeActivity.this, TechTrainerActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else
                alertNoConnection();
        } else if (id == R.id.nav_share_app) {
            shareApp(HomeActivity.this);
        } else if (id == R.id.nav_rate_us) {
            launchMarket();
        } else if (id == R.id.nav_feedback) {
            if (isNetworkAvailable(HomeActivity.this)) {
                startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else
                alertNoConnection();
        }
        else if (id == R.id.nav_faq) {
            if (isNetworkAvailable(HomeActivity.this)) {
                startActivity(new Intent(HomeActivity.this, FaqActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else
                alertNoConnection();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void shareApp(Context context) {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toasty.error(this, "Could Not Launch Market", Toasty.LENGTH_LONG).show();
        }
    }


    /*method to handle network connection**/
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void alertNoConnection() {

        /*final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.drawable.nowifi);
        builder.setCancelable(true);
        builder.setTitle("No Connection Available!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();*/

        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(dialogInterface -> {
            //nothing;
        });

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.nowifi);

        /*imageView.getLayoutParams().height = 100;
        imageView.getLayoutParams().width = 100;
        imageView.requestLayout();
        imageView.setImage(R.drawable.nowifi);*/

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                400,
                400));
        builder.show();
    }


    private void initializeWidgets() {
        titleCourses.setVisibility(View.INVISIBLE);
        titleCategories.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBar_home);
        progressBar.setVisibility(View.VISIBLE);
    }


    /* Getting Data From Firebase for Courses */
    private void getFirebaseCourseData() {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_courses))
                .addValueEventListener(new ValueEventListener() {
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
                            courseAuthorID.clear();

                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                fetchCourseData(singleSnapshot);
                            }
                        } else {
                            titleCourses.setVisibility(View.GONE);
                            mRecyclerViewCourse.setVisibility(View.GONE);

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
            courseAuthorID.add(course.getAuthor_id());
            initializeCourse();
            titleCourses.setVisibility(View.VISIBLE);
        }
    }

    private void initializeCourse() {

        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewCourse.setLayoutManager(manager1);

        mRecyclerViewCourse.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
    }


    /* Getting Data From Firebase for Categories */
    private void getFirebaseCategoriesData() {
        FirebaseDatabase.getInstance().getReference().child("categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        courseCategoriesList.clear();

                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            fetchCategoriesData(singleSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void fetchCategoriesData(DataSnapshot singleSnapshot) {
        Categories categories = singleSnapshot.getValue(Categories.class);

        if (categories != null) {
            courseCategoriesList.add(categories.getCategory_name());
            initializeCategories();
        }

        progressBar.setVisibility(View.GONE);
        titleCategories.setVisibility(View.VISIBLE);

    }

    private void initializeCategories() {

        LinearLayoutManager manager3 = new LinearLayoutManager(this);
        manager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewCategories.setLayoutManager(manager3);

        mRecyclerViewCategories.setAdapter(categoriesAdapter);
        categoriesAdapter.notifyDataSetChanged();
    }


    /* Getting Banner Ad */
    private void getBannerAd() {
        FirebaseDatabase.getInstance().getReference().child("banner_ad")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            String imgBannerAdUrl = singleSnapshot.getValue(String.class);

                            Glide.with(getApplicationContext())
                                    .load(imgBannerAdUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imgBannerAd);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    public void updateNavHeader() {

        navUserPhoto.setVisibility(View.VISIBLE);
        navUserMail.setVisibility(View.VISIBLE);
        navUsername.setVisibility(View.VISIBLE);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());

        // now we will use Glide to load user image
        // first we need to import the library

        Glide.with(getApplicationContext())
                .load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.ic_user_place_holder)
                .circleCrop()
                .into(navUserPhoto);

    }


}