package com.vatsal.voltstudy.create_course_section;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.models.Question;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/** Form for adding questions to quiz */

public class QuizFormActivity extends AppCompatActivity {

    EditText question;
    EditText aText;
    EditText bText;
    EditText cText;
    EditText dText;
    RadioButton aRadio;
    RadioButton bRadio;
    RadioButton cRadio;
    RadioButton dRadio;

    int currentQuestion = 1;
    int previousQuestion = 1;
    TextView questionNumber;

    ArrayList<Question> ques;
    JSONArray jsonArray;
    String selectedOption = "";

    AlertDialog alertDialog;
    String fileName = "file";
    private DatabaseReference myRef;
    Button fab,f2,fl;

    String courseCode, moduleID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        jsonArray = new JSONArray();
        setContentView(R.layout.activity_create_quiz);
        getDataOverIntent();
        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Quiz");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff333745));

        question = findViewById(R.id.questionView);
        aText =  findViewById(R.id.aText);
        bText =  findViewById(R.id.bText);
        cText =  findViewById(R.id.cText);
        dText =  findViewById(R.id.dText);
        questionNumber =  findViewById(R.id.questionNumber);
        aRadio =  findViewById(R.id.aRadio);
        bRadio =  findViewById(R.id.bRadio);
        cRadio =  findViewById(R.id.cRadio);
        dRadio =  findViewById(R.id.dRadio);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference();
        selectedOption = "";
        currentQuestion = 1;
        setListeners();

        ques = new ArrayList<>();

        alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        inflater.inflate(R.layout.dialog_custom, null);


        fab = findViewById(R.id.nextfab);
        fl = findViewById(R.id.fab2);//save button
        f2 = findViewById(R.id.pre_card);

        f2.setOnClickListener(v -> {

            if(previousQuestion>1) {
                previousQuestion--;
                setAllData(previousQuestion);
            }
            if(previousQuestion==1)
                f2.setVisibility(View.INVISIBLE);
                //Question question1 = new Question();
            Toast.makeText(QuizFormActivity.this, String.valueOf(previousQuestion), Toast.LENGTH_SHORT).show();
        });

        fab.setOnClickListener(v -> {

            if(previousQuestion!=currentQuestion) {
                previousQuestion++;
                if(previousQuestion!=currentQuestion)
                setAllData(previousQuestion);
                else {
                    clearAllData();
                    questionNumber.setText(String.valueOf(currentQuestion));
                }
                if(previousQuestion>1)
                    f2.setVisibility(View.VISIBLE);
            }
            boolean cont = getEnteredQuestionsValue();
            if (cont)
            {
                previousQuestion++;
                currentQuestion++;
                Toast.makeText(QuizFormActivity.this, "QUESTION " + currentQuestion, Toast.LENGTH_SHORT).show();
                questionNumber.setText(String.valueOf(currentQuestion));
                clearAllData();
                f2.setVisibility(View.VISIBLE);
            }
        });

