package com.vatsal.voltstudy.attempt_quiz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vatsal.voltstudy.notification_section.NotificationService;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.models.Question;
import com.vatsal.voltstudy.models.Test;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

/** AttemptTest Activity */

public class AttemptTest extends AppCompatActivity {

    ArrayList<Question> questions;
    String []answers;
    Toolbar toolbar;
    DiscreteScrollView scrollView;
    LinearLayout indexLayout;
    GridView quesGrid;
    ArrayList<String> list;
    ArrayList<String> arrayList;
    popGridAdapter popGrid;
    Button next,prev;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String TESTNAME, courseCode, moduleID, courseType;
    private Double moduleNumber;
    private int countPaused = 0;
    int flag_controller = 1;

    long timer;  // =((Test) getIntent().getExtras().get("Questions")).getTime()*60*1000;

    public String postUrl;
    public String chnameEncoded;

    CountDownTimer countDownTimer;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth= FirebaseAuth.getInstance();
        setContentView(R.layout.activity_attempt);

        getDataOverIntent();


        questions=((Test) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("Questions"))).getQuestions();
        TESTNAME = (String) getIntent().getExtras().get("TESTNAME");

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        answers=new String[questions.size()];
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        scrollView = findViewById(R.id.discrete);
        final QuestionAdapter questionAdapter=new QuestionAdapter(questions);
        scrollView.setAdapter(questionAdapter);

        next=findViewById(R.id.next);
        next.setOnClickListener(view -> {
            if(scrollView.getCurrentItem()==questions.size()-1){

                    showPopUp();

            }else {
                //setNextPrevButton(scrollView.getCurrentItem() + 1);
                scrollView.smoothScrollToPosition(scrollView.getCurrentItem() + 1);
            }
        });

        prev=findViewById(R.id.prev);
        prev.setOnClickListener(view -> {
            if(scrollView.getCurrentItem()!=0){
                //setNextPrevButton(scrollView.getCurrentItem()-1);
                scrollView.smoothScrollToPosition(scrollView.getCurrentItem()-1);
            }
        });

        setNextPrevButton(scrollView.getCurrentItem());
        indexLayout=findViewById(R.id.index_layout);
        indexLayout.setAlpha(.5f);
        quesGrid=findViewById(R.id.pop_grid);
        popGrid=new popGridAdapter(AttemptTest.this);
        quesGrid.setAdapter(popGrid);
        quesGrid.setOnItemClickListener((adapterView, view, i, l) -> {
            scrollView.smoothScrollToPosition(i+1);
            slideUp(indexLayout);
        });
        scrollView.addScrollListener((scrollPosition, currentPosition, newPosition, currentHolder, newCurrent) -> setNextPrevButton(newPosition));

        timer=((Test) Objects.requireNonNull(getIntent().getExtras().get("Questions"))).getTime()*60*1000;
    }

    private void getDataOverIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            courseCode = Objects.requireNonNull(intent.getExtras()).getString("courseCode");
            moduleID = Objects.requireNonNull(intent.getExtras()).getString("moduleID");
            courseType = Objects.requireNonNull(intent.getExtras()).getString("courseType");
            moduleNumber = Objects.requireNonNull(intent.getExtras()).getDouble("moduleNumber");
        }
    }

    void showPopUp(){
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AttemptTest.this);
            builder.setMessage("Do you want to submit?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                submit();
                Log.e("PopUpSubmit", "Popup");
//                setAlertDialog(answerText);
                dialogStart();
            });

            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            builder.show();
        }

    }

    /*submit result to database**/
    void submit(){
        flag_controller = 0;
        int score=0;
        list = new ArrayList<>();
        arrayList = new ArrayList<>();
        for(int i=0;i<answers.length;i++){
            if(answers[i]!=null&&answers[i].equals(questions.get(i).getAnswer())){
                score++;
            }
            String temp = (answers[i]!=null) ? answers[i]+") ":"null) ";

            list.add("Your choice ("+
                    temp +
                    "Right choice is("+ questions.get(i).getAnswer()+")");
            arrayList.add(questions.get(i).getQuestion());
        }

        try {
            mDatabase.child("Results").child(courseCode).child(moduleID).child(((Test) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("Questions"))).getName())
                    .child(Objects.requireNonNull(Objects.requireNonNull(auth.getUid()))).setValue(score).addOnSuccessListener(aVoid -> {
                        if(courseType.equals("isVideo")){
                            String chname = ((Test) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("Questions"))).getName();
                            try {
                                chnameEncoded = URLEncoder.encode(chname, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            double nextModuleNum = moduleNumber + 1;
                            postUrl = "https://yoururl.cloudfunctions.net/CompletedVideo?uid="+ auth.getUid() +"&cid="+courseCode+"&chid="+moduleID+"&chname="+chnameEncoded+"&mno="+nextModuleNum;
                            try {
                                postRequest(postUrl);
                                countDownTimer.cancel();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(courseType.equals("isText")){
                            String chname = ((Test) Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("Questions"))).getName();
                            try {
                                chnameEncoded = URLEncoder.encode(chname, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            double nextModuleNum = moduleNumber + 1;
                            postUrl = "https://yoururl.cloudfunctions.net/CompletedText?uid="+ auth.getUid() +"&cid="+courseCode+"&chid="+moduleID+"&chname="+chnameEncoded+"&mno="+nextModuleNum;
                            try {
                                postRequest(postUrl);
                                countDownTimer.cancel();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        }catch (Exception e){
            Log.e("Result Update Failed " ,e.getMessage());
        }
    }

    void dialogStart() {
        if (!isFinishing()) {
            final AlertDialog.Builder builderSingle = new AlertDialog.Builder(AttemptTest.this);
            builderSingle.setIcon(R.mipmap.ic_launcher);
            builderSingle.setTitle(TESTNAME + " Answers");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                    (AttemptTest.this, android.R.layout.select_dialog_singlechoice);
            final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>
                    (AttemptTest.this, android.R.layout.select_dialog_singlechoice);

            for (String y : arrayList) {
                arrayAdapter1.add(y);
            }
            for (String x : list) {
                arrayAdapter.add(x);
            }

            builderSingle.setCancelable(false);
            builderSingle.setNegativeButton("Done!", (dialog, which) -> {
                finish();
                dialog.dismiss();
            });

            builderSingle.setAdapter(arrayAdapter1, (dialog, which) -> {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(AttemptTest.this);
                builderInner.setMessage(strName);
                builderInner.setCancelable(false);
                builderInner.setTitle("Your Selected Question Answer is");
                builderInner.setPositiveButton("Ok", (dialog1, which1) -> {
//                        finish();
                    builderSingle.show();
//                        dialog.dismiss();
                });
                builderInner.show();
            });
            builderSingle.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(countPaused<=2 && countPaused >=0 && flag_controller == 1)
            startService(new Intent(AttemptTest.this,
                    NotificationService.class));
        countPaused++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(AttemptTest.this, NotificationService.class));
        if(countPaused>2) {
            Toasty.success(AttemptTest.this,"Thank you! Your response has been submitted.",
                    Toasty.LENGTH_SHORT).show();
            countPaused = -1000;
            submit();
            Log.e("Resume", "Resume");
                dialogStart();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stopService(new Intent(AttemptTest.this, NotificationService.class));
}

    @SuppressLint("SetTextI18n")
    void setNextPrevButton(int pos){
        if(pos==0){
//            prev.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            prev.setText("");
        }else {
//            prev.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            prev.setText("Previous");
        }
        if(pos==questions.size()-1){
            next.setText("Submit");
//            next.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }else {
            next.setText("Next");
//            next.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onBackPressed() {
            showPopUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.attempt_menu, menu);
        final MenuItem  counter = menu.findItem(R.id.counter);

        countDownTimer=new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
                long hr=TimeUnit.MILLISECONDS.toHours(millisUntilFinished),mn=(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)-
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))),
                        sc=TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));


                String  hms =format(hr)+":"+format(mn)+":"+format(sc) ;
                counter.setTitle(hms);
                timer = millisUntilFinished;
            }
            String format(long n){
                if(n<10)
                    return "0"+n;
                else return ""+n;
            }

            public void onFinish() {
                    submit();
                    Log.e("Finish", "Finish");
                    dialogStart();
            }

        }.start();

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.submit){
                showPopUp();

                return true;
        }else if(id==R.id.info){
            togglePopUp();
        }
        return super.onOptionsItemSelected(item);
    }


    void togglePopUp(){
        if(indexLayout.getVisibility()==View.GONE){
            slideDown(indexLayout);
        }else slideUp(indexLayout);
    }

    class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

        private int itemHeight;
        private ArrayList<Question> data;

        QuestionAdapter(ArrayList<Question> data) {
            this.data = data;
        }


        @Override
        public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            Activity context = (Activity) recyclerView.getContext();
            Point windowDimensions = new Point();
            context.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
            itemHeight = Math.round(windowDimensions.y * 0.6f);
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.frag_test, parent, false);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    itemHeight);
            v.setLayoutParams(params);
            return new ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.questionText.setText(data.get(position).getQuestion());
            holder.r1.setText(data.get(position).getOpt_A());
            holder.r2.setText(data.get(position).getOpt_B());
            holder.r3.setText(data.get(position).getOpt_C());
            holder.r4.setText(data.get(position).getOpt_D());
            holder.r5.setText("Clear Selected");

            holder.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                final int selectedId = holder.radioGroup.getCheckedRadioButtonId();
                if(i==R.id.radioButton){
                    answers[position]="A";
                }  else if(i==R.id.radioButton2){
                    answers[position]="B";
                }else if(i==R.id.radioButton3){
                    answers[position]="C";
                }else if(i==R.id.radioButton4){
                    answers[position]="D";
                }
                else if(i==R.id.radioButton5) {
                    holder.radioGroup.clearCheck();
                    answers[position] = null;
                }
                popGrid.notifyDataSetChanged();
            });


            if(answers[position]==null) {
                holder.radioGroup.clearCheck();
            }else if(answers[position].equals("A")) {
                holder.radioGroup.check(R.id.radioButton);
            }else if(answers[position].equals("B")) {
                holder.radioGroup.check(R.id.radioButton2);
            }else if(answers[position].equals("C")) {
                holder.radioGroup.check(R.id.radioButton3);
            }else if(answers[position].equals("D")) {
                holder.radioGroup.check(R.id.radioButton4);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private View overlay;
            private  TextView questionText;
            private RadioGroup radioGroup;
            private RadioButton r1,r2,r3,r4,r5;

            ViewHolder(View itemView) {
                super(itemView);
                questionText =  itemView.findViewById(R.id.questionTextView);
                radioGroup=itemView.findViewById(R.id.radioGroup);
                r1=itemView.findViewById(R.id.radioButton);
                r2=itemView.findViewById(R.id.radioButton2);
                r3=itemView.findViewById(R.id.radioButton3);
                r4=itemView.findViewById(R.id.radioButton4);
                r5 = itemView.findViewById(R.id.radioButton5);
            }

            public void setOverlayColor(@ColorInt int color) {
                overlay.setBackgroundColor(color);
            }

            public void unCheck() {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton) {
                    r1.setChecked(true);
                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton2) {
                    r2.setChecked(true);
                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton3) {
                    r3.setChecked(true);
                }
                else if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton4) {
                    r4.setChecked(true);
                }
                else if(radioGroup.getCheckedRadioButtonId() ==R.id.radioButton5) {
                    r5.setChecked(true);
                }
            }
        }
    }

    class popGridAdapter extends BaseAdapter{
        Context mContext;
        popGridAdapter(Context context){
            mContext=context;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getCount() {
            return questions.size();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View convertView;
            if(view==null){
                convertView=new Button(mContext);
            }else convertView=view;
            if(answers[i]==null)
                (convertView).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            else
                (convertView).setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

            ((Button)convertView).setText(""+(i+1));

            (convertView).setOnClickListener(view1 -> {
                //setNextPrevButton(i);
                scrollView.smoothScrollToPosition(i);
            });
            return convertView;
        }
    }

    public void slideUp(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
               -view.getHeight());                // toYDelta
        animate.setDuration(500);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -view.getHeight(),                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        view.startAnimation(animate);

    }



    public void onStop() {
        countDownTimer.cancel();
        super.onStop();
    }



    void postRequest(String postUrl) throws IOException {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(postUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("HttpError", e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.d("HttpResponse",response.body().string());
            }

        });
    }


}

