package com.vatsal.voltstudy.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.CategoriesListActivity;

import java.util.ArrayList;

/** Adapter For Category List Loaded in HomeActivity */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    // Model Variables
    private ArrayList<String> courseCategories;
    private Context mContext;


    public CategoriesAdapter(Context mContext, ArrayList<String> courseCategories){

        this.courseCategories = courseCategories;
        this.mContext = mContext;
    }


    // This method is used to create the view and inflate it with values
    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_categ, parent, false);

        return new CategoriesViewHolder(view);
    }


    // This method binds those controls inside the view to their respective controls
    @Override
    public void onBindViewHolder(@NonNull final CategoriesViewHolder holder, int position) {

        holder.mCourseCategory.setText(courseCategories.get(position));


        // onClickListener for every item in the RecyclerView
        holder.mParent.setOnClickListener(view -> launchCourse(courseCategories.get(holder.getAdapterPosition())));
    }


    // This method is used to launch a new Activity which displays the profile of the Course to the manager
    private void launchCourse(String course_code) {
        Intent intent = new Intent(mContext, CategoriesListActivity.class);
        intent.putExtra("courseCategories", course_code);
        mContext.startActivity(intent);
    }


    // This method returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return courseCategories.size();
    }



    public class CategoriesViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mCourseCategory;
        private CardView mParent;

        public CategoriesViewHolder(View itemView) {
            super(itemView);

            mCourseCategory = itemView.findViewById(R.id.txt_category_name);
            mParent = itemView.findViewById(R.id.cvCourseCategories);
        }
    }
    
}