        fl.setOnClickListener(v -> {
            if(jsonArray.length()!=0)
            {
                JSONObject tempObject = new JSONObject();
                // get dialog_custom.xml view
                LayoutInflater li = LayoutInflater.from(QuizFormActivity.this);
                @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog_custom, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        QuizFormActivity.this);

                // set dialog_custom.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText userInput =  promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                final EditText userTime = promptsView.findViewById(R.id.editTextDialogUserInput1);
                final String str = userTime.getText().toString();
                String temp2 = str;
                if(temp2 != null) {
                    temp2 = String.valueOf(jsonArray.length());
                }
                Log.d("TIMEON",userTime.getText().toString().trim());
                try {
                    tempObject.put("Questions",jsonArray);
                    final String TIME = userTime.getText().toString().trim();
                    tempObject.put("Time",Integer.parseInt(temp2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String jsonStr = tempObject.toString();
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    // get user input and set it to result
                                    // edit text
                                    if (str != null ) {
                                        //result.setText(userInput.getText());
                                        Map<String, Object> result = new Gson().fromJson(jsonStr, Map.class);
                                        fileName = userInput.getText().toString().trim();
                                        if (!TextUtils.isEmpty(fileName)) {
                                            myRef.child("tests").child(courseCode).child(moduleID).child(fileName).setValue(result).addOnSuccessListener(aVoid -> {
                                                Toasty.success(QuizFormActivity.this,"Quiz Has Been Added Successfully", Toasty.LENGTH_LONG).show();
                                                finish();
                                            });
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                (dialog, id) -> dialog.cancel());
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
            else
            {
                Toasty.error(getApplicationContext(),
                        "Incomplete Question format", Toasty.LENGTH_SHORT).show();
            }
        });

    }


    public void getDataOverIntent(){
        courseCode = getIntent().getExtras().getString("courseCode");
        moduleID = getIntent().getExtras().getString("ModuleID");

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(QuizFormActivity.this);
        builder.setMessage("You have to add a test after every section in course.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void updateData(int position) {
        Question question1 = new Question();
        question1 = ques.get(position-1);
    }

    public void setAllData(int position) {
        clearAllData();
        new Question();
        Question question1;
        question1 = ques.get(position-1);
        questionNumber.setText(String.valueOf(question1.getId()));
        question.setText(question1.getQuestion());
        aText.setText(question1.getOpt_A());
        bText.setText(question1.getOpt_B());
        cText.setText(question1.getOpt_C());
        dText.setText(question1.getOpt_D());
        switch (question1.getAnswer()){
            case "A":
                aRadio.setChecked(true);
                break;
            case "B":
                bRadio.setChecked(true);
                break;
            case "C":
                cRadio.setChecked(true);
                break;
            case "D":
                dRadio.setChecked(true);
                break;
        }
    }

    private void clearAllData() {

        aRadio.setChecked(false);
        bRadio.setChecked(false);
        cRadio.setChecked(false);
        dRadio.setChecked(false);
        aText.setText(null);
        bText.setText(null);
        cText.setText(null);
        dText.setText(null);
        question.setText(null);
        selectedOption = "";
    }

    private boolean getEnteredQuestionsValue() {

        boolean cont = false;
        if (TextUtils.isEmpty(question.getText().toString().trim())) {
            question.setError("Please fill in a question");
        }
        else if (TextUtils.isEmpty(aText.getText().toString().trim())) {
            aText.setError("Please fill in option A");
        }
        else if (TextUtils.isEmpty(bText.getText().toString().trim())) {
            bText.setError("Please fill in option B");
        }
        else if (TextUtils.isEmpty(cText.getText().toString().trim())) {
            cText.setError("Please fill in option C");
        }
        else if (TextUtils.isEmpty(dText.getText().toString().trim())) {
            dText.setError("Please fill in option D");
        }
        else if (selectedOption.equals("")) {
            Toasty.error(this, "Please select the correct answer", Toasty.LENGTH_SHORT).show();
        }
        else {
            Question quest = new Question();
            quest.setId(currentQuestion);
            quest.setQuestion(question.getText().toString());
            quest.setOpt_A(aText.getText().toString());
            quest.setOpt_B(bText.getText().toString());
            quest.setOpt_C(cText.getText().toString());
            quest.setOpt_D(dText.getText().toString());
            quest.setAnswer(selectedOption);
            ques.add(quest);
            cont = true;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("answer",selectedOption);
                jsonObject.put("opt_A",aText.getText().toString().trim());
                jsonObject.put("opt_B",bText.getText().toString().trim());
                jsonObject.put("opt_C",cText.getText().toString().trim());
                jsonObject.put("opt_D",dText.getText().toString().trim());
                jsonObject.put("question",question.getText().toString().trim());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);
        }
        return cont;
    }

    private void setListeners() {
        aRadio.setOnClickListener(v -> {
            selectedOption = "A";
            bRadio.setChecked(false);
            cRadio.setChecked(false);
            dRadio.setChecked(false);
        });
        bRadio.setOnClickListener(v -> {
            selectedOption = "B";
            aRadio.setChecked(false);
            cRadio.setChecked(false);
            dRadio.setChecked(false);
        });
        cRadio.setOnClickListener(v -> {
            selectedOption = "C";
            bRadio.setChecked(false);
            aRadio.setChecked(false);
            dRadio.setChecked(false);
        });
        dRadio.setOnClickListener(v -> {
            selectedOption = "D";
            bRadio.setChecked(false);
            cRadio.setChecked(false);
            aRadio.setChecked(false);
        });

    }
}
