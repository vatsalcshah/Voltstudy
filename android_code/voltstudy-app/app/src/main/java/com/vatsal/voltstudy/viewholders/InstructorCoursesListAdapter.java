package com.vatsal.voltstudy.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.CourseContentActivity;

import java.util.ArrayList;

public class InstructorCoursesListAdapter extends RecyclerView.Adapter<InstructorCoursesListAdapter.InstructorCoursesViewHolder> {

    // Model Variables
    private ArrayList<String> courseName;
    private ArrayList<String> courseCode;
    private ArrayList<String> courseAuthor;
    private ArrayList<String> courseLanguage;
    private ArrayList<String> courseCategory;
    private ArrayList<String> courseSubCategory;
    private ArrayList<String> courseImageUrl;
    private ArrayList<String> courseType;
    private ArrayList<String> promoCode;
    private ArrayList<Double> coursePrice;

    private Context mContext;

    // Constructors
    public InstructorCoursesListAdapter(Context mContext, ArrayList<String> courseName, ArrayList<String> courseAuthor,
                                       ArrayList<String> courseLanguage, ArrayList<String> courseCode,
                                       ArrayList<String> courseCategory, ArrayList<String> courseSubCategory,
                                       ArrayList<String> courseImageUrl, ArrayList<String> courseType, ArrayList<Double> coursePrice, ArrayList<String> promoCode) {

        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseAuthor = courseAuthor;
        this.courseLanguage = courseLanguage;
        this.courseCategory = courseCategory;
        this.courseSubCategory = courseSubCategory;
        this.courseImageUrl = courseImageUrl;
        this.courseType = courseType;
        this.coursePrice = coursePrice;
        this.promoCode = promoCode;
        this.mContext = mContext;
    }

    // This method is used to create the view and inflate it with values
    @NonNull
    @Override
    public InstructorCoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instructor_course_list, parent, false);

        return new InstructorCoursesViewHolder(view);
    }

    // This method binds those controls inside the view to their respective controls
    @Override
    public void onBindViewHolder(@NonNull final InstructorCoursesViewHolder holder, int position) {

        holder.mCourseName.setText(courseName.get(position));
        holder.mCourseAuthor.setText(courseAuthor.get(position));
        holder.mCourseLanguage.setText(courseLanguage.get(position));
        holder.mCourseCategory.setText(courseCategory.get(position));
        holder.mCourseSubCategory.setText(courseSubCategory.get(position));
        holder.mCourseType.setText(courseType.get(position));
        holder.mCoursePrice.setText(String.valueOf(coursePrice.get(position)));
        holder.mCoursePriceHigh.setText(String.valueOf(coursePrice.get(position) + 1500));
        holder.mPromoCode.setText(promoCode.get(position));
        Glide.with(this.mContext)
                .load(courseImageUrl.get(position))
                .transforms(new CenterCrop())
                .override(400,400)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mCourseImageUrl);

        // onClickListener for every item in the RecyclerView
        holder.mParent.setOnClickListener(view ->
                launchCourse(courseName.get(holder.getAdapterPosition()), courseAuthor.get(holder.getAdapterPosition()), courseLanguage.get(holder.getAdapterPosition()), courseCategory.get(holder.getAdapterPosition()), courseSubCategory.get(holder.getAdapterPosition()), courseImageUrl.get(holder.getAdapterPosition()), courseCode.get(holder.getAdapterPosition()), coursePrice.get(holder.getAdapterPosition())));
    }

    // This method is used to launch a new Activity which displays the profile of the Course to the manager
    private void launchCourse(String course_name, String course_author, String course_language, String course_category, String course_subcategory,String course_img, String course_code, Double course_price) {
        Intent intent = new Intent(mContext, CourseContentActivity.class);
        intent.putExtra("courseName", course_name);
        intent.putExtra("courseAuthor", course_author);
        intent.putExtra("courseLanguage", course_language);
        intent.putExtra("courseCategory", course_category);
        intent.putExtra("courseSubCategory", course_subcategory);
        intent.putExtra("courseImageUrl", course_img);
        intent.putExtra("courseCode", course_code);
        intent.putExtra("coursePrice", course_price);
        mContext.startActivity(intent);
    }

    // This method returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return courseName.size();
    }

    public class InstructorCoursesViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mCourseName, mCourseAuthor, mCourseLanguage, mCourseCategory, mCourseSubCategory, mCourseType, mCoursePrice, mRupeeHighSymbol, mCoursePriceHigh, mPromoCode;
        private ImageView mCourseImageUrl;
        private RelativeLayout mParent;

        public InstructorCoursesViewHolder(View itemView) {
            super(itemView);

            mCourseName = itemView.findViewById(R.id.txt_course_name_instructor_list);
            mCourseAuthor = itemView.findViewById(R.id.txt_course_author_instructor_list);
            mCourseLanguage = itemView.findViewById(R.id.txt_course_lang_instructor_list);
            mCourseCategory = itemView.findViewById(R.id.txt_course_category_instructor_list);
            mCourseSubCategory = itemView.findViewById(R.id.txt_course_subcategory_instructor_list);
            mCourseImageUrl = itemView.findViewById(R.id.img_course_instructor_list);
            mCourseType = itemView.findViewById(R.id.txt_course_type_instructor_list);
            mCoursePrice = itemView.findViewById(R.id.txt_course_price_instructor_list);
            mRupeeHighSymbol = itemView.findViewById(R.id.rupee_high_instructor_logo);
            mRupeeHighSymbol.setPaintFlags(mRupeeHighSymbol.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            mCoursePriceHigh = itemView.findViewById(R.id.tvInstructorHighPrice);
            mCoursePriceHigh.setPaintFlags(mCoursePriceHigh.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mPromoCode = itemView.findViewById(R.id.tv_promocode);
            mParent = itemView.findViewById(R.id.cvInstructorList);
        }

    }

}