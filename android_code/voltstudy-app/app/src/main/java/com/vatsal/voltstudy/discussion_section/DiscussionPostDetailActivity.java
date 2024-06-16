package com.vatsal.voltstudy.discussion_section;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.viewholders.DiscussionAnswerAdapter;
import com.vatsal.voltstudy.models.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Discussion Post Activity */

public class DiscussionPostDetailActivity extends AppCompatActivity {

    ImageView imgPost, imgCurrentUser, imgUserPost;
    TextView txtPostDesc, txtPostDateName, txtPostTitle, txtPostUserName;
    EditText editTextComment;
    Button btnAddComment;
    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    RecyclerView rvComment;
    LinearLayoutManager mLayoutManager;
    DiscussionAnswerAdapter discussionAnswerAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment" ;
    String uimg = null;
    String postUserId, userPostKey;
    LinearLayout deleteQuestionLayout;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_post_detail);
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // ini Views
        rvComment = findViewById(R.id.rv_comment);
        imgPost =findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);

        txtPostTitle = findViewById(R.id.post_detail_title);
        txtPostDesc = findViewById(R.id.post_detail_desc);
        txtPostDateName = findViewById(R.id.post_detail_date_name);
        txtPostUserName = findViewById(R.id.post_detail_username);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        deleteQuestionLayout = findViewById(R.id.delete_question_layout);
        deleteQuestionLayout.setVisibility(View.GONE);
        postUserId = getIntent().getExtras().getString("postUserId");

        if(firebaseUser.getUid().equals(postUserId)){
            deleteQuestionLayout.setVisibility(View.VISIBLE);
            deleteQuestionLayout.setOnClickListener(v -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiscussionPostDetailActivity.this);
                alertDialogBuilder.setMessage("Are you sure you want to remove this question?");
                alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {

                    userPostKey = getIntent().getExtras().getString("postKey");

                    DatabaseReference postRef = firebaseDatabase.getReference().child("Posts");
                    postRef.child(userPostKey).removeValue().addOnSuccessListener(aVoid -> {
                        DatabaseReference commentRef = firebaseDatabase.getReference().child("Comment");
                        commentRef.child(userPostKey).removeValue().addOnSuccessListener(aVoid1 -> {
                            Toasty.success(this, "Question has been removed successfully", Toasty.LENGTH_LONG).show();
                            finish();
                        }).addOnFailureListener(e -> {
                            Toasty.error(this, Objects.requireNonNull(e.getMessage()), Toasty.LENGTH_LONG).show();
                        });
                    }).addOnFailureListener(e -> {
                        Toasty.error(this, Objects.requireNonNull(e.getMessage()), Toasty.LENGTH_LONG).show();
                    });


                }).setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            });
        }







        // add Comment button click listener
        btnAddComment.setOnClickListener(view -> {

            btnAddComment.setVisibility(View.INVISIBLE);
            DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
            String commentId = commentReference.getKey();
            String comment_content = editTextComment.getText().toString();
            String uid = firebaseUser.getUid();
            String uname = firebaseUser.getDisplayName();

            Uri userImageUri = firebaseUser.getPhotoUrl();
            if(userImageUri != null) {
                uimg = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString();
            }

            Comment comment = new Comment(comment_content,uid,uimg,uname,commentId);
            commentReference.setValue(comment).addOnSuccessListener(aVoid -> {
                showMessage("comment added");
                editTextComment.setText("");
                btnAddComment.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> showMessage("fail to add comment : "+e.getMessage()));

        });


        // now we need to bind all data into those views
        // firt we need to get post data
        // we need to send post detail data to this activity first ...
        // now we can get post data

        String postImage = Objects.requireNonNull(getIntent().getExtras()).getString("postImage") ;
        if(postImage != null){
        Glide.with(this).load(postImage).into(imgPost);
        imgPost.setVisibility(View.VISIBLE);
        }

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String userpostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this)
                .load(userpostImage)
                .placeholder(R.drawable.ic_user_place_holder)
                .circleCrop()
                .into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);

        String postUserName = getIntent().getExtras().getString("userName");
        txtPostUserName.setText(postUserName);


        // set comment user image
        Glide.with(this)
                .load(firebaseUser.getPhotoUrl())
                .placeholder(R.drawable.ic_user_place_holder)
                .circleCrop()
                .into(imgCurrentUser);

        // get post id
        PostKey = getIntent().getExtras().getString("postKey");

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);


        // ini Recyclerview Comment
        iniRvComment();

        editTextComment.setOnTouchListener((v, event) -> {
            if (editTextComment.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });
    }

    private void iniRvComment() {

        mLayoutManager = new LinearLayoutManager(DiscussionPostDetailActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        rvComment.setLayoutManager(mLayoutManager);

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        String firebaseUserId = firebaseUser.getUid();
        commentRef.orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment) ;

                }

                discussionAnswerAdapter = new DiscussionAnswerAdapter(getApplicationContext(),listComment, firebaseUserId);
                rvComment.setAdapter(discussionAnswerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }


    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        return DateFormat.format("hh:mm a  MMM dd, yyyy",calendar).toString();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, DiscussionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DiscussionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}

