package com.vatsal.voltstudy.home_section;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.models.PurchasedCourses;
import com.vatsal.voltstudy.models.SavedCourses;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Course Details and Purchase methods */

public class CourseDetailActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    FirebaseUser currentUser;

    // Variables
    private String courseName;
    private String courseAuthor;
    private String courseLanguage;
    private String courseCategory;
    private String courseSubCategory;
    private String courseImageUrl;
    private String courseCode;
    private String courseDesc;
    private String courseType;
    private Double coursePrice;
    private String courseAuthorID;
    private String productID;
    private EditText coupon;
    private String code;
    //Billing Processor
    BillingProcessor billingProcessor;

    //Buy Button
    Button buyButton;

    //Save Floating Button
    FloatingActionButton saveButton;

    //Purchased Course Codes
    private ArrayList<String> purchasedCourseCodes = new ArrayList<>();

    //Saved Course Codes
    private ArrayList<String> savedCourseCodes = new ArrayList<>();

    //coupon Dialogue
    Dialog myDialog;

    private Boolean isCoupon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        getDataOverIntent();

        auth= FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();


        Toolbar toolbar = findViewById(R.id.course_details_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(courseName);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        TextView textView_courseAuthor = findViewById(R.id.txt_course_details_author);
        textView_courseAuthor.setText(courseAuthor);
        TextView textView_courseLanguage = findViewById(R.id.txt_course_details_language);
        textView_courseLanguage.setText(courseLanguage);
        TextView textView_courseCategory = findViewById(R.id.txt_course_details_category);
        textView_courseCategory.setText(courseCategory);
        TextView textView_courseSubCategory = findViewById(R.id.txt_course_details_subcategory);
        textView_courseSubCategory.setText(courseSubCategory);

        TextView textView_courseDesc = findViewById(R.id.txt_course_details_desc);
        textView_courseDesc.setText(courseDesc);

        TextView textView_courseType = findViewById(R.id.txt_course_details_type);
        textView_courseType.setText(courseType);

        TextView textView_coursePrice = findViewById(R.id.txt_course_details_price);
        textView_coursePrice.setText(String.valueOf(coursePrice));


        ImageView imageView_course = findViewById(R.id.img_course_details);
        Glide.with(CourseDetailActivity.this)
                .load(courseImageUrl)
                .transforms(new CenterCrop())
                .override(1080,720)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView_course);

        myDialog = new Dialog(this);
        buyButton = findViewById(R.id.btn_buy_course);
        if(auth.getCurrentUser() != null) {
            getFirebasePurchasedCourseData();
            getPurchaseCode(coursePrice);

            String licenseKey = "YOUR_KEY";
            billingProcessor = new BillingProcessor(this, licenseKey, this);
            buyButton.setOnClickListener(view -> {
                    getCouponCode(courseCode);
                    ShowPopup(view);
            });
        }
        else {
            buyButton.setText("Please Login To Buy");
        }


        currentUser = auth.getCurrentUser();
        saveButton = findViewById(R.id.btn_save_course);
        if(currentUser != null){
            getFirebaseSavedCourseData();
        }
        else {
            saveButton.setVisibility(View.INVISIBLE);
        }
        saveButton.setOnClickListener(v -> {

            if (auth.getCurrentUser() != null) {
                SavedCourses savedCourses = new SavedCourses();
                savedCourses.setCourseCode(courseCode);
                savedCourses.setCourseName(courseName);
                savedCourses.setCourseAuthor(courseAuthor);
                savedCourses.setCourseLanguage(courseLanguage);
                savedCourses.setCourseCategory(courseCategory);
                savedCourses.setCourseSubCategory(courseSubCategory);
                savedCourses.setCourseImageUrl(courseImageUrl);
                savedCourses.setCourseType(courseType);
                savedCourses.setCourseDesc(courseDesc);
                savedCourses.setCoursePrice(coursePrice);

                DatabaseReference savedCourseRef = rootRef.child("savedCourses").child(auth.getUid()).push();
                String key = savedCourseRef.getKey();
                savedCourses.setSaveCourseKey(key);

                savedCourseRef.setValue(savedCourses).addOnSuccessListener(aVoid -> {
                    Toasty.success(this, "Course Saved Successfully", Toasty.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    Toasty.error(this, e.getMessage(), Toasty.LENGTH_LONG).show();
                });
            } else {
                Toasty.error(this, "Please Login To Save Courses", Toasty.LENGTH_LONG).show();
            }

        });

    }

    public void ShowPopup(View view) {
        TextView txtclose;
        Button btnApplyCoupon;
        myDialog.setContentView(R.layout.coupon_code_dialoguebox);
        txtclose = myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        btnApplyCoupon = myDialog.findViewById(R.id.btnEnter);
        txtclose.setOnClickListener(v -> {
            myDialog.dismiss();
            billingProcessor.initialize();
            billingProcessor.consumePurchase(productID);
            billingProcessor.purchase(CourseDetailActivity.this, productID);

        });
        btnApplyCoupon.setOnClickListener(view1 -> {
            coupon = myDialog.findViewById(R.id.coupon_input);
            String couponCode = coupon.getText().toString().trim();
            if(!couponCode.isEmpty()){
                if(couponCode.equals(code)){
                    Double discount = coursePrice * 0.10;
                    Double promoCoursePrise = coursePrice - discount;
                    coursePrice = promoCoursePrise;
                    productID = "voltstudy_" + promoCoursePrise + "0";
                    isCoupon = true;
                    billingProcessor.initialize();
                    billingProcessor.consumePurchase(productID);
                    billingProcessor.purchase(CourseDetailActivity.this, productID);
                    myDialog.dismiss();
                }
                else{
                    Toasty.warning(getApplicationContext(),"Invalid Coupon Code!", Toasty.LENGTH_SHORT).show();
                }}
            else{
                Toasty.warning(getApplicationContext(),"Enter Coupon Code!", Toasty.LENGTH_SHORT).show();
            }

        });

        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();


    }

    private void getDataOverIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            courseName = Objects.requireNonNull(intent.getExtras()).getString("courseName");
            courseAuthor = Objects.requireNonNull(intent.getExtras()).getString("courseAuthor");
            courseLanguage = Objects.requireNonNull(intent.getExtras()).getString("courseLanguage");
            courseCategory = Objects.requireNonNull(intent.getExtras()).getString("courseCategory");
            courseSubCategory = Objects.requireNonNull(intent.getExtras()).getString("courseSubCategory");
            courseImageUrl = Objects.requireNonNull(intent.getExtras()).getString("courseImageUrl");
            courseCode = Objects.requireNonNull(intent.getExtras()).getString("courseCode");
            courseDesc = Objects.requireNonNull(intent.getExtras()).getString("courseDesc");
            courseType = intent.getExtras().getString("courseType");
            coursePrice = intent.getExtras().getDouble("coursePrice");
            courseAuthorID = intent.getExtras().getString("courseAuthorID");
        }
    }

    private void getPurchaseCode(Double price){
        if(price == 499){
            productID = "voltstudy_499";
        }
        else if(price == 449.1){
            productID = "voltstudy_449.10";
        }
        else if(price == 699){
            productID = "voltstudy_699";
        }
        else if(price == 629.1){
            productID = "voltstudy_629.10";
        }
        else if(price == 999){
            productID = "voltstudy_999";
        }
        else if(price == 899.1){
            productID = "voltstudy_899.10";
        }
        else if(price == 1499){
            productID = "voltstudy_1499";
        }
        else if(price == 1349.1){
            productID = "voltstudy_1349.10";
        }
        else if(price == 1999){
            productID = "voltstudy_1999";
        }
        else if(price == 1799.1){
            productID = "voltstudy_1799.10";
        }
        else if(price == 2499){
            productID = "voltstudy_2499";
        }
        else if(price == 2249.1){
            productID = "voltstudy_2249.10";
        }
        else if(price == 2999){
            productID = "voltstudy_2999";
        }
        else if(price == 2699.1){
            productID = "voltstudy_2699.10";
        }
        else if(price == 3499){
            productID = "voltstudy_3499";
        }
        else if(price == 3149.1){
            productID = "voltstudy_3149.10";
        }
        else if(price == 3999){
            productID = "voltstudy_3999";
        }
        else if(price == 3599.1){
            productID = "voltstudy_3599.10";
        }
        else if(price == 4499){
            productID = "voltstudy_4499";
        }
        else if(price == 4049.1){
            productID = "voltstudy_4049.10";
        }
        else if(price == 4999){
            productID = "voltstudy_4999";
        }
        else if(price == 4499.1){
            productID = "voltstudy_4499.10";
        }
        else if(price == 5499){
            productID = "voltstudy_5499";
        }
        else if(price == 4949.1){
            productID = "voltstudy_4949.10";
        }
        else if(price == 5999){
            productID = "voltstudy_5999";
        }
        else if(price == 5399.1){
            productID = "voltstudy_5399.10";
        }
        else{
            productID = null;
        }
    }


    /* Getting Data From Firebase for Saved Courses */
    private void getFirebaseSavedCourseData() {
        FirebaseDatabase.getInstance().getReference().child("savedCourses").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            fetchSavedCourseData(singleSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void fetchSavedCourseData(DataSnapshot singleSnapshot) {
        SavedCourses savedCourses = singleSnapshot.getValue(SavedCourses.class);

        if (savedCourses != null) {
            savedCourseCodes.add(savedCourses.getCourseCode());
            checkSavedCourse();
        }
    }

    private void checkSavedCourse(){
        if(savedCourseCodes.contains(courseCode)) {
            saveButton.setImageResource(R.drawable.ic_like_filled);
            saveButton.setClickable(false);
            saveButton.setFocusable(false);
        }
    }



    /* Getting Data From Firebase for Purchased Courses */
    private void getFirebasePurchasedCourseData() {
        FirebaseDatabase.getInstance().getReference().child("purchasedCourses").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            fetchPurchasedCourseData(singleSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void fetchPurchasedCourseData(DataSnapshot singleSnapshot) {
        PurchasedCourses purchasedCourses = singleSnapshot.getValue(PurchasedCourses.class);

        if (purchasedCourses != null) {
            purchasedCourseCodes.add(purchasedCourses.getCourseCode());
            checkCoursePurchase();
        }
    }

    private void checkCoursePurchase(){
        if(purchasedCourseCodes.contains(courseCode)) {
            buyButton.setText("Purchased");
            buyButton.setEnabled(false);
            buyButton.setBackgroundColor(Color.GRAY);
        }
    }



    //In-app Billing Methods
    @Override
    public void onProductPurchased(String productID, TransactionDetails details) {
        PurchasedCourses purchasedCourses = new PurchasedCourses();
        purchasedCourses.setCourseCode(courseCode);
        purchasedCourses.setCourseName(courseName);
        purchasedCourses.setCourseAuthor(courseAuthor);
        purchasedCourses.setCourseLanguage(courseLanguage);
        purchasedCourses.setCourseCategory(courseCategory);
        purchasedCourses.setCourseSubCategory(courseSubCategory);
        purchasedCourses.setCourseImageUrl(courseImageUrl);
        purchasedCourses.setCourseType(courseType);
        purchasedCourses.setCoursePrice(coursePrice);
        purchasedCourses.setCourseAuthorID(courseAuthorID);
        purchasedCourses.setCoupon(isCoupon);
        DatabaseReference purchasedCourseRef  = rootRef.child("purchasedCourses").child(auth.getUid()).child(String.valueOf(purchasedCourseCodes.size()));
        purchasedCourseRef.setValue(purchasedCourses).addOnSuccessListener(aVoid -> {
            DatabaseReference mDatabase = rootRef.child("completed").child(auth.getUid()).child(courseCode);
            mDatabase.child("Video").setValue(1);
            mDatabase.child("text").setValue(1);
            Toasty.success(this, "Course Purchased Successfully", Toasty.LENGTH_LONG).show();
        });
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    //On Back Pressed
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

    private void getCouponCode(String courseID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("courses")
                .child(courseID)
                .child("promocode");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    code = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
