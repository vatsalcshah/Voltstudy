package com.vatsal.voltstudy.home_section;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vatsal.voltstudy.BuildConfig;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.models.TextCourse;
import com.vatsal.voltstudy.viewholders.TextualCourseAdapter;
import com.vatsal.voltstudy.viewholders.VideoAdapter;
import com.vatsal.voltstudy.models.Video;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/** Activity where the list of modules of courses are displayed */

@SuppressWarnings("FieldCanBeLocal")
public class CourseContentActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private long value,certificateNumber;

    // Widgets
    private RecyclerView videoListRecyclerView, textListRecyclerView;
    private VideoAdapter videoListAdapter;
    private TextualCourseAdapter textListAdapter;
    private TextView videoTitle, textTitle;
    private ImageView videoImage, textImage;
    private CardView certificateCard;
    private double completedTexts, completedVideos;
    private double totalTexts, totalVideos;


    // Data Models
    private Video video;
    private TextCourse textCourse;

    // Model Variables
    private ArrayList<String> videoDownloadUrl = new ArrayList<>();
    private ArrayList<String> videoName = new ArrayList<>();
    private ArrayList<String> videoId = new ArrayList<>();
    private ArrayList<Double> videoNumber = new ArrayList<>();

    // Model Variables
    private ArrayList<String> textContent = new ArrayList<>();
    private ArrayList<String> textName = new ArrayList<>();
    private ArrayList<String> moduleId = new ArrayList<>();
    private ArrayList<Double> textNumber = new ArrayList<>();

    // Variables
    private String courseCode, courseName, courseAuthorName;
    String currentUserID;

    //Certificate
    Bitmap bmp, scaledBmp, bmp2, scaledBmp2, bmp3, scaledBmp3, bmp4, scaledBmp4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        fetchCourseCertificate();
        getDataOverIntent();

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        nrCompletedTexts();
        nrCompletedVideos();
        nrTotalTexts();
        nrTotalVideos();

        checkAuth();

        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle(courseName);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        videoTitle = findViewById(R.id.title_videos_content);
        textTitle = findViewById(R.id.tv_textual_content);
        videoImage = findViewById(R.id.iv_learn_course);
        textImage = findViewById(R.id.iv_text_content);

        certificateCard = findViewById(R.id.certificate_card);
        certificateCard.setOnClickListener(v -> {
            Log.e("completedVideos", String.valueOf(completedVideos));
            Log.e("totalVideos", String.valueOf(totalVideos));
            Log.e("completedTexts", String.valueOf(completedTexts));
            Log.e("totalTexts", String.valueOf(totalTexts));
            if(completedVideos>totalVideos && completedTexts>totalTexts) {
                createCertificate();
                addOne();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseContentActivity.this);
                builder.setMessage("Please complete all the video and textual chapters to proceed further.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> dialog.cancel())
                        //Set your icon here
                        .setTitle("Not Yet Unlocked!")
                        .setIcon(R.drawable.ic_lock);
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        bmp  = BitmapFactory.decodeResource(getResources(), R.drawable.img_certificate_border);
        scaledBmp = getResizedBitmap(bmp, 103, 320);

        bmp2  = BitmapFactory.decodeResource(getResources(), R.drawable.img_border_rotated);
        scaledBmp2 = getResizedBitmap(bmp2, 103, 320);

        bmp3  = BitmapFactory.decodeResource(getResources(), R.drawable.img_certi_footer);

        Drawable d = getResources().getDrawable(R.drawable.img_certificate_badge);
        bmp4 = drawableToBitmap(d);
        scaledBmp4 = getResizedBitmap(bmp4, 200, 277);


    }


    public void addOne() {
        myRef.child("certificateNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                value =(long) dataSnapshot.getValue();
                value = value + 1;
                dataSnapshot.getRef().setValue(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchCourseCertificate() {
        myRef.child("certificateNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                certificateNumber =(long) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createCertificate(){

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1020, 720, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        // border1
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.certificateBorder));
        paint.setStrokeWidth(5);
        canvas.drawRect(20, 20, canvas.getWidth() - 20, canvas.getHeight() - 20, paint);

        // border1
        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(getResources().getColor(R.color.certificateBorder));
        paint2.setStrokeWidth(5);
        Rect rect = new Rect(50, 50, canvas.getWidth() - 50, canvas.getHeight() - 50);
        canvas.drawRect(rect, paint2);
        int innerBorderWidth = rect.width();
        int innerBorderHeight = rect.height();

        // borderImg1
        Paint paint3 = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint3.setAntiAlias(true);
        canvas.drawBitmap(scaledBmp, canvas.getWidth() - 153, 50, paint3);

        // borderImg2
        Paint paint4 = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint4.setAntiAlias(true);
        canvas.drawBitmap(scaledBmp2, 50, innerBorderHeight-270, paint4);

        // badge
        Paint paint13 = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint13.setAntiAlias(true);
        canvas.drawBitmap(scaledBmp4, 85, 85, paint13);

        //CertificateText
        Paint paint5 = new Paint();
        paint5.setTextAlign(Paint.Align.CENTER);
        paint5.setTypeface(Typeface.DEFAULT_BOLD);
        paint5.setTextSize(58);
        paint5.setColor(getResources().getColor(R.color.certificateHighlightTextColor));
        canvas.drawText("CERTIFICATE", 520, 150, paint5);

        //ofCompletionText
        Paint paint6 = new Paint();
        paint6.setTextAlign(Paint.Align.CENTER);
        paint6.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint6.setColor(getResources().getColor(R.color.certificateHighlightTextColor));
        paint6.setTextSize(50);
        canvas.drawText("of  Completion", 520, 195, paint6);

        Paint paint7 = new Paint();
        paint7.setTextAlign(Paint.Align.CENTER);
        paint7.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint7.setTextSize(28);
        canvas.drawText("This Certificate is awarded to", 520, 295, paint7);

        Paint paint8 = new Paint();
        paint8.setTextAlign(Paint.Align.CENTER);
        paint8.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint8.setTextSize(28);
        canvas.drawText(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName()), 520, 340, paint8);

        Paint paint9 = new Paint();
        paint9.setTextAlign(Paint.Align.CENTER);
        paint9.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint9.setTextSize(24);
        canvas.drawText("By Trainer : "+courseAuthorName+" in voltstudy", 520, 420, paint9);

        Paint paint10 = new Paint();
        paint10.setTextAlign(Paint.Align.CENTER);
        paint10.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint10.setTextSize(18);
        canvas.drawText("for successfully completing online training coursework for", 520, 450, paint10);

        Paint paint11 = new Paint();
        paint11.setTextAlign(Paint.Align.CENTER);
        paint11.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint11.setTextSize(28);
        canvas.drawText(courseName, 520, 490, paint11);

        Paint paint12 = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint12.setAntiAlias(true);
        scaledBmp3 = getResizedBitmap(bmp3, innerBorderWidth-206, 170);
        canvas.drawBitmap(scaledBmp3, 160, 475, paint12);

        Paint paint14 = new Paint();
        paint14.setTextAlign(Paint.Align.CENTER);
        paint14.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint14.setTextSize(22);

        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);

        canvas.drawText(formattedDate, 250, 610, paint14);


        Paint paint15 = new Paint();
        paint15.setTextAlign(Paint.Align.CENTER);
        paint15.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint15.setTextSize(18);
        canvas.drawText("CERTIFICATE NO.: VoltStudy/TP/"+ formattedDate +"/"+ certificateNumber, 520, 690, paint15);


        pdfDocument.finishPage(page);

        File file = new File(Environment.getExternalStorageDirectory(), "/"+courseName + certificateNumber +"voltstudyCourseCertificate.pdf");

        try{

            pdfDocument.writeTo(new FileOutputStream(file));


            Uri filepath = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", file);

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(filepath,"application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(Intent.createChooser(intent, "Open File Using..."));



        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();

    }

    private void getDataOverIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            courseCode = Objects.requireNonNull(intent.getExtras()).getString("courseCode");
            courseName = Objects.requireNonNull(intent.getExtras()).getString("courseName");
            courseAuthorName = Objects.requireNonNull(intent.getExtras()).getString("courseAuthor");
        }
    }

    private void checkAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getFirebaseData();
        }
    }

    private void getFirebaseData() {
        FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_videos))
                .child(courseCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            videoDownloadUrl.clear();
                            videoName.clear();
                            videoId.clear();
                            videoNumber.clear();

                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                video = singleSnapshot.getValue(Video.class);

                                if (video != null) {
                                    fetchVideos(video);
                                }
                            }
                        } else {
                            videoTitle.setVisibility(View.GONE);
                            videoImage.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("textcourses")
                .child(courseCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            textContent.clear();
                            textName.clear();
                            moduleId.clear();
                            textNumber.clear();


                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                textCourse = singleSnapshot.getValue(TextCourse.class);

                                if (textCourse != null) {
                                    fetchTextCourse(textCourse);
                                }
                            }
                        } else {
                            textTitle.setVisibility(View.GONE);
                            textImage.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void fetchVideos(Video video) {
        videoDownloadUrl.add(video.getDownload_url());
        videoName.add(video.getName());
        videoId.add(video.getVideoID());
        videoNumber.add(video.getNumber());

        initializeVideoList();
    }

    private void fetchTextCourse(TextCourse textCourse) {

        textContent.add(textCourse.getTextcontent());
        textName.add(textCourse.getName());
        moduleId.add(textCourse.getTextCourseID());
        textNumber.add(textCourse.getNumber());
        initializeTextList();
    }


    private void initializeVideoList() {
        videoListRecyclerView = findViewById(R.id.rvVideoList);
        videoListAdapter = new VideoAdapter(videoDownloadUrl, videoName, videoNumber, videoId, courseCode,"isVideo", completedVideos, this);
        videoListRecyclerView.setAdapter(videoListAdapter);
        videoListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeTextList() {
        textListRecyclerView = findViewById(R.id.rv_textual_content);
        textListAdapter = new TextualCourseAdapter(textContent, textName, textNumber,moduleId, courseCode, "isText", completedTexts, this);
        textListRecyclerView.setAdapter(textListAdapter);
        textListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void nrCompletedTexts(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("completed")
                .child(currentUserID).child(courseCode).child("text");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long cmpTexts = (Long) snapshot.getValue();
                    completedTexts = cmpTexts.doubleValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nrCompletedVideos(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("completed")
                .child(currentUserID).child(courseCode).child("Video");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Long cmpVids = (Long) snapshot.getValue();
                    completedVideos = cmpVids.doubleValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void nrTotalVideos(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("videos")
                .child(courseCode);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    totalVideos = dataSnapshot.getChildrenCount();
                }
                else {
                    totalVideos = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void nrTotalTexts(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("textcourses")
                .child(courseCode);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    totalTexts = dataSnapshot.getChildrenCount();
                }
                else {
                    totalTexts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onResume(){
        super.onResume();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        fetchCourseCertificate();
        getDataOverIntent();

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        nrCompletedTexts();
        nrCompletedVideos();
        nrTotalTexts();
        nrTotalVideos();


        checkAuth();

    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(resizedBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return resizedBitmap;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }



}
