package com.vatsal.voltstudy.viewholders;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.attempt_quiz.Tests;
import com.vatsal.voltstudy.home_section.TextContentActivity;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class TextualCourseAdapter extends RecyclerView.Adapter<TextualCourseAdapter.TextualCourseViewHolder> {

    // Model Variables
    private ArrayList<String> textContent;
    private ArrayList<String> name;
    private ArrayList<Double> number;
    private ArrayList<String> moduleID;
    private String courseCode, courseType;
    private Context mContext;
    private double completedTexts;

    // Constructors
    public TextualCourseAdapter(ArrayList<String> textContent, ArrayList<String> name,
                        ArrayList<Double> number, ArrayList<String> moduleID,
                        String courseCode, String courseType,double completedTexts,
                        Context mContext) {
        this.textContent = textContent;
        this.name = name;
        this.number = number;
        this.moduleID = moduleID;
        this.courseCode = courseCode;
        this.courseType = courseType;
        this.completedTexts = completedTexts;
        this.mContext = mContext;
    }

    // This method is used to create the view and inflate it with values
    @NonNull
    @Override
    public TextualCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_textual_course, parent, false);

        return new TextualCourseViewHolder(view);
    }

    // This method binds those controls inside the view to their respective controls
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final TextualCourseViewHolder holder, int position) {

        holder.mName.setText(name.get(position));
        holder.mNumber.setText(number.get(position).toString());
        double currentNumber = Double.parseDouble(number.get(position).toString());

        // onClickListener for every item in the RecyclerView
        holder.mParent.setOnClickListener(view -> {
            Log.e("currNum", String.valueOf(currentNumber));
            Log.e("completedTexts", String.valueOf(completedTexts));
                if(currentNumber <= completedTexts){
                    launchVideo(textContent.get(holder.getAdapterPosition()), name.get(holder.getAdapterPosition()), number.get(holder.getAdapterPosition()));
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Please complete the quiz for previous chapter & get more than 50% questions right to progress further.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, id) -> dialog.cancel())
                            //Set your icon here
                            .setTitle("Not Yet Unlocked!")
                            .setIcon(R.drawable.ic_lock);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                });

        holder.mQuizButton.setOnClickListener(v -> {
            if(currentNumber <= completedTexts){
                launchQuiz(moduleID.get(holder.getAdapterPosition()), courseCode, courseType, number.get(holder.getAdapterPosition()));
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Please complete the quiz for previous chapter & get more than 50% questions right to progress further.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> dialog.cancel())
                        //Set your icon here
                        .setTitle("Not Yet Unlocked!")
                        .setIcon(R.drawable.ic_lock);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    // This method is used to launch a new Activity which displays the profile of the Course to the manager
    private void launchVideo(String url, String title, Double num) {
        Intent intent = new Intent(mContext, TextContentActivity.class);
        intent.putExtra("textContent", url);
        intent.putExtra("textTitle", title);
        intent.putExtra("textNumber", num);
        mContext.startActivity(intent);
    }

    private void launchQuiz(String moduleID, String courseCode, String courseType, Double moduleNumber) {
        Intent intent = new Intent(mContext, Tests.class);
        intent.putExtra("moduleID", moduleID);
        intent.putExtra("courseCode", courseCode);
        intent.putExtra("courseType", courseType);
        intent.putExtra("moduleNumber", moduleNumber);
        mContext.startActivity(intent);
    }

    // This method returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return name.size();
    }

    static class TextualCourseViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mNumber, mName;
        private CardView mParent;
        private LinearLayout mQuizButton;

        TextualCourseViewHolder(View itemView) {
            super(itemView);

            mNumber = itemView.findViewById(R.id.tvTextualCourseNumber);
            mName = itemView.findViewById(R.id.tvTextualCourseName);
            mParent = itemView.findViewById(R.id.cvTextualCourse);
            mQuizButton = itemView.findViewById(R.id.layout_text_quiz_button);
        }

    }

}