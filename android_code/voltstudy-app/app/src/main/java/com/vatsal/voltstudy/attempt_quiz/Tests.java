package com.vatsal.voltstudy.attempt_quiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.vatsal.voltstudy.notification_section.NotificationService;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.models.Question;
import com.vatsal.voltstudy.models.Test;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Main Test Activity */

public class Tests extends AppCompatActivity {
    private DatabaseReference myRef;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private TestAdapter testAdapter;
    private int lastPos = -1;

    ArrayList<Test> tests=new ArrayList<>();

    private String courseCode, moduleID, courseType;
    private Double moduleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);

        getDataOverIntent();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        setSupportActionBar(toolbar);
        avLoadingIndicatorView = findViewById(R.id.loader1);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        avLoadingIndicatorView.show();
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference();
        ListView listView = findViewById(R.id.test_listview);
        testAdapter=new TestAdapter(Tests.this,tests);
        listView.setAdapter(testAdapter);
        getQues();

    }

    private void getDataOverIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            courseCode = Objects.requireNonNull(intent.getExtras()).getString("courseCode");
            moduleID = Objects.requireNonNull(intent.getExtras()).getString("moduleID");
            courseType = Objects.requireNonNull(intent.getExtras()).getString("courseType");
            moduleNumber= Objects.requireNonNull(intent.getExtras()).getDouble("moduleNumber");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stopService(new Intent(Tests.this, NotificationService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getQues(){
        //addListenerForSingleValueEvent
        myRef.child("tests").child(courseCode).child(moduleID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tests.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Test t = new Test();
                        t.setName(snapshot.getKey());
                        t.setTime(Long.parseLong(Objects.requireNonNull(snapshot.child("Time").getValue()).toString()));
                        ArrayList<Question> ques = new ArrayList<>();
                        for (DataSnapshot qSnap : snapshot.child("Questions").getChildren()) {
                            ques.add(qSnap.getValue(Question.class));
                        }
                        t.setQuestions(ques);
                        tests.add(t);

                    }
                    testAdapter.dataList = tests;
                    testAdapter.notifyDataSetChanged();
                    avLoadingIndicatorView.setVisibility(View.GONE);
                    avLoadingIndicatorView.hide();
                    Log.e("The read success: ", "su" + tests.size());
                }
                else{
                    Toasty.warning(Tests.this, "Quiz not provided by Instructor", Toasty.LENGTH_LONG).show();
                    avLoadingIndicatorView.setVisibility(View.GONE);
                    avLoadingIndicatorView.hide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                avLoadingIndicatorView.setVisibility(View.GONE);
                avLoadingIndicatorView.hide();
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }

    class TestAdapter extends ArrayAdapter<Test> implements Filterable {
        private Context mContext;
        ArrayList<Test> dataList;
        TestAdapter(Context context, ArrayList<Test> list) {
            super(context, 0 , list);
            mContext = context;
            dataList = list;
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.item_test,parent,false);

            ((ImageView)listItem.findViewById(R.id.item_imageView)).
                    setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.tests_color));

            ((TextView)listItem.findViewById(R.id.item_textView))
                    .setText(dataList.get(position).getName()+" : "+dataList.get(position).getTime()+"Min");

            ((Button)listItem.findViewById(R.id.item_button)).setText("Attempt");

            (listItem.findViewById(R.id.item_button)).setOnClickListener(view -> {
                Intent intent=new Intent(mContext, AttemptTest.class);
                intent.putExtra("Questions",dataList.get(position));
                intent.putExtra("TESTNAME",dataList.get(position).getName());
                intent.putExtra("courseCode",courseCode);
                intent.putExtra("courseType", courseType);
                intent.putExtra("moduleID",moduleID);
                intent.putExtra("moduleNumber",moduleNumber);
                startActivity(intent);
            });

            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    (position > lastPos) ? R.anim.up_from_bottom : R.anim.down_from_top);
            (listItem).startAnimation(animation);
            lastPos = position;

            return listItem;
        }
    }



}
