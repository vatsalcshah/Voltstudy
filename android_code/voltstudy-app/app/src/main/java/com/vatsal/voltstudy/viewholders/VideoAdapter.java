package com.vatsal.voltstudy.viewholders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.vatsal.voltstudy.home_section.VideoActivity;

import java.util.ArrayList;


@SuppressWarnings("FieldCanBeLocal")
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    // Model Variables
    private ArrayList<String> downloadURL;
    private ArrayList<String> name;
    private ArrayList<Double> number;
    private ArrayList<String> videoID;
    private String courseCode;
    private String courseType;
    private String userID;
    private double completedVideos;
    private Context mContext;

    // Constructors
    public VideoAdapter(ArrayList<String> downloadURL, ArrayList<String> name,
                        ArrayList<Double> number, ArrayList<String> videoID,
                        String courseCode, String courseType, double completedVideos,
                        Context mContext) {
        this.downloadURL = downloadURL;
        this.name = name;
        this.number = number;
        this.videoID = videoID;
        this.courseCode = courseCode;
        this.courseType = courseType;
        this.completedVideos = completedVideos;
        this.mContext = mContext;
    }

    // This method is used to create the view and inflate it with values
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_videos, parent, false);

        return new VideoViewHolder(view);
    }

    // This method binds those controls inside the view to their respective controls
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder holder, int position) {

        holder.mName.setText(name.get(position));
        holder.mNumber.setText(number.get(position).toString());
        double currentNumber = Double.parseDouble(number.get(position).toString());


        // onClickListener for every item in the RecyclerView
        holder.mParent.setOnClickListener(view -> {
            if(currentNumber <= completedVideos){
                launchVideo(downloadURL.get(holder.getAdapterPosition()), name.get(holder.getAdapterPosition()));
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
            if(currentNumber <= completedVideos){
                launchQuiz(videoID.get(holder.getAdapterPosition()), courseCode, courseType, number.get(holder.getAdapterPosition()));
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
    private void launchVideo(String url, String name) {
        Intent intent = new Intent(mContext, VideoActivity.class);
        intent.putExtra("downloadURL", url);
        intent.putExtra("videoName", name);
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

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mNumber, mName;
        private CardView mParent;
        private LinearLayout mQuizButton;

        VideoViewHolder(View itemView) {
            super(itemView);

            mNumber = itemView.findViewById(R.id.tvVideoNumber);
            mName = itemView.findViewById(R.id.tvVideoName);
            mParent = itemView.findViewById(R.id.cvVideos);
            mQuizButton = itemView.findViewById(R.id.layout_videos_quiz_button);
        }

    }

}
