package com.vatsal.voltstudy.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.search_section.LanguagesListActivity;

import java.util.ArrayList;

/** Adapter for list of Languages in HomeActivity */

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.CourseLanguagesViewHolder> {

    // Model Variables
    private ArrayList<String> courseLangName;
    private Context mContext;


    public LanguagesAdapter(Context mContext, ArrayList<String> courseLangName){

        this.courseLangName = courseLangName;
        this.mContext = mContext;
    }


    // This method is used to create the view and inflate it with values
    @NonNull
    @Override
    public CourseLanguagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_lang, parent, false);

        return new CourseLanguagesViewHolder(view);
    }


    // This method binds those controls inside the view to their respective controls
    @Override
    public void onBindViewHolder(@NonNull final CourseLanguagesViewHolder holder, int position) {

        holder.mCoursesLangName.setText(courseLangName.get(position));

        // onClickListener for every item in the RecyclerView
        holder.mParent.setOnClickListener(view -> launchCourse(courseLangName.get(holder.getAdapterPosition())));
    }


    // This method is used to launch a new Activity which displays the profile of the Course to the manager
    private void launchCourse(String course_code) {
        Intent intent = new Intent(mContext, LanguagesListActivity.class);
        intent.putExtra("courseLangName", course_code);
        mContext.startActivity(intent);
    }


    // This method returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return courseLangName.size();
    }



    public class CourseLanguagesViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mCoursesLangName;
        private RelativeLayout mParent;

        public CourseLanguagesViewHolder(View itemView) {
            super(itemView);

            mCoursesLangName = itemView.findViewById(R.id.txt_courses_lang);
            mParent = itemView.findViewById(R.id.cvCoursesLanguage);
        }
    }


}
